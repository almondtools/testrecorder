package net.amygdalum.testrecorder.asm;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public abstract class AbstractInsnListBuilder implements InsnListBuilder {

	public InsnList list(AbstractInsnNode insnNode) {
		InsnList insnList = new InsnList();
		insnList.add(insnNode);
		return insnList;
	}

}
