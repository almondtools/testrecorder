package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.asm.ByteCode.methodDescriptor;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

public class InvokeVirtual implements SequenceInstruction {

	private Class<?> clazz;
	private String method;
	private Class<?>[] argumentTypes;
	
	private SequenceInstruction base;
	private SequenceInstruction[] arguments;

	public InvokeVirtual(Class<?> clazz, String method, Class<?>... argumentTypes) {
		this.clazz = clazz;
		this.method = method;
		this.argumentTypes = argumentTypes;
		this.arguments = new SequenceInstruction[argumentTypes.length];
	}

	public InvokeVirtual withBase(SequenceInstruction base) {
		this.base = base;
		return this;
	}

	public InvokeVirtual withArgument(int i, SequenceInstruction argument) {
		arguments[i] = argument;
		return this;
	}

	@Override
	public InsnList build(MethodContext context) {
		InsnList insnList = new InsnList();

		insnList.add(base.build(context));

		for (SequenceInstruction argument : arguments) {
			insnList.add(argument.build(context));
		}
		
		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(clazz), method, methodDescriptor(clazz, method, argumentTypes), false));
		
		return insnList;
	}

}
