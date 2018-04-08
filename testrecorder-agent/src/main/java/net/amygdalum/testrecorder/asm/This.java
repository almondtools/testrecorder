package net.amygdalum.testrecorder.asm;


import static org.objectweb.asm.Opcodes.ALOAD;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.VarInsnNode;

public class This implements SequenceInstruction {

	public This() {
	}

	@Override
	public InsnList build(MethodContext context) {
		return list(new VarInsnNode(ALOAD, 0));
	}

}
