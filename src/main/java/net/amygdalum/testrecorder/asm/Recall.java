package net.amygdalum.testrecorder.asm;

import static org.objectweb.asm.Opcodes.ALOAD;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.VarInsnNode;

import net.amygdalum.testrecorder.util.ByteCode;

public class Recall implements SequenceInstruction {

	private String variableName;

	public Recall(String variableName) {
		this.variableName = variableName;
	}

	@Override
	public InsnList build(Sequence sequence) {
		int local = sequence.local(variableName);
		return ByteCode.list(new VarInsnNode(ALOAD, local));
	}

}
