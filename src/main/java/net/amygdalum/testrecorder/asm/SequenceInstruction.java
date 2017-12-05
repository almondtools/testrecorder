package net.amygdalum.testrecorder.asm;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public interface SequenceInstruction {

	InsnList build(MethodContext context);

	default InsnList list(AbstractInsnNode... insnNodes) {
		InsnList insnList = new InsnList();
		for (AbstractInsnNode insnNode : insnNodes) {
			insnList.add(insnNode);
		}
		return insnList;
	}

}
