package com.almondtools.testrecorder;

import static java.util.stream.Collectors.toList;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP2;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SWAP;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.almondtools.testrecorder.generator.TypeHelper;

public class SnapshotInstrumentor implements ClassFileTransformer {

	private static final String CONSTRUCTOR_NAME = "<init>";
	private static final String DEFINE_PACKAGE = "definePackage";
	private static final String DEFINE_CLASS = "defineClass";
	private static final String CLASS_LOADER = "java.lang.ClassLoader";

	public static final String SNAPSHOT_GENERATOR_FIELD_NAME = "generator";
	
	private SnapshotConfig config;

	public SnapshotInstrumentor(SnapshotConfig config) {
		this.config = config;
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		for (String pkg : config.getPackages()) {
			pkg = pkg.replace('.', '/');
			if (className != null && className.startsWith(pkg)) {
				System.out.println("recording snapshots of " + className);
				return instrument(new ClassReader(classfileBuffer));
			}
		}
		return null;
	}
	
	public void register(String clazz) throws IOException {
		byte[] bytecode = instrument(clazz);
		saveClass(clazz, bytecode);
	}

	public byte[] instrument(String className) throws IOException {
		return instrument(new ClassReader(className));
	}

	public byte[] instrument(ClassReader cr) {
		ClassNode classNode = new ClassNode();

		cr.accept(classNode, 0);

		instrumentFields(classNode);

		instrumentConstructors(classNode);

		instrumentMethods(classNode);

		ClassWriter out = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(out);
		return out.toByteArray();
	}

	private void instrumentFields(ClassNode classNode) {
		classNode.fields.add(createTestAspectField());
	}

	private void instrumentConstructors(ClassNode classNode) {
		for (MethodNode method : getRootConstructors(classNode)) {
			List<InsnNode> rets = findReturn(method.instructions);
			for (InsnNode ret : rets) {
				method.instructions.insertBefore(ret, createTestAspectInitializer(classNode));
			}
		}
	}

	private void instrumentMethods(ClassNode classNode) {
		for (MethodNode method : getSnapshotMethods(classNode)) {
			LabelNode tryLabel = new LabelNode();
			LabelNode catchLabel = new LabelNode();
			LabelNode finallyLabel = new LabelNode();
			method.tryCatchBlocks.add(createTryCatchBlock(tryLabel, catchLabel));
			method.instructions.insert(createTry(tryLabel, setupVariables(classNode, method)));
			List<InsnNode> rets = findReturn(method.instructions);
			for (InsnNode ret : rets) {
				method.instructions.insert(ret, new JumpInsnNode(GOTO, finallyLabel));
				method.instructions.remove(ret);
			}
			int returnOpcode = rets.stream()
				.map(ret -> ret.getOpcode())
				.distinct()
				.findFirst()
				.orElse(RETURN);
			method.instructions.add(createCatchFinally(catchLabel, throwVariables(classNode, method), finallyLabel, expectVariables(classNode, method), new InsnNode(returnOpcode)));
		}
	}

