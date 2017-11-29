package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.util.ByteCode.list;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;

public class Ldc implements SequenceInstruction {

	private Object constant;

	public Ldc(Class<?> clazz) {
		this.constant = clazz;
	}

	public Ldc(String stringLiteral) {
		this.constant = stringLiteral;
	}

	@Override
	public InsnList build(Sequence sequence) {
		return list(new LdcInsnNode(constant));
	}

}
