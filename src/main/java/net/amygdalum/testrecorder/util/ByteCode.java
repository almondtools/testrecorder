package net.amygdalum.testrecorder.util;

import static java.lang.Class.forName;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP2;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

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
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import net.amygdalum.testrecorder.SerializationException;

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

	public static String constructorDescriptor(Class<?> clazz, Class<?>... arguments) {
		try {
			return Type.getConstructorDescriptor(Types.getDeclaredConstructor(clazz, arguments));
		} catch (NoSuchMethodException e) {
			throw new SerializationException(e);
		}
	}

	public static String methodDescriptor(Class<?> clazz, String name, Class<?>... arguments) {
		try {
			return Type.getMethodDescriptor(Types.getDeclaredMethod(clazz, name, arguments));
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

	public static Class<?> classFromInternalName(String name, ClassLoader loader) throws ClassNotFoundException {
		switch (name) {
		case "boolean":
			return boolean.class;
		case "char":
			return char.class;
		case "byte":
			return byte.class;
		case "short":
			return short.class;
		case "int":
			return int.class;
		case "float":
			return float.class;
		case "long":
			return long.class;
		case "double":
			return double.class;
		case "void":
			return void.class;
		default:
			return loader.loadClass(name.replace('/', '.'));
		}
	}

	public static Class<?> classFromInternalName(String name) throws ClassNotFoundException {
		switch (name) {
		case "boolean":
			return boolean.class;
		case "char":
			return char.class;
		case "byte":
			return byte.class;
		case "short":
			return short.class;
		case "int":
			return int.class;
		case "float":
			return float.class;
		case "long":
			return long.class;
		case "double":
			return double.class;
		case "void":
			return void.class;
		default:
			return forName(name.replace('/', '.'));
		}
	}

	public static Class<?>[] getArgumentTypes(String signature) throws ClassNotFoundException {
		org.objectweb.asm.Type[] argumentTypeDescriptions = org.objectweb.asm.Type.getMethodType(signature).getArgumentTypes();
		Class<?>[] argumentTypes = new Class<?>[argumentTypeDescriptions.length];
		for (int i = 0; i < argumentTypes.length; i++) {
			argumentTypes[i] = classFromInternalName(argumentTypeDescriptions[i].getClassName());
		}
		return argumentTypes;
	}

	public static List<String> toString(InsnList instructions) {
		Printer p = new Textifier();
		TraceMethodVisitor mp = new TraceMethodVisitor(p);
		instructions.accept(mp);
		return p.getText().stream()
			.map(Object::toString)
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

}
