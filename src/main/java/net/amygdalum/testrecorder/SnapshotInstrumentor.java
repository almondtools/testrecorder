package net.amygdalum.testrecorder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.asm.ByteCode.classFrom;
import static net.amygdalum.testrecorder.asm.ByteCode.isNative;
import static net.amygdalum.testrecorder.asm.ByteCode.returnsResult;
import static org.objectweb.asm.Opcodes.ACC_ANNOTATION;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.amygdalum.testrecorder.asm.Assign;
import net.amygdalum.testrecorder.asm.ByteCode;
import net.amygdalum.testrecorder.asm.GetStackTrace;
import net.amygdalum.testrecorder.asm.GetStatic;
import net.amygdalum.testrecorder.asm.GetThisOrNull;
import net.amygdalum.testrecorder.asm.InvokeVirtual;
import net.amygdalum.testrecorder.asm.Ldc;
import net.amygdalum.testrecorder.asm.MemoizeBoxed;
import net.amygdalum.testrecorder.asm.MethodContext;
import net.amygdalum.testrecorder.asm.Nop;
import net.amygdalum.testrecorder.asm.Recall;
import net.amygdalum.testrecorder.asm.Sequence;
import net.amygdalum.testrecorder.asm.SequenceInstruction;
import net.amygdalum.testrecorder.asm.WrapArgumentTypes;
import net.amygdalum.testrecorder.asm.WrapArguments;
import net.amygdalum.testrecorder.asm.WrapMethod;
import net.amygdalum.testrecorder.asm.WrapResultType;
import net.amygdalum.testrecorder.asm.WrapWithTryCatch;
import net.amygdalum.testrecorder.profile.Classes;
import net.amygdalum.testrecorder.profile.Fields;
import net.amygdalum.testrecorder.profile.Methods;
import net.amygdalum.testrecorder.profile.SerializationProfile.Global;
import net.amygdalum.testrecorder.profile.SerializationProfile.Input;
import net.amygdalum.testrecorder.profile.SerializationProfile.Output;
import net.amygdalum.testrecorder.util.AttachableClassFileTransformer;

public class SnapshotInstrumentor extends AttachableClassFileTransformer implements ClassFileTransformer {

	private TestRecorderAgentConfig config;
	private Map<String, ClassNode> classCache;
	private Set<String> instrumentedClassNames;
	private Set<Class<?>> instrumentedClasses;

	public SnapshotInstrumentor(TestRecorderAgentConfig config) {
		this.config = config;
		this.classCache = new HashMap<>();
		this.instrumentedClassNames = new LinkedHashSet<>();
		this.instrumentedClasses = new LinkedHashSet<>();
		SnapshotManager.init(config);
	}

