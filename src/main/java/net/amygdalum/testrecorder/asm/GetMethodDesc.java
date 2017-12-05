package net.amygdalum.testrecorder.asm;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;

public class GetMethodDesc implements SequenceInstruction {

	public GetMethodDesc() {
	}

	@Override
	public InsnList build(MethodContext context) {
		return list(new LdcInsnNode(context.getMethodDescriptor()));
	}

}
