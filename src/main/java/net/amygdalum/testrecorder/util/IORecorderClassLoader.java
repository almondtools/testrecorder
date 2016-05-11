package net.amygdalum.testrecorder.util;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.ByteCode.pushAsArray;
import static net.amygdalum.testrecorder.ByteCode.range;
import static net.amygdalum.testrecorder.ByteCode.unboxPrimitives;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DCONST_0;
import static org.objectweb.asm.Opcodes.DRETURN;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.FCONST_0;
import static org.objectweb.asm.Opcodes.FRETURN;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.INSTANCEOF;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.LCONST_0;
import static org.objectweb.asm.Opcodes.LRETURN;
import static org.objectweb.asm.Opcodes.POP;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import net.amygdalum.testrecorder.ByteCode;
import net.amygdalum.testrecorder.SnapshotInput;
import net.amygdalum.testrecorder.SnapshotOutput;

public class IORecorderClassLoader extends AbstractInstrumentedClassLoader {

	private static final String Class_name = Type.getInternalName(Class.class);
	private static final String IORecorderClassLoader_name = Type.getInternalName(IORecorderClassLoader.class);
	private static final String InputProvider_name = Type.getInternalName(InputProvider.class);
	private static final String OutputListener_name = Type.getInternalName(OutputListener.class);

	private static final String SnapshotInput_descriptor = Type.getDescriptor(SnapshotInput.class);
	private static final String SnapshotOutput_descriptor = Type.getDescriptor(SnapshotOutput.class);

	private static final String Class_getClassLoader_descriptor = ByteCode.methodDescriptor(Class.class, "getClassLoader");

	private static final String IORecorderClassLoader_getOut_descriptor = ByteCode.methodDescriptor(IORecorderClassLoader.class, "getOut");
	private static final String IORecorderClassLoader_getIn_descriptor = ByteCode.methodDescriptor(IORecorderClassLoader.class, "getIn");

	private static final String InputProvider_requestInput_descriptor = ByteCode.methodDescriptor(InputProvider.class, "requestInput", Class.class, String.class, Object[].class);

	private static final String OutputListener_notifyOutput_descriptor = ByteCode.methodDescriptor(OutputListener.class, "notifyOutput", Class.class, String.class, Object[].class);

	private InputProvider in;
	private OutputListener out;
	private String root;
	private Set<String> classes;

	public IORecorderClassLoader(Class<?> clazz, InputProvider in, OutputListener out, Set<String> classes) {
		super(clazz.getClassLoader());
		this.root = clazz.getName();
		this.in = in;
		this.out = out;
		this.classes = classes;
		adoptInstrumentations(clazz.getClassLoader());
	}

	private final void adoptInstrumentations(ClassLoader loader) {
		if (loader instanceof ClassInstrumenting) {
			Map<String, byte[]> instrumentations = ((ClassInstrumenting) loader).getInstrumentations();
			for (Map.Entry<String, byte[]> instrumentation : instrumentations.entrySet()) {
				String name = instrumentation.getKey();
				byte[] bytes = instrumentation.getValue();
				if (!classes.contains(name) && findLoadedClass(name) == null) {
					define(name, bytes);
				}
			}
		}
	}

	public InputProvider getIn() {
		return in;
	}

	public OutputListener getOut() {
		return out;
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (name.startsWith(root) ) {
			Class<?> clazz = findLoadedClass(name);
			if (clazz != null) {
				return clazz;
			} else {
				return findClass(name);
			}
		}
		if (isInstrumented(name)) {
			return findLoadedClass(name);
		}
		if (!classes.contains(name)) {
			return super.loadClass(name);
		}

		try {
			byte[] bytes = instrument(name);
			return define(name, bytes);
		} catch (Throwable t) {
			throw new ClassNotFoundException(t.getMessage(), t);
		}

	}

	public byte[] instrument(String className) throws IOException {
		return instrument(new ClassReader(className));
	}

	public byte[] instrument(ClassReader cr) {
		ClassNode classNode = new ClassNode();

		cr.accept(classNode, 0);

		instrumentInputMethods(classNode);
		instrumentOutputMethods(classNode);

		ClassWriter out = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(out);
		return out.toByteArray();
	}

	private void instrumentInputMethods(ClassNode classNode) {
		for (MethodNode method : getInputMethods(classNode)) {
			method.instructions.clear();
			method.instructions.insert(readInput(classNode, method));
		}
	}

	private void instrumentOutputMethods(ClassNode classNode) {
		for (MethodNode method : getOutputMethods(classNode)) {
			method.instructions.insert(notifyOutput(classNode, method));
		}
	}

	private List<MethodNode> getInputMethods(ClassNode classNode) {
		return classNode.methods.stream()
			.filter(method -> isInputMethod(method))
			.collect(toList());
	}

	private boolean isInputMethod(MethodNode method) {
		if (method.visibleAnnotations == null) {
			return false;
		}
		return method.visibleAnnotations.stream()
			.anyMatch(annotation -> annotation.desc.equals(SnapshotInput_descriptor));
	}

	private List<MethodNode> getOutputMethods(ClassNode classNode) {
		return classNode.methods.stream()
			.filter(method -> isOutputMethod(method))
			.collect(toList());
	}

	private boolean isOutputMethod(MethodNode method) {
		if (method.visibleAnnotations == null) {
			return false;
		}
		return method.visibleAnnotations.stream()
			.anyMatch(annotation -> annotation.desc.equals(SnapshotOutput_descriptor));
	}

