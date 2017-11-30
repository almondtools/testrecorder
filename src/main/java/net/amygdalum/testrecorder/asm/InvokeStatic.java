package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.util.ByteCode.methodDescriptor;
import static net.bytebuddy.jar.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

public class InvokeStatic implements SequenceInstruction {

	private Class<?> clazz;
	private String method;
	private Class<?>[] argumentTypes;

	private SequenceInstruction[] arguments;

	public InvokeStatic(Class<?> clazz, String method, Class<?>... argumentTypes) {
		this.clazz = clazz;
		this.method = method;
		this.argumentTypes = argumentTypes;
		this.arguments = new SequenceInstruction[argumentTypes.length];
	}

	public InvokeStatic withArgument(int i, SequenceInstruction argument) {
		arguments[i] = argument;
		return this;
	}

	@Override
	public InsnList build(Sequence sequence) {
		InsnList insnList = new InsnList();

		for (SequenceInstruction argument : arguments) {
			insnList.add(argument.build(sequence));
		}

		insnList.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(clazz), method, methodDescriptor(clazz, method, argumentTypes), false));

		return insnList;
	}

}
