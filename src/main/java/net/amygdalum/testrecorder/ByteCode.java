package net.amygdalum.testrecorder;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP2;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class ByteCode {

	private ByteCode() {
	}
	
	public static List<LocalVariableNode> range(List<LocalVariableNode> locals, int start, int length) {
		return locals.stream()
			.filter(local -> local.index >= start && local.index < start + length)
			.sorted(comparingInt(local -> local.index))
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
		int params = argumentTypes.length;
		
		InsnList insnList = new InsnList();
		
		insnList.add(new LdcInsnNode(params));
		insnList.add(new TypeInsnNode(Opcodes.ANEWARRAY, Type.getInternalName(Object.class)));
		
		for (int i = 0; i < params; i++) {
			insnList.add(new InsnNode(DUP));
			insnList.add(new LdcInsnNode(i));

			Type type = argumentTypes[i];
			int index = locals[i];
			
			insnList.add(new VarInsnNode(type.getOpcode(ILOAD), index));
			
			insnList.add(boxPrimitives(type));
			
			insnList.add(new InsnNode(AASTORE));
		}
		return insnList;
	}

	public static InsnList pushAsArray(List<LocalVariableNode> locals, Type... argumentTypes) {
		int params = argumentTypes.length;
		
		InsnList insnList = new InsnList();
		
		insnList.add(new LdcInsnNode(params));
		insnList.add(new TypeInsnNode(Opcodes.ANEWARRAY, Type.getInternalName(Object.class)));
		
		for (int i = 0; i < params; i++) {
			insnList.add(new InsnNode(DUP));
			insnList.add(new LdcInsnNode(i));
			LocalVariableNode node = (LocalVariableNode) locals.get(i);
			Type type = Type.getType(node.desc);
			
			insnList.add(new VarInsnNode(type.getOpcode(ILOAD), node.index));
			
			insnList.add(boxPrimitives(type));
			
			insnList.add(new InsnNode(AASTORE));
		}
		return insnList;
	}
	
	public static AbstractInsnNode pushType(Type type) {
		if (type.getDescriptor().length() == 1) {
			Class<?> boxedType = getBoxedType(type.getDescriptor().charAt(0));
			return new FieldInsnNode(GETSTATIC, Type.getInternalName(boxedType), "TYPE", Type.getDescriptor(Class.class));
		} else {
			return new LdcInsnNode(type);
		}
	}

	public static InsnList boxPrimitives(Type type) {
		InsnList insnList = new InsnList();
		if (type.getDescriptor().length() == 1) {
			char desc = type.getDescriptor().charAt(0);
			Class<?> raw = getRawType(desc);
			Class<?> boxed = getBoxedType(desc);

			insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(boxed), "valueOf", methodDescriptor(boxed, "valueOf", raw), false));
		}
		return insnList;
	}

	public static InsnList unboxPrimitives(Type type) {
		InsnList insnList = new InsnList();
		if (type.getDescriptor().length() == 1) {
			char desc = type.getDescriptor().charAt(0);
			Class<?> raw = getRawType(desc);
			Class<?> boxed = getBoxedType(desc);

			insnList.add(new TypeInsnNode(CHECKCAST, Type.getInternalName(boxed)));

			String methodName = raw.getSimpleName() + "Value";
			insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Type.getInternalName(boxed), methodName, methodDescriptor(boxed, methodName), false));
		}
		return insnList;
	}

	public static String constructorDescriptor(Class<?> clazz, Class<?>... arguments) {
		return Type.getConstructorDescriptor(constructorOf(clazz, arguments));	
	}
	
	private static <T> Constructor<T> constructorOf(Class<T> clazz, Class<?>... arguments) {
		try {
			return clazz.getConstructor(arguments);
		} catch (NoSuchMethodException e) {
			throw new SerializationException(e);
		}
	}

	public static String methodDescriptor(Class<?> clazz, String name, Class<?>... arguments) {
		return Type.getMethodDescriptor(methodOf(clazz, name, arguments));	
	}
	
	private static Method methodOf(Class<?> clazz, String name, Class<?>... arguments) {
		try {
			return clazz.getMethod(name, arguments);
		} catch (NoSuchMethodException e) {
			throw new SerializationException(e);
		}
	}

	public static Class<?> getRawType(char desc) {
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

	public static Class<?> getBoxedType(char desc) {
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