	private InsnList readInput(ClassNode classNode, MethodNode methodNode) {
		Type returnType = Type.getReturnType(methodNode.desc);
		Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
		List<LocalVariableNode> arguments = range(methodNode.localVariables, 1, argumentTypes.length);

		InsnList insnList = new InsnList();

		LabelNode skip = new LabelNode();
		LabelNode done = new LabelNode();

		insnList.add(new LdcInsnNode(Type.getObjectType(classNode.name)));
		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Class_name, "getClassLoader", Class_getClassLoader_descriptor, false));

		insnList.add(new InsnNode(DUP));
		insnList.add(new JumpInsnNode(IFNULL, skip));

		insnList.add(new InsnNode(DUP));
		insnList.add(new TypeInsnNode(INSTANCEOF, IORecorderClassLoader_name));
		insnList.add(new JumpInsnNode(IFEQ, skip));
		insnList.add(new TypeInsnNode(CHECKCAST, IORecorderClassLoader_name));

		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, IORecorderClassLoader_name, "getIn", IORecorderClassLoader_getIn_descriptor, false));

		insnList.add(new InsnNode(DUP));
		insnList.add(new JumpInsnNode(IFNULL, skip));

		insnList.add(new LdcInsnNode(Type.getObjectType(classNode.name)));
		insnList.add(new LdcInsnNode(methodNode.name));
		insnList.add(pushAsArray(arguments, argumentTypes));
		insnList.add(new MethodInsnNode(INVOKEINTERFACE, InputProvider_name, "requestInput", InputProvider_requestInput_descriptor, true));

		insnList.add(returnValue(returnType));
		insnList.add(skip);
		insnList.add(new InsnNode(POP));
		insnList.add(returnDefaultValue(returnType));
		insnList.add(done);
		return insnList;
	}

	private InsnList returnValue(Type returnType) {
		InsnList insnList = new InsnList();
		if (returnType.getSize() == 0) {
			insnList.add(new InsnNode(Opcodes.RETURN));
			return insnList;
		}
		insnList.add(unboxPrimitives(returnType));
		switch (returnType.getSort()) {
		case Type.BOOLEAN:
		case Type.BYTE:
		case Type.CHAR:
		case Type.SHORT:
		case Type.INT:
			insnList.add(new InsnNode(IRETURN));
			break;
		case Type.LONG:
			insnList.add(new InsnNode(LRETURN));
			break;
		case Type.FLOAT:
			insnList.add(new InsnNode(FRETURN));
			break;
		case Type.DOUBLE:
			insnList.add(new InsnNode(DRETURN));
			break;
		case Type.OBJECT:
		default:
			insnList.add(new TypeInsnNode(CHECKCAST, returnType.getInternalName()));
			insnList.add(new InsnNode(ARETURN));
			break;
		}
		return insnList;
	}

	private InsnList returnDefaultValue(Type returnType) {
		InsnList insnList = new InsnList();
		if (returnType.getSize() == 0) {
			insnList.add(new InsnNode(Opcodes.RETURN));
			return insnList;
		}
		switch (returnType.getSort()) {
		case Type.BOOLEAN:
		case Type.BYTE:
		case Type.CHAR:
		case Type.SHORT:
		case Type.INT:
			insnList.add(new InsnNode(ICONST_0));
			insnList.add(new InsnNode(IRETURN));
			break;
		case Type.LONG:
			insnList.add(new InsnNode(LCONST_0));
			insnList.add(new InsnNode(LRETURN));
			break;
		case Type.FLOAT:
			insnList.add(new InsnNode(FCONST_0));
			insnList.add(new InsnNode(FRETURN));
			break;
		case Type.DOUBLE:
			insnList.add(new InsnNode(DCONST_0));
			insnList.add(new InsnNode(DRETURN));
			break;
		case Type.OBJECT:
		default:
			insnList.add(new InsnNode(ACONST_NULL));
			insnList.add(new TypeInsnNode(CHECKCAST, returnType.getInternalName()));
			insnList.add(new InsnNode(ARETURN));
			break;
		}
		return insnList;
	}

	private InsnList notifyOutput(ClassNode classNode, MethodNode methodNode) {
		Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
		List<LocalVariableNode> arguments = range(methodNode.localVariables, 1, argumentTypes.length);

		InsnList insnList = new InsnList();

		LabelNode skip = new LabelNode();
		LabelNode done = new LabelNode();

		insnList.add(new LdcInsnNode(Type.getObjectType(classNode.name)));
		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Class_name, "getClassLoader",
			Class_getClassLoader_descriptor, false));

		insnList.add(new InsnNode(DUP));
		insnList.add(new JumpInsnNode(IFNULL, skip));

		insnList.add(new InsnNode(DUP));
		insnList.add(new TypeInsnNode(INSTANCEOF, IORecorderClassLoader_name));
		insnList.add(new JumpInsnNode(IFEQ, skip));
		insnList.add(new TypeInsnNode(CHECKCAST, IORecorderClassLoader_name));

		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, IORecorderClassLoader_name, "getOut", IORecorderClassLoader_getOut_descriptor, false));

		insnList.add(new InsnNode(DUP));
		insnList.add(new JumpInsnNode(IFNULL, skip));

		insnList.add(new LdcInsnNode(Type.getObjectType(classNode.name)));
		insnList.add(new LdcInsnNode(methodNode.name));
		insnList.add(pushAsArray(arguments, argumentTypes));
		insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, OutputListener_name, "notifyOutput", OutputListener_notifyOutput_descriptor, true));

		insnList.add(new JumpInsnNode(Opcodes.GOTO, done));
		insnList.add(skip);
		insnList.add(new InsnNode(POP));
		insnList.add(done);
		return insnList;
	}

}