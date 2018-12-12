package net.amygdalum.testrecorder.asm;

import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class GetThisOrNull implements SequenceInstruction {

	public GetThisOrNull() {
	}

	@Override
	public InsnList build(MethodContext context) {
		return context.isStatic()
			? list(new InsnNode(ACONST_NULL))
			: list(new VarInsnNode(ALOAD, 0));
	}

}
