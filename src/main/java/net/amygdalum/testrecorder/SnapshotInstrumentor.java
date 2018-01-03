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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.amygdalum.testrecorder.asm.Assign;
import net.amygdalum.testrecorder.asm.ByteCode;
import net.amygdalum.testrecorder.asm.CaptureCall;
import net.amygdalum.testrecorder.asm.GetInvokedMethodArgumentTypes;
import net.amygdalum.testrecorder.asm.GetInvokedMethodName;
import net.amygdalum.testrecorder.asm.GetInvokedMethodResultType;
import net.amygdalum.testrecorder.asm.GetStackTrace;
import net.amygdalum.testrecorder.asm.GetStatic;
import net.amygdalum.testrecorder.asm.GetThisOrClass;
import net.amygdalum.testrecorder.asm.GetThisOrNull;
import net.amygdalum.testrecorder.asm.InvokeStatic;
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
import net.amygdalum.testrecorder.bridge.BridgedSnapshotManager;
import net.amygdalum.testrecorder.profile.Classes;
import net.amygdalum.testrecorder.profile.Fields;
import net.amygdalum.testrecorder.profile.Methods;
import net.amygdalum.testrecorder.profile.SerializationProfile.Global;
import net.amygdalum.testrecorder.profile.SerializationProfile.Input;
import net.amygdalum.testrecorder.profile.SerializationProfile.Output;
import net.amygdalum.testrecorder.util.AttachableClassFileTransformer;

public class SnapshotInstrumentor extends AttachableClassFileTransformer implements ClassFileTransformer {

	private TestRecorderAgentConfig config;
	private ClassNodeManager classes = new ClassNodeManager();
	private Set<String> instrumentedClassNames;
	private Set<Class<?>> instrumentedClasses;

	public SnapshotInstrumentor(TestRecorderAgentConfig config) {
		this.config = config;
		this.classes = new ClassNodeManager();
		this.instrumentedClassNames = new LinkedHashSet<>();
		this.instrumentedClasses = new LinkedHashSet<>();
		SnapshotManager.init(config);
	}

	@Override
	public Collection<Class<?>> filterClassesToRetransform(Class<?>[] loaded) {
		Set<Class<?>> classesToRetransform = new LinkedHashSet<>();
		for (Class<?> clazz : loaded) {
			for (Classes classes : config.getClasses()) {
				if (classes.matches(clazz)) {
					classesToRetransform.add(clazz);
				}
			}
		}
		return classesToRetransform;
	}

