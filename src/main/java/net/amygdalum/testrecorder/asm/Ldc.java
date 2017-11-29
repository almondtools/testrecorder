package net.amygdalum.testrecorder.asm;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;

public class Ldc extends AbstractInsnListBuilder {

	private Object constant;

	public Ldc(Class<?> clazz) {
		this.constant = clazz;
	}

	public Ldc(String stringLiteral) {
		this.constant = stringLiteral;
	}

	@Override
	public InsnList build() {
		return list(new LdcInsnNode(constant));
	}

}
