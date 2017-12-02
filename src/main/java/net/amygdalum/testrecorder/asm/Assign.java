package net.amygdalum.testrecorder.asm;

import static org.objectweb.asm.Opcodes.ISTORE;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.VarInsnNode;

public class Assign implements SequenceInstruction {

	private String variableName;
	private SequenceInstruction value;
	private Type type;

	public Assign(String variableName, Type type) {
		this.variableName = variableName;
		this.type = type;
	}
	
	public Assign value(SequenceInstruction value) {
		this.value = value;
		return this;
	}

	@Override
	public InsnList build(Sequence sequence) {
		InsnList insnList = new InsnList();
		insnList.add(value.build(sequence));
		Local local = sequence.newLocal(variableName, type);
		insnList.add(new VarInsnNode(local.type.getOpcode(ISTORE), local.index));
		return insnList;
	}

}
