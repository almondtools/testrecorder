package net.amygdalum.testrecorder.asm;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.objectweb.asm.Opcodes.ACC_NATIVE;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Optional;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import net.amygdalum.testrecorder.util.Logger;
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

	private static Optional<PrimitiveTypeInfo> primitive(int sort) {
		for (PrimitiveTypeInfo info : PRIMITIVES) {
			if (info.sort == sort) {
				return Optional.of(info);
			}
		}
		return Optional.empty();
	}

	private static Optional<PrimitiveTypeInfo> primitiveWrapper(String name) {
		for (PrimitiveTypeInfo info : PRIMITIVES) {
			if (info.boxedType.equals(name)) {
				return Optional.of(info);
			}
		}
		return Optional.empty();
	}

	public static boolean isStatic(MethodNode methodNode) {
		return (methodNode.access & ACC_STATIC) != 0;
	}

	public static boolean isStatic(FieldNode methodNode) {
		return (methodNode.access & ACC_STATIC) != 0;
	}

	public static boolean isNative(MethodNode methodNode) {
		return (methodNode.access & ACC_NATIVE) != 0;
	}

	public static boolean returnsResult(MethodNode methodNode) {
		return Type.getReturnType(methodNode.desc).getSize() > 0;
	}

	public static boolean returnsResult(MethodInsnNode methodNode) {
		return Type.getReturnType(methodNode.desc).getSize() > 0;
	}

	public static boolean isPrimitive(Type type) {
		return type.getSort() < Type.ARRAY;
	}

	public static boolean isArray(Type type) {
		return type.getSort() == Type.ARRAY;
	}

	public static Type boxedType(Type type) {
		return primitive(type.getSort())
			.map(info -> Type.getType(info.boxedClass))
			.orElse(type);
	}

	public static Type unboxedType(Type type) {
		return primitive(type.getSort())
			.map(info -> Type.getType(info.rawClass))
			.orElseGet(() -> primitiveWrapper(type.getInternalName())
				.map(info -> Type.getType(info.rawClass))
				.orElse(null));
	}

	public static String boxingFactory(Type type) {
		return primitive(type.getSort())
			.map(info -> info.boxingFactory)
			.orElse(null);
	}

	public static String unboxingFactory(Type type) {
		return primitive(type.getSort())
			.map(info -> info.unboxingFactory)
			.orElse(null);
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
		int arrayDimensions = isArray(type) ? type.getDimensions() : 0;
		Type baseType = isArray(type) ? type.getElementType() : type;
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Class<?> clazz = primitive(baseType.getSort())
			.map(info -> info.rawClass)
			.orElseGet(() -> (Class) loadClass(baseType.getClassName(), loader));
		for (int i = 0; i < arrayDimensions; i++) {
			clazz = Array.newInstance(clazz, 0).getClass();
		}
		return clazz;
	}

	private static Class<?> loadClass(String name, ClassLoader loader) {
		try {
			return loader.loadClass(name);
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
		Logger.info(text);
		return instructions;
	}

	public static <T extends AbstractInsnNode> T print(T node) {
		String text = toString(node);
		Logger.info(text);
		return node;
	}

}