	private FieldNode createTestAspectField() {
		FieldNode fieldNode = new FieldNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC, SNAPSHOT_GENERATOR_FIELD_NAME, Type.getDescriptor(SnapshotGenerator.class), Type.getDescriptor(SnapshotGenerator.class), null);
		fieldNode.visibleAnnotations = new ArrayList<>();
		fieldNode.visibleAnnotations.add(new AnnotationNode(Type.getDescriptor(SnapshotExcluded.class)));
		return fieldNode;
	}

	private List<MethodNode> getRootConstructors(ClassNode classNode) {
		return (classNode.methods).stream()
			.filter(method -> isConstructor(method))
			.filter(method -> isRoot(method, classNode.name))
			.collect(toList());
	}

	private boolean isConstructor(MethodNode method) {
		return method.name.equals(CONSTRUCTOR_NAME);
	}

	private boolean isRoot(MethodNode method, String name) {
		return stream(method.instructions.iterator())
			.filter(insn -> insn.getOpcode() == Opcodes.INVOKESPECIAL && insn instanceof MethodInsnNode)
			.map(insn -> (MethodInsnNode) insn)
			.filter(insn -> insn.name.equals(CONSTRUCTOR_NAME))
			.noneMatch(insn -> insn.owner != null && insn.owner.equals(name));
	}

	private <T> Stream<T> stream(Iterator<T> iterator) {
		Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
		return StreamSupport.stream(spliterator, false);
	}

	private List<InsnNode> findReturn(InsnList instructions) {
		return stream(instructions.iterator())
			.filter(insn -> insn instanceof InsnNode)
			.map(insn -> (InsnNode) insn)
			.filter(insn -> IRETURN <= insn.getOpcode() && insn.getOpcode() <= RETURN)
			.collect(toList());
	}

	private InsnList createTestAspectInitializer(ClassNode classNode) {
		InsnList insnList = new InsnList();

		insnList.add(new VarInsnNode(ALOAD, 0));
		insnList.add(new TypeInsnNode(NEW, Type.getDescriptor(SnapshotGenerator.class)));
		insnList.add(new InsnNode(DUP));
		insnList.add(new VarInsnNode(ALOAD, 0));
		insnList.add(new LdcInsnNode(Type.getType(config.getClass())));
		insnList.add(new MethodInsnNode(INVOKESPECIAL, Type.getDescriptor(SnapshotGenerator.class), CONSTRUCTOR_NAME,
			Type.getConstructorDescriptor(constructorOf(SnapshotGenerator.class, Object.class, Class.class)), false));
		insnList.add(new FieldInsnNode(PUTFIELD, classNode.name, SNAPSHOT_GENERATOR_FIELD_NAME, Type.getDescriptor(SnapshotGenerator.class)));

		insnList.add(new VarInsnNode(ALOAD, 0));
		insnList.add(new FieldInsnNode(GETFIELD, classNode.name, SNAPSHOT_GENERATOR_FIELD_NAME, Type.getDescriptor(SnapshotGenerator.class)));
		for (MethodNode methodNode : getSnapshotMethods(classNode)) {
			insnList.add(new InsnNode(DUP));
			insnList.add(new LdcInsnNode(methodNode.name + methodNode.desc));
			
			insnList.add(pushMethod(classNode, methodNode));
			
			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getDescriptor(SnapshotGenerator.class), "register", Type.getMethodDescriptor(methodOf(SnapshotGenerator.class, "register", String.class, Method.class)), false));
		}
		insnList.add(new InsnNode(POP));

		return insnList;
	}

	private InsnList pushMethod(ClassNode clazz, MethodNode method) {
		Type[] argumentTypes = Type.getArgumentTypes(method.desc);
		int argCount = argumentTypes.length;;
		
		InsnList insnList = new InsnList();

		insnList.add(new VarInsnNode(ALOAD, 0));
		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getDescriptor(Object.class), "getClass", Type.getMethodDescriptor(methodOf(Object.class, "getClass")), false));
		
		
		insnList.add(new LdcInsnNode(method.name));
		
		insnList.add(new LdcInsnNode(argCount));
		insnList.add(new TypeInsnNode(Opcodes.ANEWARRAY, Type.getDescriptor(Class.class)));
		for (int i = 0; i < argCount; i++) {
			insnList.add(new InsnNode(DUP));
			insnList.add(new LdcInsnNode(i));
			insnList.add(pushType(argumentTypes[i]));
			insnList.add(new InsnNode(AASTORE));
		}
		
		insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getDescriptor(TypeHelper.class), "getDeclaredMethod", Type.getMethodDescriptor(methodOf(TypeHelper.class, "getDeclaredMethod", Class.class, String.class, Class[].class)), false));
		return insnList;
	}

	private List<MethodNode> getSnapshotMethods(ClassNode classNode) {
		return ((List<MethodNode>) classNode.methods).stream()
			.filter(method -> isSnapshotMethod(method))
			.collect(toList());
	}

	private boolean isSnapshotMethod(MethodNode method) {
		if (method.visibleAnnotations == null) {
			return false;
		}
		return method.visibleAnnotations.stream()
			.anyMatch(annotation -> annotation.desc.equals(Type.getDescriptor(Snapshot.class)));
	}

	private TryCatchBlockNode createTryCatchBlock(LabelNode tryLabel, LabelNode catchLabel) {
		return new TryCatchBlockNode(tryLabel, catchLabel, catchLabel, null);
	}

	private InsnList createTry(LabelNode tryLabel, InsnList onBegin) {
		InsnList insnList = new InsnList();
		insnList.add(tryLabel);
		insnList.add(onBegin);
		return insnList;
	}

	private InsnList createCatchFinally(LabelNode catchLabel, InsnList onError, LabelNode finallyLabel, InsnList onSuccess, InsnNode ret) {
		InsnList insnList = new InsnList();
		insnList.add(new JumpInsnNode(GOTO, finallyLabel));
		insnList.add(catchLabel);
		insnList.add(onError);
		insnList.add(new InsnNode(ATHROW));
		insnList.add(finallyLabel);
		insnList.add(onSuccess);
		insnList.add(ret);
		return insnList;
	}

	private InsnList setupVariables(ClassNode classNode, MethodNode methodNode) {
		InsnList insnList = new InsnList();

		insnList.add(new VarInsnNode(ALOAD, 0));
		insnList.add(new FieldInsnNode(GETFIELD, classNode.name, SNAPSHOT_GENERATOR_FIELD_NAME, Type.getDescriptor(SnapshotGenerator.class)));

		insnList.add(new LdcInsnNode(methodNode.name + methodNode.desc));

		insnList.add(pushMethodArguments(methodNode));

		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getDescriptor(SnapshotGenerator.class), "setupVariables",
			Type.getMethodDescriptor(methodOf(SnapshotGenerator.class, "setupVariables", String.class, Object[].class)), false));

		return insnList;
	}

	private InsnList expectVariables(ClassNode classNode, MethodNode methodNode) {
		int newLocal = methodNode.maxLocals;

		InsnList insnList = new InsnList();
		Type returnType = Type.getReturnType(methodNode.desc);

		if (returnType.getSize() == 1) {
			insnList.add(new InsnNode(DUP));
			insnList.add(boxPrimitives(returnType));
			insnList.add(new VarInsnNode(ASTORE, newLocal));
		} else if (returnType.getSize() == 2) {
			insnList.add(new InsnNode(DUP2));
			insnList.add(boxPrimitives(returnType));
			insnList.add(new VarInsnNode(ASTORE, newLocal));
		}

		insnList.add(new VarInsnNode(ALOAD, 0));
		insnList.add(new FieldInsnNode(GETFIELD, classNode.name, SNAPSHOT_GENERATOR_FIELD_NAME, Type.getDescriptor(SnapshotGenerator.class)));

		if (returnType.getSize() > 0) {
			insnList.add(new VarInsnNode(ALOAD, newLocal));
		}

		insnList.add(pushMethodArguments(methodNode));

		if (returnType.getSize() > 0) {
			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getDescriptor(SnapshotGenerator.class), "expectVariables",
				Type.getMethodDescriptor(methodOf(SnapshotGenerator.class, "expectVariables", Object.class, Object[].class)), false));
		} else {
			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getDescriptor(SnapshotGenerator.class), "expectVariables",
				Type.getMethodDescriptor(methodOf(SnapshotGenerator.class, "expectVariables", Object[].class)), false));
		}

		return insnList;
	}

	private InsnList throwVariables(ClassNode classNode, MethodNode methodNode) {
		InsnList insnList = new InsnList();

		insnList.add(new InsnNode(DUP));

		insnList.add(new VarInsnNode(ALOAD, 0));
		insnList.add(new FieldInsnNode(GETFIELD, classNode.name, SNAPSHOT_GENERATOR_FIELD_NAME, Type.getDescriptor(SnapshotGenerator.class)));

		insnList.add(new InsnNode(SWAP));

		insnList.add(pushMethodArguments(methodNode));

		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getDescriptor(SnapshotGenerator.class), "throwVariables",
			Type.getMethodDescriptor(methodOf(SnapshotGenerator.class, "throwVariables", Throwable.class, Object[].class)), false));

		return insnList;
	}

	private InsnList pushMethodArguments(MethodNode methodNode) {
		int params = Type.getArgumentTypes(methodNode.desc).length;

		InsnList insnList = new InsnList();

		insnList.add(new LdcInsnNode(params));
		insnList.add(new TypeInsnNode(Opcodes.ANEWARRAY, Type.getDescriptor(Object.class)));

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

	private AbstractInsnNode pushType(Type type) {
		if (type.getDescriptor().length() == 1) {
			Class<?> boxedType = getBoxedType(type.getDescriptor().charAt(0));
			return new FieldInsnNode(GETSTATIC, Type.getDescriptor(boxedType), "TYPE", Type.getDescriptor(Class.class));
		} else {
			return new LdcInsnNode(type);
		}
	}

	private InsnList boxPrimitives(Type type) {
		InsnList insnList = new InsnList();
		if (type.getDescriptor().length() == 1) {
			char desc = type.getDescriptor().charAt(0);
			Class<?> raw = getRawType(desc);
			Class<?> boxed = getBoxedType(desc);

			Method method = methodOf(boxed, "valueOf", raw);
			insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getDescriptor(boxed), "valueOf", Type.getMethodDescriptor(method), false));
		}
		return insnList;
	}

	public Class<?> saveClass(String className, byte[] b) {
		try {
			ClassLoader loader = ClassLoader.getSystemClassLoader();
			Class<?> cls = Class.forName(CLASS_LOADER);

			Method defineClass = cls.getDeclaredMethod(DEFINE_CLASS, new Class[] { String.class, byte[].class, int.class, int.class });

			try {
				defineClass.setAccessible(true);
				Object[] args = new Object[] { className, b, new Integer(0), new Integer(b.length) };
				Class<?> clazz = (Class<?>) defineClass.invoke(loader, args);
				if (clazz.getPackage() == null) {
					createPackage(className, loader, cls);
				}
				return clazz;
			} finally {
				defineClass.setAccessible(false);
			}
		} catch (Exception e) {
			throw new SerializationException(e);
		}
	}

	private void createPackage(String className, ClassLoader loader, Class<?> cls) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		int packageEnd = className.lastIndexOf('.');
		String pkg = className.substring(0,packageEnd );
		
		Method definePackage = cls.getDeclaredMethod(DEFINE_PACKAGE, new Class[] {
			String.class, String.class, String.class, String.class, String.class, String.class, String.class, URL.class
		});
		try {
			definePackage.setAccessible(true);
			Object[] args = new Object[] { pkg, null, null, null, null, null, null, null };
			definePackage.invoke(loader, args);
		} finally {
			definePackage.setAccessible(false);
		}
	}

	private <T> Constructor<T> constructorOf(Class<T> clazz, Class<?>... arguments) {
		try {
			return clazz.getConstructor(arguments);
		} catch (NoSuchMethodException e) {
			throw new SerializationException(e);
		}
	}

	private Method methodOf(Class<?> clazz, String name, Class<?>... arguments) {
		try {
			return clazz.getMethod(name, arguments);
		} catch (NoSuchMethodException e) {
			throw new SerializationException(e);
		}
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