	@Override
	public Class<?>[] classesToRetransform() {
		Set<Class<?>> classesToRetransform = new LinkedHashSet<>();
		classesToRetransform.addAll(instrumentedClasses);

		for (String className : instrumentedClassNames) {
			classesToRetransform.add(classFrom(className));
		}

		return classesToRetransform.toArray(new Class[0]);
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		try {
			if (className == null) {
				return null;
			}
			for (Classes clazz : config.getClasses()) {
				if (clazz.matches(className)) {
					System.out.println("recording snapshots of " + className);
					byte[] instrument = instrument(classfileBuffer);
					if (classBeingRedefined != null) {
						instrumentedClasses.add(classBeingRedefined);
					} else {
						instrumentedClassNames.add(className);
					}

					return instrument;
				}
			}
			return null;
		} catch (Throwable e) {
			System.err.println("exception occured while preparing recording of snapshots: " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}

	}

	private ClassNode fetchClassNode(String className) throws IOException {
		ClassNode classNode = classCache.get(className);
		if (classNode == null) {
			ClassReader cr = new ClassReader(className);
			classNode = new ClassNode();

			cr.accept(classNode, 0);
			classCache.put(className, classNode);
		}
		return classNode;
	}

	private ClassNode fetchClassNode(byte[] buffer) {
		ClassReader cr = new ClassReader(buffer);
		ClassNode classNode = new ClassNode();

		cr.accept(classNode, 0);
		classCache.put(classNode.name, classNode);
		return classNode;
	}

	private MethodNode fetchMethodNode(ClassNode classNode, String methodName, String methodDesc) throws IOException, NoSuchMethodException {
		ClassNode currentClassNode = classNode;
		Set<String> interfaces = new LinkedHashSet<>();
		while (currentClassNode != null) {
			for (MethodNode method : currentClassNode.methods) {
				if (method.name.equals(methodName) && method.desc.equals(methodDesc)) {
					return method;
				}
			}
			interfaces.addAll(currentClassNode.interfaces);
			currentClassNode = currentClassNode.superName == null ? null : fetchClassNode(currentClassNode.superName);
		}
		for (String interfaceName : interfaces) {
			currentClassNode = fetchClassNode(interfaceName);
			for (MethodNode method : currentClassNode.methods) {
				if (method.name.equals(methodName) && method.desc.equals(methodDesc)) {
					return method;
				}
			}
		}
		return null;
	}

	public byte[] instrument(String className) throws IOException {
		return instrument(fetchClassNode(className));
	}

	public byte[] instrument(byte[] buffer) {
		return instrument(fetchClassNode(buffer));
	}

	public byte[] instrument(ClassNode classNode) {
		if (isClass(classNode)) {

			logSkippedSnapshotMethods(classNode);

			instrumentStaticInitializer(classNode);

			instrumentSnapshotMethods(classNode);

			instrumentInputMethods(classNode);

			instrumentOutputMethods(classNode);

			instrumentInputCalls(classNode);

			instrumentOutputCalls(classNode);
		}

		ClassWriter out = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(out);
		return out.toByteArray();
	}

	private boolean isClass(ClassNode classNode) {
		return (classNode.access & (ACC_INTERFACE | ACC_ANNOTATION)) == 0;
	}

	private void logSkippedSnapshotMethods(ClassNode classNode) {
		for (MethodNode methodNode : getSkippedSnapshotMethods(classNode)) {
			System.err.println("method " + Type.getMethodType(methodNode.desc).getDescriptor() + " in " + Type.getType(classNode.name) + " is not accessible, skipping");
		}
	}

	private void instrumentStaticInitializer(ClassNode classNode) {
		for (MethodNode methodNode : getSnapshotMethods(classNode)) {
			SnapshotManager.MANAGER.registerRecordedMethod(keySignature(classNode, methodNode), classNode.name, methodNode.name, methodNode.desc);
		}
		for (FieldNode fieldNode : getGlobalFields(classNode)) {
			SnapshotManager.MANAGER.registerGlobal(classNode.name, fieldNode.name);
		}
	}

	private void instrumentSnapshotMethods(ClassNode classNode) {
		for (MethodNode method : getSnapshotMethods(classNode)) {
			instrumentSnapshotMethod(classNode, method);
		}
	}

	protected void instrumentSnapshotMethod(ClassNode classNode, MethodNode methodNode) {
		methodNode.instructions = new WrapWithTryCatch(methodNode)
			.before(setupVariables(classNode, methodNode))
			.after(expectVariables(classNode, methodNode))
			.handler(throwVariables(classNode, methodNode))
			.build(new MethodContext(classNode, methodNode));
	}

	private void instrumentInputMethods(ClassNode classNode) {
		for (MethodNode method : getJavaInputMethods(classNode)) {
			instrumentInputMethod(classNode, method);
		}
	}

	protected void instrumentInputMethod(ClassNode classNode, MethodNode methodNode) {
		methodNode.instructions = new WrapMethod()
			.prepend(inputVariables(classNode, methodNode))
			.append(inputArgumentsAndResult(classNode, methodNode))
			.build(new MethodContext(classNode, methodNode));
	}

	protected SequenceInstruction inputVariables(ClassNode classNode, MethodNode methodNode) {
		return new Assign("inputId", Type.INT_TYPE)
			.value(new InvokeVirtual(SnapshotManager.class, "inputVariables", StackTraceElement[].class, Object.class, String.class, java.lang.reflect.Type.class, java.lang.reflect.Type[].class)
				.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
				.withArgument(0, new GetStackTrace())
				.withArgument(1, new GetThisOrNull())
				.withArgument(2, new Ldc(methodNode.name))
				.withArgument(3, new WrapResultType())
				.withArgument(4, new WrapArgumentTypes()));
	}

	protected SequenceInstruction inputArgumentsAndResult(ClassNode classNode, MethodNode methodNode) {
		if (returnsResult(methodNode)) {
			return Sequence.start()
				.then(new MemoizeBoxed("returnValue", Type.getReturnType(methodNode.desc)))
				.then(new InvokeVirtual(SnapshotManager.class, "inputArguments", int.class, Object[].class)
					.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
					.withArgument(0, new Recall("inputId"))
					.withArgument(1, new WrapArguments()))
				.then(new InvokeVirtual(SnapshotManager.class, "inputResult", int.class, Object.class)
					.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
					.withArgument(0, new Recall("inputId"))
					.withArgument(1, new Recall("returnValue")));
		} else {
			return Sequence.start()
				.then(new InvokeVirtual(SnapshotManager.class, "inputArguments", int.class, Object[].class)
					.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
					.withArgument(0, new Recall("inputId"))
					.withArgument(1, new WrapArguments()));
		}
	}

	private void instrumentOutputMethods(ClassNode classNode) {
		for (MethodNode method : getJavaOutputMethods(classNode)) {
			instrumentOutputMethod(classNode, method);
		}
	}

	protected void instrumentOutputMethod(ClassNode classNode, MethodNode methodNode) {
		methodNode.instructions = new WrapMethod()
			.prepend(outputVariables(classNode, methodNode))
			.append(outputResult(classNode, methodNode))
			.build(new MethodContext(classNode, methodNode));
	}

	protected SequenceInstruction outputVariables(ClassNode classNode, MethodNode methodNode) {
		return Sequence.start()
			.then(new Assign("outputId", Type.INT_TYPE)
				.value(new InvokeVirtual(SnapshotManager.class, "outputVariables", StackTraceElement[].class, Object.class, String.class, java.lang.reflect.Type.class, java.lang.reflect.Type[].class)
					.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
					.withArgument(0, new GetStackTrace())
					.withArgument(1, new GetThisOrNull())
					.withArgument(2, new Ldc(methodNode.name))
					.withArgument(3, new WrapResultType())
					.withArgument(4, new WrapArgumentTypes())))
			.then(new InvokeVirtual(SnapshotManager.class, "outputArguments", int.class, Object[].class)
				.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
				.withArgument(0, new Recall("outputId"))
				.withArgument(1, new WrapArguments()));
	}

	protected SequenceInstruction outputResult(ClassNode classNode, MethodNode methodNode) {
		if (returnsResult(methodNode)) {
			return Sequence.start()
				.then(new MemoizeBoxed("returnValue", Type.getReturnType(methodNode.desc)))
				.then(new InvokeVirtual(SnapshotManager.class, "outputResult", int.class, Object.class)
					.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
					.withArgument(0, new Recall("outputId"))
					.withArgument(1, new Recall("returnValue")));
		} else {
			return Nop.NOP;
		}
	}

	private void instrumentInputCalls(ClassNode classNode) {
		for (MethodNode method : classNode.methods) {
			if (!isInputMethod(classNode, method)) {
				for (MethodInsnNode inputCall : getNativeInputCalls(method)) {
					System.out.println(ByteCode.print(inputCall));
				}
			}
		}
	}

	private void instrumentOutputCalls(ClassNode classNode) {
		for (MethodNode method : classNode.methods) {
			if (!isOutputMethod(classNode, method)) {
				for (MethodInsnNode outputCall : getNativeOutputCalls(method)) {
					System.out.println(ByteCode.print(outputCall));
				}
			}
		}
	}

	private List<MethodNode> getSnapshotMethods(ClassNode classNode) {
		if (!isVisible(classNode)) {
			return Collections.emptyList();
		}
		return classNode.methods.stream()
			.filter(method -> isSnapshotMethod(method))
			.filter(method -> isVisible(method))
			.collect(toList());
	}

	private List<MethodNode> getJavaInputMethods(ClassNode classNode) {
		if (!isVisible(classNode)) {
			return Collections.emptyList();
		}
		return classNode.methods.stream()
			.filter(method -> isJavaInputMethod(classNode, method))
			.collect(toList());
	}

	private List<MethodInsnNode> getNativeInputCalls(MethodNode methodNode) {
		List<MethodInsnNode> calls = new ArrayList<>();
		ListIterator<AbstractInsnNode> instructions = methodNode.instructions.iterator();
		while (instructions.hasNext()) {
			AbstractInsnNode insn = instructions.next();
			if (insn instanceof MethodInsnNode) {
				MethodInsnNode methodinsn = (MethodInsnNode) insn;
				try {
					ClassNode calledClassNode = fetchClassNode(methodinsn.owner);
					MethodNode calledMethodNode = fetchMethodNode(calledClassNode, methodinsn.name, methodinsn.desc);
					if (isNativeInputMethod(calledClassNode, calledMethodNode)) {
						calls.add(methodinsn);

					}
				} catch (IOException | NoSuchMethodException e) {
					System.err.println("cannot load method " + methodinsn.owner + "." + methodinsn.name + methodinsn.desc);
					e.printStackTrace(System.err);
				}
			}
		}
		return calls;
	}

	private List<MethodNode> getJavaOutputMethods(ClassNode classNode) {
		if (!isVisible(classNode)) {
			return Collections.emptyList();
		}
		return classNode.methods.stream()
			.filter(method -> isJavaOutputMethod(classNode, method))
			.collect(toList());
	}

	private List<MethodInsnNode> getNativeOutputCalls(MethodNode methodNode) {
		List<MethodInsnNode> calls = new ArrayList<>();
		ListIterator<AbstractInsnNode> instructions = methodNode.instructions.iterator();
		while (instructions.hasNext()) {
			AbstractInsnNode insn = instructions.next();
			if (insn instanceof MethodInsnNode) {
				MethodInsnNode methodinsn = (MethodInsnNode) insn;
				try {
					ClassNode calledClassNode = fetchClassNode(methodinsn.owner);
					MethodNode calledMethodNode = fetchMethodNode(calledClassNode, methodinsn.name, methodinsn.desc);
					if (isNativeOutputMethod(calledClassNode, calledMethodNode)) {
						calls.add(methodinsn);

					}
				} catch (IOException | NoSuchMethodException e) {
					System.err.println("cannot load method " + methodinsn.owner + "." + methodinsn.name + methodinsn.desc);
					e.printStackTrace(System.err);
				}
			}
		}
		return calls;
	}

	private List<FieldNode> getGlobalFields(ClassNode classNode) {
		if (!isVisible(classNode)) {
			return Collections.emptyList();
		}
		return classNode.fields.stream()
			.filter(field -> isGlobalField(classNode.name, field))
			.filter(field -> isVisible(field))
			.collect(toList());
	}

	private List<MethodNode> getSkippedSnapshotMethods(ClassNode classNode) {
		return classNode.methods.stream()
			.filter(method -> isSnapshotMethod(method))
			.filter(method -> !isVisible(classNode) || !isVisible(method))
			.collect(toList());
	}

	private boolean isVisible(ClassNode classNode) {
		if ((classNode.access & ACC_PRIVATE) != 0) {
			return false;
		}
		return classNode.innerClasses.stream()
			.filter(innerClassNode -> innerClassNode.name.equals(classNode.name))
			.map(innerClassNode -> (innerClassNode.access & ACC_PRIVATE) == 0)
			.findFirst()
			.orElse(true);
	}

	private boolean isSnapshotMethod(MethodNode methodNode) {
		if (methodNode.visibleAnnotations == null) {
			return false;
		}
		return methodNode.visibleAnnotations.stream()
			.anyMatch(annotation -> annotation.desc.equals(Type.getDescriptor(Recorded.class)));
	}

	protected boolean isGlobalField(String className, FieldNode fieldNode) {
		return fieldNode.visibleAnnotations != null && fieldNode.visibleAnnotations.stream()
			.anyMatch(annotation -> annotation.desc.equals(Type.getDescriptor(Global.class)))
			|| config.getGlobalFields().stream()
				.anyMatch(field -> matches(field, className, fieldNode.name, fieldNode.desc));
	}

	private boolean isVisible(MethodNode methodNode) {
		return (methodNode.access & ACC_PRIVATE) == 0;
	}

	private boolean isVisible(FieldNode fieldNode) {
		return (fieldNode.access & ACC_PRIVATE) == 0;
	}

	protected boolean isJavaInputMethod(ClassNode classNode, MethodNode methodNode) {
		return !isNative(methodNode)
			&& isInputMethod(classNode, methodNode);
	}

	protected boolean isNativeInputMethod(ClassNode classNode, MethodNode methodNode) {
		return isNative(methodNode)
			&& isInputMethod(classNode, methodNode);
	}

	protected boolean isInputMethod(ClassNode classNode, MethodNode methodNode) {
		return methodNode.visibleAnnotations != null && methodNode.visibleAnnotations.stream()
			.anyMatch(annotation -> annotation.desc.equals(Type.getDescriptor(Input.class)))
			|| config.getInputs().stream()
				.anyMatch(method -> matches(method, classNode.name, methodNode.name, methodNode.desc));
	}

	protected boolean isJavaOutputMethod(ClassNode classNode, MethodNode methodNode) {
		return !isNative(methodNode)
			&& isOutputMethod(classNode, methodNode);
	}

	protected boolean isNativeOutputMethod(ClassNode classNode, MethodNode methodNode) {
		return isNative(methodNode)
			&& isOutputMethod(classNode, methodNode);
	}

	protected boolean isOutputMethod(ClassNode classNode, MethodNode methodNode) {
		return methodNode.visibleAnnotations != null && methodNode.visibleAnnotations.stream()
			.anyMatch(annotation -> annotation.desc.equals(Type.getDescriptor(Output.class)))
			|| config.getOutputs().stream()
				.anyMatch(method -> matches(method, classNode.name, methodNode.name, methodNode.desc));
	}

	private boolean matches(Fields field, String className, String fieldName, String fieldDescriptor) {
		return field.matches(className, fieldName, fieldDescriptor);
	}

	private boolean matches(Methods method, String className, String methodName, String methodDescriptor) {
		return method.matches(className, methodName, methodDescriptor);
	}

	protected SequenceInstruction setupVariables(ClassNode classNode, MethodNode methodNode) {
		return new InvokeVirtual(SnapshotManager.class, "setupVariables", Object.class, String.class, Object[].class)
			.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
			.withArgument(0, new GetThisOrNull())
			.withArgument(1, new Ldc(keySignature(classNode, methodNode)))
			.withArgument(2, new WrapArguments());
	}

	protected SequenceInstruction expectVariables(ClassNode classNode, MethodNode methodNode) {
		if (returnsResult(methodNode)) {
			return Sequence.start()
				.then(new MemoizeBoxed("returnValue", Type.getReturnType(methodNode.desc)))
				.then(new InvokeVirtual(SnapshotManager.class, "expectVariables", Object.class, String.class, Object.class, Object[].class)
					.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
					.withArgument(0, new GetThisOrNull())
					.withArgument(1, new Ldc(keySignature(classNode, methodNode)))
					.withArgument(2, new Recall("returnValue"))
					.withArgument(3, new WrapArguments()));
		} else {
			return Sequence.start()
				.then(new InvokeVirtual(SnapshotManager.class, "expectVariables", Object.class, String.class, Object[].class)
					.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
					.withArgument(0, new GetThisOrNull())
					.withArgument(1, new Ldc(keySignature(classNode, methodNode)))
					.withArgument(2, new WrapArguments()));
		}
	}

	protected SequenceInstruction throwVariables(ClassNode classNode, MethodNode methodNode) {
		return Sequence.start()
			.then(new MemoizeBoxed("throwable", Type.getType(Throwable.class)))
			.then(new InvokeVirtual(SnapshotManager.class, "throwVariables", Throwable.class, Object.class, String.class, Object[].class)
				.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
				.withArgument(0, new Recall("throwable"))
				.withArgument(1, new GetThisOrNull())
				.withArgument(2, new Ldc(keySignature(classNode, methodNode)))
				.withArgument(3, new WrapArguments()));
	}

	private String keySignature(ClassNode classNode, MethodNode methodNode) {
		return classNode.name + ":" + methodNode.name + methodNode.desc;
	}

}
