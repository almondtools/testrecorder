package net.amygdalum.testrecorder.asm;

import static org.objectweb.asm.Opcodes.ILOAD;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.VarInsnNode;

public class Recall implements SequenceInstruction {

	private String variableName;

	public Recall(String variableName) {
		this.variableName = variableName;
	}

	@Override
	public InsnList build(Sequence sequence) {
		Local local = sequence.local(variableName);
		return ByteCode.list(new VarInsnNode(local.type.getOpcode(ILOAD), local.index));
	}

}
