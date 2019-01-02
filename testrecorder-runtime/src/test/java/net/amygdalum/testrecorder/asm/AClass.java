package net.amygdalum.testrecorder.asm;

import java.io.IOException;
import java.util.Arrays;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class AClass {

	private static ClassNode NODE = createClassNode();
	private static final MethodNode VIRTUAL_METHOD = NODE.methods.stream()
		.filter(m -> m.name.equals("virtualMethod"))
		.findFirst()
		.orElse(null);
	private static final MethodNode STATIC_METHOD = NODE.methods.stream()
		.filter(m -> m.name.startsWith("static"))
		.findFirst()
		.orElse(null);

	public static ClassNode classNode() {
		return NODE;
	}

	public void virtualMethod() {
	}

	public static MethodNode virtualMethodNode() {
		return new MethodNode(VIRTUAL_METHOD.access, VIRTUAL_METHOD.name, VIRTUAL_METHOD.desc, VIRTUAL_METHOD.signature, VIRTUAL_METHOD.exceptions.toArray(new String[0]));
	}

	public static MethodNode virtualMethodNodeReturning(Class<?> clazz) {
		return new MethodNode(VIRTUAL_METHOD.access, VIRTUAL_METHOD.name, modifyReturn(VIRTUAL_METHOD.desc, clazz), VIRTUAL_METHOD.signature, VIRTUAL_METHOD.exceptions.toArray(new String[0]));
	}

	private static String modifyReturn(String desc, Class<?> clazz) {
		Type[] args = Type.getArgumentTypes(desc);
		return Type.getMethodDescriptor(Type.getType(clazz), args);
	}

	public static MethodNode virtualMethodNodePassing(Class<?>... clazzes) {
		return new MethodNode(VIRTUAL_METHOD.access, VIRTUAL_METHOD.name, modifyArguments(VIRTUAL_METHOD.desc, clazzes), VIRTUAL_METHOD.signature, VIRTUAL_METHOD.exceptions.toArray(new String[0]));
	}

	private static String modifyArguments(String desc, Class<?>... clazzes) {
		Type result = Type.getReturnType(desc);
		return Type.getMethodDescriptor(result, Arrays.stream(clazzes)
			.map(clazz -> Type.getType(clazz))
			.toArray(Type[]::new));
	}

	public static void staticMethod() {
	}

	public static MethodNode staticMethodNode() {
		return new MethodNode(STATIC_METHOD.access, STATIC_METHOD.name, STATIC_METHOD.desc, STATIC_METHOD.signature, STATIC_METHOD.exceptions.toArray(new String[0]));
	}

	private static ClassNode createClassNode() {
		try {
			ClassNode node = new ClassNode();
			ClassReader reader = new ClassReader(AClass.class.getName());
			reader.accept(node, 0);

			return node;
		} catch (IOException e) {
			return null;
		}
	}

}
