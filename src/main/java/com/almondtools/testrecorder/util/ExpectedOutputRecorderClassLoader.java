package com.almondtools.testrecorder.util;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INSTANCEOF;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.POP;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
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
import org.objectweb.asm.tree.VarInsnNode;

import com.almondtools.testrecorder.SnapshotOutput;
import com.almondtools.testrecorder.dynamiccompile.DynamicClassLoader;

public class ExpectedOutputRecorderClassLoader extends URLClassLoader {

	private ClassLoader orig;
	private OutputListener out;
	private Set<String> classes;
	private String root;

	public ExpectedOutputRecorderClassLoader(ClassLoader orig, String root, OutputListener out, String... classes) {
		super(((URLClassLoader) getSystemClassLoader()).getURLs());
		this.orig = orig;
		this.root = root;
		this.out = out;
		this.classes = new HashSet<>(asList(classes));
	}

	public OutputListener getOut() {
		return out;
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (!classes.contains(name)) {
			if (orig instanceof DynamicClassLoader) {
				byte[] bytes = ((DynamicClassLoader) orig).getBytes(name);
				if (bytes != null) {
					return defineClass(name, bytes, 0, bytes.length);
				}
			}
			if (name.contains(root)) {
				return findClass(name);
			} else {
				return super.loadClass(name);
			}
		}

		try {
			byte[] bytes = instrument(name);

			return defineClass(name, bytes, 0, bytes.length);
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

		instrumentOutputMethods(classNode);

		ClassWriter out = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(out);
		return out.toByteArray();
	}

	private void instrumentOutputMethods(ClassNode classNode) {
		for (MethodNode method : getOutputMethods(classNode)) {
			method.instructions.insert(notifyOutput(classNode, method));
		}
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
			.anyMatch(annotation -> annotation.desc.equals(Type.getDescriptor(SnapshotOutput.class)));
	}

	private InsnList notifyOutput(ClassNode classNode, MethodNode methodNode) {
		try {
			InsnList insnList = new InsnList();

			LabelNode skip = new LabelNode();
			LabelNode done = new LabelNode();

			insnList.add(new LdcInsnNode(Type.getObjectType(classNode.name)));
			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(Class.class), "getClassLoader",
				Type.getMethodDescriptor(Class.class.getMethod("getClassLoader")), false));

			insnList.add(new InsnNode(DUP));
			insnList.add(new JumpInsnNode(IFNULL, skip));

			insnList.add(new InsnNode(DUP));
			insnList.add(new TypeInsnNode(INSTANCEOF, Type.getInternalName(ExpectedOutputRecorderClassLoader.class)));
			insnList.add(new JumpInsnNode(IFEQ, skip));
			insnList.add(new TypeInsnNode(CHECKCAST, Type.getInternalName(ExpectedOutputRecorderClassLoader.class)));

			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(ExpectedOutputRecorderClassLoader.class), "getOut",
				Type.getMethodDescriptor(ExpectedOutputRecorderClassLoader.class.getMethod("getOut")), false));

			insnList.add(new InsnNode(DUP));
			insnList.add(new JumpInsnNode(IFNULL, skip));

			insnList.add(new LdcInsnNode(Type.getObjectType(classNode.name)));
			insnList.add(new LdcInsnNode(methodNode.name));
			insnList.add(pushMethodArguments(methodNode));
			insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Type.getInternalName(OutputListener.class), "notifyOutput",
				Type.getMethodDescriptor(OutputListener.class.getMethod("notifyOutput", Class.class, String.class, Object[].class)), true));

			insnList.add(new JumpInsnNode(Opcodes.GOTO, done));
			insnList.add(skip);
			insnList.add(new InsnNode(POP));
			insnList.add(done);
			return insnList;
		} catch (ReflectiveOperationException e) {
			return new InsnList();
		}
	}

	private InsnList pushMethodArguments(MethodNode methodNode) throws ReflectiveOperationException {
		int params = Type.getArgumentTypes(methodNode.desc).length;

		InsnList insnList = new InsnList();

		insnList.add(new LdcInsnNode(params));
		insnList.add(new TypeInsnNode(Opcodes.ANEWARRAY, Type.getInternalName(Object.class)));

		for (int i = 0; i < params; i++) {
			insnList.add(new InsnNode(DUP));
			insnList.add(new LdcInsnNode(i));
			LocalVariableNode node = (LocalVariableNode) methodNode.localVariables.get(i + 1);
			Type type = Type.getType(node.desc);
			insnList.add(new VarInsnNode(type.getOpcode(ILOAD), i + 1));

			insnList.add(boxPrimitives(type));

			insnList.add(new InsnNode(AASTORE));
		}
		return insnList;

	}

	private InsnList boxPrimitives(Type type) throws ReflectiveOperationException {
		InsnList insnList = new InsnList();
		if (type.getDescriptor().length() == 1) {
			char desc = type.getDescriptor().charAt(0);
			Class<?> raw = getRawType(desc);
			Class<?> boxed = getBoxedType(desc);

			Method method = boxed.getMethod("valueOf", raw);
			insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(boxed), "valueOf", Type.getMethodDescriptor(method), false));
		}
		return insnList;
	}

	private Class<?> getRawType(char desc) {
		switch (desc) {
		case 'Z':
			return boolean.class;
		case 'C':
			return char.class;
		case 'B':
			return byte.class;
		case 'S':
			return short.class;
		case 'I':
			return int.class;
		case 'F':
			return float.class;
		case 'J':
			return long.class;
		case 'D':
			return double.class;
		case 'V':
		default:
			return void.class;
		}
	}

	private Class<?> getBoxedType(char desc) {
		switch (desc) {
		case 'Z':
			return Boolean.class;
		case 'C':
			return Character.class;
		case 'B':
			return Byte.class;
		case 'S':
			return Short.class;
		case 'I':
			return Integer.class;
		case 'F':
			return Float.class;
		case 'J':
			return Long.class;
		case 'D':
			return Double.class;
		case 'V':
		default:
			return Void.class;
		}
	}

}