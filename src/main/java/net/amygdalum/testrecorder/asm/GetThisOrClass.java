package net.amygdalum.testrecorder.asm;

import static org.objectweb.asm.Opcodes.ALOAD;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class GetThisOrClass implements SequenceInstruction {

	public GetThisOrClass() {
	}

	@Override
	public InsnList build(MethodContext context) {
		return context.isStatic()
			? list(new LdcInsnNode(context.getClassType()))
			: list(new VarInsnNode(ALOAD, 0));
	}

}