	@Override
	public Collection<Class<?>> getClassesToRetransform() {
		Set<Class<?>> classesToRetransform = new LinkedHashSet<>();
		classesToRetransform.addAll(instrumentedClasses);

		for (String className : instrumentedClassNames) {
			classesToRetransform.add(classFrom(className));
		}

		return classesToRetransform;
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		try {
			boolean aquired = lock.acquire();
			if (!aquired) {
				return null;
			}
			if (className == null) {
				return null;
			}
			for (Classes clazz : config.getClasses()) {
				if (clazz.matches(className)) {
					System.out.println("recording snapshots of " + className);

					byte[] instrument = instrument(classfileBuffer, classBeingRedefined);
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
		} finally {
			lock.release();
		}

	}

	public byte[] instrument(String className, Class<?> clazz) throws IOException {
		return instrument(classes.fetch(className), clazz);
	}

	public byte[] instrument(byte[] buffer, Class<?> clazz) {
		return instrument(classes.register(buffer), clazz);
	}

	public byte[] instrument(ClassNode classNode, Class<?> clazz) {
		if (!isClass(classNode)) {
			return null;
		}
		Task task = needsBridging(classNode, clazz)
			? new BridgedTask(config, classes, classNode)
			: new DefaultTask(config, classes, classNode);

		task.logSkippedSnapshotMethods();

		task.registerCallbacks();

		task.instrumentSnapshotMethods();

		task.instrumentInputMethods();

		task.instrumentOutputMethods();

		task.instrumentNativeInputCalls();

		task.instrumentNativeOutputCalls();

		ClassWriter out = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(out);
		return out.toByteArray();
	}

	private boolean isClass(ClassNode classNode) {
		return (classNode.access & (ACC_INTERFACE | ACC_ANNOTATION)) == 0;
	}

	private boolean needsBridging(ClassNode classNode, Class<?> clazz) {
		if (clazz != null && clazz.getClassLoader() == null) {
			return true;
		}
		return false;
	}

	public static abstract class Task {

		private TestRecorderAgentConfig config;
		private ClassNodeManager classes;

		protected ClassNode classNode;

		public Task(TestRecorderAgentConfig config, ClassNodeManager classes, ClassNode classNode) {
			this.config = config;
			this.classes = classes;
			this.classNode = classNode;
		}

		public void logSkippedSnapshotMethods() {
			for (MethodNode methodNode : getSkippedSnapshotMethods()) {
				System.err.println("method " + Type.getMethodType(methodNode.desc).getDescriptor() + " in " + Type.getType(classNode.name) + " is not accessible, skipping");
			}
		}

		public void registerCallbacks() {
			for (MethodNode methodNode : getSnapshotMethods()) {
				SnapshotManager.MANAGER.registerRecordedMethod(keySignature(classNode, methodNode), classNode.name, methodNode.name, methodNode.desc);
			}
			for (FieldNode fieldNode : getGlobalFields()) {
				SnapshotManager.MANAGER.registerGlobal(classNode.name, fieldNode.name);
			}
		}

		private void instrumentSnapshotMethods() {
			for (MethodNode method : getSnapshotMethods()) {
				instrumentSnapshotMethod(method);
			}
		}

		protected void instrumentSnapshotMethod(MethodNode methodNode) {
			methodNode.instructions = new WrapWithTryCatch(methodNode)
				.before(setupVariables(methodNode))
				.after(expectVariables(methodNode))
				.handler(throwVariables(methodNode))
				.build(new MethodContext(classNode, methodNode));
		}

		protected SequenceInstruction setupVariables(MethodNode methodNode) {
			return new InvokeVirtual(SnapshotManager.class, "setupVariables", Object.class, String.class, Object[].class)
				.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
				.withArgument(0, new GetThisOrNull())
				.withArgument(1, new Ldc(keySignature(classNode, methodNode)))
				.withArgument(2, new WrapArguments());
		}

		protected SequenceInstruction expectVariables(MethodNode methodNode) {
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

		protected SequenceInstruction throwVariables(MethodNode methodNode) {
			return Sequence.start()
				.then(new MemoizeBoxed("throwable", Type.getType(Throwable.class)))
				.then(new InvokeVirtual(SnapshotManager.class, "throwVariables", Throwable.class, Object.class, String.class, Object[].class)
					.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
					.withArgument(0, new Recall("throwable"))
					.withArgument(1, new GetThisOrNull())
					.withArgument(2, new Ldc(keySignature(classNode, methodNode)))
					.withArgument(3, new WrapArguments()));
		}

		public void instrumentInputMethods() {
			for (MethodNode method : getJavaInputMethods()) {
				instrumentInputMethod(method);
			}
		}

		protected void instrumentInputMethod(MethodNode methodNode) {
			methodNode.instructions = new WrapMethod()
				.prepend(inputVariables(methodNode))
				.append(inputArgumentsAndResult(methodNode))
				.build(new MethodContext(classNode, methodNode));
		}

		protected abstract SequenceInstruction inputArgumentsAndResult(MethodNode methodNode);

		protected abstract SequenceInstruction inputVariables(MethodNode methodNode);

		public void instrumentOutputMethods() {
			for (MethodNode method : getJavaOutputMethods()) {
				instrumentOutputMethod(method);
			}
		}

		protected void instrumentOutputMethod(MethodNode methodNode) {
			methodNode.instructions = new WrapMethod()
				.prepend(outputVariables(methodNode))
				.append(outputResult(methodNode))
				.build(new MethodContext(classNode, methodNode));
		}

		protected abstract SequenceInstruction outputVariables(MethodNode methodNode);

		protected abstract SequenceInstruction outputResult(MethodNode methodNode);

		private void instrumentNativeInputCalls() {
			for (MethodNode method : classNode.methods) {
				if (!isInputMethod(classNode, method)) {
					MethodContext context = new MethodContext(classNode, method);
					for (MethodInsnNode inputCall : getNativeInputCalls(method)) {
						method.instructions.insertBefore(inputCall, beforeNativeInputCall(context, inputCall));
						method.instructions.insert(inputCall, afterNativeInputCall(context, inputCall));
					}
				}
			}
		}

		protected InsnList beforeNativeInputCall(MethodContext context, MethodInsnNode inputCall) {
			return Sequence.start()
				.then(new CaptureCall(inputCall, "base", "arguments"))
				.then(new Assign("inputId", Type.INT_TYPE)
					.value(
						new InvokeVirtual(SnapshotManager.class, "inputVariables", StackTraceElement[].class, Object.class, String.class, java.lang.reflect.Type.class, java.lang.reflect.Type[].class)
							.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
							.withArgument(0, new GetStackTrace())
							.withArgument(1, new Recall("base"))
							.withArgument(2, new GetInvokedMethodName(inputCall))
							.withArgument(3, new GetInvokedMethodResultType(inputCall))
							.withArgument(4, new GetInvokedMethodArgumentTypes(inputCall))))
				.build(context);
		}

		protected InsnList afterNativeInputCall(MethodContext context, MethodInsnNode inputCall) {
			if (returnsResult(inputCall)) {
				return Sequence.start()
					.then(new MemoizeBoxed("returnValue", Type.getReturnType(inputCall.desc)))
					.then(new InvokeVirtual(SnapshotManager.class, "inputArguments", int.class, Object[].class)
						.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
						.withArgument(0, new Recall("inputId"))
						.withArgument(1, new Recall("arguments")))
					.then(new InvokeVirtual(SnapshotManager.class, "inputResult", int.class, Object.class)
						.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
						.withArgument(0, new Recall("inputId"))
						.withArgument(1, new Recall("returnValue")))
					.build(context);
			} else {
				return Sequence.start()
					.then(new InvokeVirtual(SnapshotManager.class, "inputArguments", int.class, Object[].class)
						.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
						.withArgument(0, new Recall("inputId"))
						.withArgument(1, new Recall("arguments")))
					.build(context);
			}
		}

		private void instrumentNativeOutputCalls() {
			for (MethodNode method : classNode.methods) {
				if (!isOutputMethod(classNode, method)) {
					MethodContext context = new MethodContext(classNode, method);
					for (MethodInsnNode outputCall : getNativeOutputCalls(method)) {
						method.instructions.insertBefore(outputCall, beforeNativeOutputCall(context, outputCall));
						method.instructions.insert(outputCall, afterNativeOutputCall(context, outputCall));
					}
				}
			}
		}

		protected InsnList beforeNativeOutputCall(MethodContext context, MethodInsnNode inputCall) {
			return Sequence.start()
				.then(new CaptureCall(inputCall, "base", "arguments"))
				.then(new Assign("outputId", Type.INT_TYPE)
					.value(
						new InvokeVirtual(SnapshotManager.class, "outputVariables", StackTraceElement[].class, Object.class, String.class, java.lang.reflect.Type.class, java.lang.reflect.Type[].class)
							.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
							.withArgument(0, new GetStackTrace())
							.withArgument(1, new Recall("base"))
							.withArgument(2, new GetInvokedMethodName(inputCall))
							.withArgument(3, new GetInvokedMethodResultType(inputCall))
							.withArgument(4, new GetInvokedMethodArgumentTypes(inputCall))))
				.then(new InvokeVirtual(SnapshotManager.class, "outputArguments", int.class, Object[].class)
					.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
					.withArgument(0, new Recall("outputId"))
					.withArgument(1, new Recall("arguments")))
				.build(context);
		}

		protected InsnList afterNativeOutputCall(MethodContext context, MethodInsnNode inputCall) {
			if (returnsResult(inputCall)) {
				return Sequence.start()
					.then(new MemoizeBoxed("returnValue", Type.getReturnType(inputCall.desc)))
					.then(new InvokeVirtual(SnapshotManager.class, "outputResult", int.class, Object.class)
						.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
						.withArgument(0, new Recall("outputId"))
						.withArgument(1, new Recall("returnValue")))
					.build(context);
			} else {
				return new InsnList();
			}
		}

		private List<MethodNode> getSkippedSnapshotMethods() {
			return classNode.methods.stream()
				.filter(method -> isSnapshotMethod(method))
				.filter(method -> !isVisible(classNode) || !isVisible(method))
				.collect(toList());
		}

		private List<FieldNode> getGlobalFields() {
			if (!isVisible(classNode)) {
				return Collections.emptyList();
			}
			return classNode.fields.stream()
				.filter(field -> isGlobalField(classNode.name, field))
				.filter(field -> isVisible(field))
				.collect(toList());
		}

		private List<MethodNode> getSnapshotMethods() {
			if (!isVisible(classNode)) {
				return Collections.emptyList();
			}
			return classNode.methods.stream()
				.filter(method -> isSnapshotMethod(method))
				.filter(method -> isVisible(method))
				.collect(toList());
		}

		private List<MethodNode> getJavaInputMethods() {
			if (!isVisible(classNode)) {
				return Collections.emptyList();
			}
			return classNode.methods.stream()
				.filter(method -> isJavaInputMethod(classNode, method))
				.collect(toList());
		}

		private List<MethodNode> getJavaOutputMethods() {
			if (!isVisible(classNode)) {
				return Collections.emptyList();
			}
			return classNode.methods.stream()
				.filter(method -> isJavaOutputMethod(classNode, method))
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
						Type type = Type.getType(methodinsn.owner);
						if (ByteCode.isPrimitive(type) || ByteCode.isArray(type)) {
							continue;
						}
						ClassNode calledClassNode = classes.fetch(methodinsn.owner);
						MethodNode calledMethodNode = classes.fetch(calledClassNode, methodinsn.name, methodinsn.desc);
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

		private List<MethodInsnNode> getNativeOutputCalls(MethodNode methodNode) {
			List<MethodInsnNode> calls = new ArrayList<>();
			ListIterator<AbstractInsnNode> instructions = methodNode.instructions.iterator();
			while (instructions.hasNext()) {
				AbstractInsnNode insn = instructions.next();
				if (insn instanceof MethodInsnNode) {
					MethodInsnNode methodinsn = (MethodInsnNode) insn;
					try {
						Type type = Type.getType(methodinsn.owner);
						if (ByteCode.isPrimitive(type) || ByteCode.isArray(type)) {
							continue;
						}
						ClassNode calledClassNode = classes.fetch(methodinsn.owner);
						MethodNode calledMethodNode = classes.fetch(calledClassNode, methodinsn.name, methodinsn.desc);
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

		private boolean isGlobalField(String className, FieldNode fieldNode) {
			return fieldNode.visibleAnnotations != null && fieldNode.visibleAnnotations.stream()
				.anyMatch(annotation -> annotation.desc.equals(Type.getDescriptor(Global.class)))
				|| config.getGlobalFields().stream()
					.anyMatch(field -> matches(field, className, fieldNode.name, fieldNode.desc));
		}

		private boolean isSnapshotMethod(MethodNode methodNode) {
			if (methodNode.visibleAnnotations == null) {
				return false;
			}
			return methodNode.visibleAnnotations.stream()
				.anyMatch(annotation -> annotation.desc.equals(Type.getDescriptor(Recorded.class)));
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

		private boolean isVisible(MethodNode methodNode) {
			return (methodNode.access & ACC_PRIVATE) == 0;
		}

		private boolean isVisible(FieldNode fieldNode) {
			return (fieldNode.access & ACC_PRIVATE) == 0;
		}

		private boolean matches(Fields field, String className, String fieldName, String fieldDescriptor) {
			return field.matches(className, fieldName, fieldDescriptor);
		}

		private boolean matches(Methods method, String className, String methodName, String methodDescriptor) {
			return method.matches(className, methodName, methodDescriptor);
		}

		private String keySignature(ClassNode classNode, MethodNode methodNode) {
			return classNode.name + ":" + methodNode.name + methodNode.desc;
		}

	}

	public static class BridgedTask extends Task {

		public BridgedTask(TestRecorderAgentConfig config, ClassNodeManager classes, ClassNode classNode) {
			super(config, classes, classNode);
		}

		@Override
		protected SequenceInstruction inputVariables(MethodNode methodNode) {
			return new Assign("inputId", Type.INT_TYPE)
				.value(new InvokeStatic(BridgedSnapshotManager.class, "inputVariables", StackTraceElement[].class, Object.class, String.class, java.lang.reflect.Type.class,
					java.lang.reflect.Type[].class)
						.withArgument(0, new GetStackTrace())
						.withArgument(1, new GetThisOrClass())
						.withArgument(2, new Ldc(methodNode.name))
						.withArgument(3, new WrapResultType())
						.withArgument(4, new WrapArgumentTypes()));
		}

		@Override
		protected SequenceInstruction inputArgumentsAndResult(MethodNode methodNode) {
			if (returnsResult(methodNode)) {
				return Sequence.start()
					.then(new MemoizeBoxed("returnValue", Type.getReturnType(methodNode.desc)))
					.then(new InvokeStatic(BridgedSnapshotManager.class, "inputArguments", int.class, Object[].class)
						.withArgument(0, new Recall("inputId"))
						.withArgument(1, new WrapArguments()))
					.then(new InvokeStatic(BridgedSnapshotManager.class, "inputResult", int.class, Object.class)
						.withArgument(0, new Recall("inputId"))
						.withArgument(1, new Recall("returnValue")));
			} else {
				return Sequence.start()
					.then(new InvokeStatic(BridgedSnapshotManager.class, "inputArguments", int.class, Object[].class)
						.withArgument(0, new Recall("inputId"))
						.withArgument(1, new WrapArguments()));
			}
		}

		@Override
		protected SequenceInstruction outputVariables(MethodNode methodNode) {
			return Sequence.start()
				.then(new Assign("outputId", Type.INT_TYPE)
					.value(new InvokeStatic(BridgedSnapshotManager.class, "outputVariables", StackTraceElement[].class, Object.class, String.class, java.lang.reflect.Type.class,
						java.lang.reflect.Type[].class)
							.withArgument(0, new GetStackTrace())
							.withArgument(1, new GetThisOrClass())
							.withArgument(2, new Ldc(methodNode.name))
							.withArgument(3, new WrapResultType())
							.withArgument(4, new WrapArgumentTypes())))
				.then(new InvokeStatic(BridgedSnapshotManager.class, "outputArguments", int.class, Object[].class)
					.withArgument(0, new Recall("outputId"))
					.withArgument(1, new WrapArguments()));
		}

		@Override
		protected SequenceInstruction outputResult(MethodNode methodNode) {
			if (returnsResult(methodNode)) {
				return Sequence.start()
					.then(new MemoizeBoxed("returnValue", Type.getReturnType(methodNode.desc)))
					.then(new InvokeStatic(BridgedSnapshotManager.class, "outputResult", int.class, Object.class)
						.withArgument(0, new Recall("outputId"))
						.withArgument(1, new Recall("returnValue")));
			} else {
				return Nop.NOP;
			}
		}

	}

	public static class DefaultTask extends Task {

		public DefaultTask(TestRecorderAgentConfig config, ClassNodeManager classes, ClassNode classNode) {
			super(config, classes, classNode);
		}

		@Override
		protected SequenceInstruction inputVariables(MethodNode methodNode) {
			return new Assign("inputId", Type.INT_TYPE)
				.value(new InvokeVirtual(SnapshotManager.class, "inputVariables", StackTraceElement[].class, Object.class, String.class, java.lang.reflect.Type.class, java.lang.reflect.Type[].class)
					.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
					.withArgument(0, new GetStackTrace())
					.withArgument(1, new GetThisOrClass())
					.withArgument(2, new Ldc(methodNode.name))
					.withArgument(3, new WrapResultType())
					.withArgument(4, new WrapArgumentTypes()));
		}

		@Override
		protected SequenceInstruction inputArgumentsAndResult(MethodNode methodNode) {
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

		@Override
		protected SequenceInstruction outputVariables(MethodNode methodNode) {
			return Sequence.start()
				.then(new Assign("outputId", Type.INT_TYPE)
					.value(
						new InvokeVirtual(SnapshotManager.class, "outputVariables", StackTraceElement[].class, Object.class, String.class, java.lang.reflect.Type.class, java.lang.reflect.Type[].class)
							.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
							.withArgument(0, new GetStackTrace())
							.withArgument(1, new GetThisOrClass())
							.withArgument(2, new Ldc(methodNode.name))
							.withArgument(3, new WrapResultType())
							.withArgument(4, new WrapArgumentTypes())))
				.then(new InvokeVirtual(SnapshotManager.class, "outputArguments", int.class, Object[].class)
					.withBase(new GetStatic(SnapshotManager.class, "MANAGER"))
					.withArgument(0, new Recall("outputId"))
					.withArgument(1, new WrapArguments()));
		}

		@Override
		protected SequenceInstruction outputResult(MethodNode methodNode) {
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

	}
}
