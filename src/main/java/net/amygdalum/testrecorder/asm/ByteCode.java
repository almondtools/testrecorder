package net.amygdalum.testrecorder.asm;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACC_NATIVE;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP2;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import java.lang.reflect.Array;
import java.util.List;
import java.util.stream.IntStream;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import net.amygdalum.testrecorder.util.Types;

public final class ByteCode {

	private static final PrimitiveTypeInfo[] PRIMITIVES = new PrimitiveTypeInfo[] {
		new PrimitiveTypeInfo('Z', Type.BOOLEAN, boolean.class, Boolean.class, "valueOf", "booleanValue"),
		new PrimitiveTypeInfo('C', Type.CHAR, char.class, Character.class, "valueOf", "charValue"),
		new PrimitiveTypeInfo('B', Type.BYTE, byte.class, Byte.class, "valueOf", "byteValue"),
		new PrimitiveTypeInfo('S', Type.SHORT, short.class, Short.class, "valueOf", "shortValue"),
		new PrimitiveTypeInfo('I', Type.INT, int.class, Integer.class, "valueOf", "intValue"),
		new PrimitiveTypeInfo('J', Type.LONG, long.class, Long.class, "valueOf", "longValue"),
		new PrimitiveTypeInfo('F', Type.FLOAT, float.class, Float.class, "valueOf", "floatValue"),
		new PrimitiveTypeInfo('D', Type.DOUBLE, double.class, Double.class, "valueOf", "doubleValue"),
		new PrimitiveTypeInfo('V', Type.VOID, void.class, Void.class, null, null)
	};

	private ByteCode() {
	}

	private static PrimitiveTypeInfo primitive(char desc) {
		for (PrimitiveTypeInfo info : PRIMITIVES) {
			if (info.desc == desc) {
				return info;
			}
		}
		return null;
	}

	private static PrimitiveTypeInfo primitive(int sort) {
		for (PrimitiveTypeInfo info : PRIMITIVES) {
			if (info.sort == sort) {
				return info;
			}
		}
		return null;
	}

	public static boolean isStatic(MethodNode methodNode) {
		return (methodNode.access & ACC_STATIC) != 0;
	}

	public static boolean isNative(MethodNode methodNode) {
		return (methodNode.access & ACC_NATIVE) != 0;
	}

	public static boolean returnsResult(MethodNode methodNode) {
		return Type.getReturnType(methodNode.desc).getSize() > 0;
	}

	public static boolean isPrimitive(Type type) {
		return type.getDescriptor().length() == 1;
	}

	public static AbstractInsnNode primitiveNull(Type type) {
		char desc = type.getDescriptor().charAt(0);
		switch (desc) {
		case 'C':
		case 'Z':
		case 'B':
		case 'S':
		case 'I':
			return new InsnNode(Opcodes.ICONST_0);
		case 'J':
			return new InsnNode(Opcodes.LCONST_0);
		case 'F':
			return new InsnNode(Opcodes.FCONST_0);
		case 'D':
			return new InsnNode(Opcodes.DCONST_0);
		default:
			return null;
		}
	}

	public static List<LocalVariableNode> range(List<LocalVariableNode> locals, int start, int length) {
		return locals.stream()
			.sorted(comparingInt(local -> local.index))
			.skip(start)
			.limit(length)
			.collect(toList());
	}

	public static InsnList memorizeLocal(Type type, int newLocal) {
		InsnList insnList = new InsnList();
		if (type.getSize() == 1) {
			insnList.add(new InsnNode(DUP));
			insnList.add(boxPrimitives(type));
			insnList.add(new VarInsnNode(ASTORE, newLocal));
		} else if (type.getSize() == 2) {
			insnList.add(new InsnNode(DUP2));
			insnList.add(boxPrimitives(type));
			insnList.add(new VarInsnNode(ASTORE, newLocal));
		}
		return insnList;
	}

	public static AbstractInsnNode recallLocal(int newLocal) {
		return new VarInsnNode(ALOAD, newLocal);
	}

