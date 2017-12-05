package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.asm.ByteCode.constructorDescriptor;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.NEW;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class InvokeNew implements SequenceInstruction {

	private Class<?> clazz;
	private Class<?>[] argumentTypes;
	
	private SequenceInstruction[] arguments;

	public InvokeNew(Class<?> clazz, Class<?>... argumentTypes) {
		this.clazz = clazz;
		this.argumentTypes = argumentTypes;
		this.arguments = new SequenceInstruction[argumentTypes.length];
	}

	public InvokeNew withArgument(int i, SequenceInstruction argument) {
		arguments[i] = argument;
		return this;
	}

	@Override
	public InsnList build(MethodContext context) {
		InsnList insnList = new InsnList();

		insnList.add(new TypeInsnNode(NEW, Type.getInternalName(clazz)));
		insnList.add(new InsnNode(DUP));

		for (SequenceInstruction argument : arguments) {
			insnList.add(argument.build(context));
		}
		
		insnList.add(new MethodInsnNode(INVOKESPECIAL, Type.getInternalName(clazz), "<init>", constructorDescriptor(clazz, argumentTypes), false));
		
		return insnList;
	}

}
