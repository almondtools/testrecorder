package net.amygdalum.testrecorder.asm;



import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;

public class Ldc implements SequenceInstruction {

	private Object constant;

	public Ldc(Class<?> clazz) {
		this.constant = clazz;
	}

	public Ldc(Number number) {
		this.constant = number;
	}

	public Ldc(Character character) {
		this.constant = character;
	}

	public Ldc(String stringLiteral) {
		this.constant = stringLiteral;
	}

	@Override
	public InsnList build(MethodContext context) {
		return list(new LdcInsnNode(constant));
	}

}