	public static InsnList pushTypes(Type... argumentTypes) {
		int params = argumentTypes.length;

		InsnList insnList = new InsnList();

		insnList.add(new LdcInsnNode(params));
		insnList.add(new TypeInsnNode(Opcodes.ANEWARRAY, Type.getInternalName(java.lang.reflect.Type.class)));

		for (int i = 0; i < params; i++) {
			insnList.add(new InsnNode(DUP));
			insnList.add(new LdcInsnNode(i));
			insnList.add(pushType(argumentTypes[i]));
			insnList.add(new InsnNode(AASTORE));
		}
		return insnList;

	}

	public static InsnList pushAsArray(int[] locals, Type... argumentTypes) {
		LocalVar[] localvars = IntStream.range(0, locals.length)
			.mapToObj(i -> new LocalVar(locals[i], argumentTypes[i]))
			.toArray(LocalVar[]::new);
		return pushAsArray(localvars);
	}

	public static InsnList pushAsArray(List<LocalVariableNode> locals) {
		LocalVar[] localvars = locals.stream()
			.map(local -> new LocalVar(local.index, Type.getType(local.desc)))
			.toArray(LocalVar[]::new);
		return pushAsArray(localvars);
	}

	private static InsnList pushAsArray(LocalVar[] locals) {
		int params = locals.length;

		InsnList insnList = new InsnList();

		insnList.add(new LdcInsnNode(params));
		insnList.add(new TypeInsnNode(Opcodes.ANEWARRAY, Type.getInternalName(Object.class)));

		for (int i = 0; i < params; i++) {
			insnList.add(new InsnNode(DUP));
			insnList.add(new LdcInsnNode(i));

			LocalVar local = locals[i];
			int index = local.index;
			Type type = local.type;

			insnList.add(new VarInsnNode(type.getOpcode(ILOAD), index));

			insnList.add(boxPrimitives(type));

			insnList.add(new InsnNode(AASTORE));
		}
		return insnList;
	}

	public static AbstractInsnNode pushType(Type type) {
		if (isPrimitive(type)) {
			char desc = type.getDescriptor().charAt(0);
			String boxedType = primitive(desc).boxedType;
			return new FieldInsnNode(GETSTATIC, boxedType, "TYPE", Type.getDescriptor(Class.class));
		} else {
			return new LdcInsnNode(type);
		}
	}

	public static InsnList boxPrimitives(Type type) {
		InsnList insnList = new InsnList();
		if (isPrimitive(type)) {
			char desc = type.getDescriptor().charAt(0);
			PrimitiveTypeInfo info = primitive(desc);

			String factoryDesc = "(" + info.desc + ")L" + info.boxedType + ";";
			insnList.add(new MethodInsnNode(INVOKESTATIC, info.boxedType, info.boxingFactory, factoryDesc, false));
		}
		return insnList;
	}

	public static Type boxedType(Type type) {
		if (isPrimitive(type)) {
			char desc = type.getDescriptor().charAt(0);
			PrimitiveTypeInfo info = primitive(desc);
			return Type.getType(info.boxedClass);
		}
		return type;
	}

