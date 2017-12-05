package net.amygdalum.testrecorder.asm;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public class Sequence implements SequenceInstruction {

	private List<SequenceInstruction> insns;

	public Sequence() {
		this.insns = new ArrayList<>();
	}
	
	public static Sequence start() {
		return new Sequence();
	}

	public Sequence then(AbstractInsnNode insn) {
		insns.add(context -> list(insn));
		return this;
	}

	public Sequence then(SequenceInstruction insn) {
		insns.add(insn);
		return this;
	}

	@Override
	public InsnList build(MethodContext context) {
		InsnList insnList = new InsnList();
		for (SequenceInstruction insn : insns) {
			insnList.add(insn.build(context));
		}
		return insnList;
	}

}
