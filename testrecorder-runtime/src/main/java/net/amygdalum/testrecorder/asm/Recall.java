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
	public InsnList build(MethodContext context) {
		Local local = context.local(variableName);
		return list(new VarInsnNode(local.type.getOpcode(ILOAD), local.index));
	}

}
