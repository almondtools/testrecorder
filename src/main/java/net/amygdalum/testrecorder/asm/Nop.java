package net.amygdalum.testrecorder.asm;

import org.objectweb.asm.tree.InsnList;

public class Nop implements SequenceInstruction {

	public static Nop NOP = new Nop();
	
	private Nop() {
	}

	@Override
	public InsnList build(MethodContext context) {
		return new InsnList();
	}

}