	public static InsnList unboxPrimitives(Type type) {
		char desc = type.getDescriptor().charAt(0);

		InsnList insnList = new InsnList();

		String factoryDesc = "()" + desc;
		String boxedClassName = primitive(desc).boxedType;
		String factoryName = getUnboxingFactory(desc);

		insnList.add(new TypeInsnNode(CHECKCAST, boxedClassName));
		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, boxedClassName, factoryName, factoryDesc, false));
		return insnList;
	}

	private static String getUnboxingFactory(char desc) {
		return primitive(desc).unboxingFactory;
	}

	public static InsnList list(AbstractInsnNode... insnNodes) {
		InsnList insnList = new InsnList();
		for (AbstractInsnNode insnNode : insnNodes) {
			insnList.add(insnNode);
		}
		return insnList;
	}

	public static String constructorDescriptor(Class<?> clazz, Class<?>... arguments) {
		try {
			return Type.getConstructorDescriptor(Types.getDeclaredConstructor(clazz, arguments));
		} catch (NoSuchMethodException e) {
			throw new ByteCodeException(e);
		}
	}

	public static String fieldDescriptor(Class<?> clazz, String name) {
		try {
			return Type.getDescriptor(Types.getDeclaredField(clazz, name).getType());
		} catch (NoSuchFieldException e) {
			throw new ByteCodeException(e);
		}
	}

	public static String methodDescriptor(Class<?> clazz, String name, Class<?>... arguments) {
		try {
			return Type.getMethodDescriptor(Types.getDeclaredMethod(clazz, name, arguments));
		} catch (NoSuchMethodException e) {
			throw new ByteCodeException(e);
		}
	}

	public static Class<?> classFrom(String name) {
		return classFrom(Type.getObjectType(name), ByteCode.class.getClassLoader());
	}

	public static Class<?> classFrom(String name, ClassLoader loader) {
		return classFrom(Type.getObjectType(name), loader);
	}

	public static Class<?> classFrom(Type type) {
		return classFrom(type, ByteCode.class.getClassLoader());
	}

	public static Class<?> classFrom(Type type, ClassLoader loader) {
		try {
			int arrayDimensions = 0;
			
			if (type.getSort() == Type.ARRAY) {
				arrayDimensions +=type.getDimensions();
				type = type.getElementType();
			}
			PrimitiveTypeInfo primitive = primitive(type.getSort());
			Class<?> clazz = primitive != null ? primitive.rawClass : loader.loadClass(type.getClassName());
			for (int i = 0; i < arrayDimensions; i++) {
				clazz = Array.newInstance(clazz, 0).getClass();
			}
			return clazz;
		} catch (ClassNotFoundException e) {
			throw new ByteCodeException(e);
		}
	}

	public static Class<?>[] argumentTypesFrom(String desc) {
		return argumentTypesFrom(desc, ByteCode.class.getClassLoader());
	}

	public static Class<?>[] argumentTypesFrom(String desc, ClassLoader loader) {
		Type[] argumentTypeDescriptions = Type.getMethodType(desc).getArgumentTypes();
		Class<?>[] argumentTypes = new Class<?>[argumentTypeDescriptions.length];
		for (int i = 0; i < argumentTypes.length; i++) {
			argumentTypes[i] = classFrom(argumentTypeDescriptions[i], loader);
		}
		return argumentTypes;
	}

	public static Class<?> resultTypeFrom(String desc) {
		return resultTypeFrom(desc, ByteCode.class.getClassLoader());
	}

	public static Class<?> resultTypeFrom(String desc, ClassLoader loader) {
		Type resultTypeDescription = Type.getMethodType(desc).getReturnType();
		return classFrom(resultTypeDescription, loader);
	}
	
	public static List<String> toString(InsnList instructions) {
		Printer p = new Textifier();
		TraceMethodVisitor mp = new TraceMethodVisitor(p);
		instructions.accept(mp);
		return p.getText().stream()
			.map(Object::toString)
			.map(String::trim)
			.collect(toList());
	}

	public static String toString(AbstractInsnNode instruction) {
		Printer p = new Textifier();
		TraceMethodVisitor mp = new TraceMethodVisitor(p);
		instruction.accept(mp);
		String text = p.getText().stream()
			.map(Object::toString)
			.collect(joining("\n"));
		return text;
	}

	private static class LocalVar {

		public int index;
		public Type type;

		public LocalVar(int index, Type type) {
			this.index = index;
			this.type = type;
		}

	}

	@SuppressWarnings("unused")
	private static class PrimitiveTypeInfo {
		public char desc;
		public int sort;
		public Class<?> rawClass;
		public String rawType;
		public Class<?> boxedClass;
		public String boxedType;
		public String boxingFactory;
		public String unboxingFactory;

		public PrimitiveTypeInfo(char desc, int sort, Class<?> raw, Class<?> boxed, String boxingFactory, String unboxingFactory) {
			this.desc = desc;
			this.sort = sort;
			this.rawClass = raw;
			this.rawType = raw.getName();
			this.boxedClass = boxed;
			this.boxedType = Type.getInternalName(boxed);
			this.boxingFactory = boxingFactory;
			this.unboxingFactory = unboxingFactory;
		}

	}

	public static InsnList print(InsnList instructions) {
		List<String> text = toString(instructions);
		System.out.println(text);
		return instructions;
	}

	public static <T extends AbstractInsnNode> T print(T node) {
		String text = toString(node);
		System.out.println(text);
		return node;
	}

}
