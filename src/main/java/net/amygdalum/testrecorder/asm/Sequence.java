package net.amygdalum.testrecorder.asm;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public class Sequence implements SequenceInstruction {

	private List<SequenceInstruction> insns;
	private Locals locals;

	public Sequence(Locals locals) {
		this.insns = new ArrayList<>();
		this.locals = locals;
	}

	public static Sequence sequence(Locals locals) {
		return new Sequence(locals);
	}

	public Local newLocal(String variableName, Type type) {
		return locals.newLocal(variableName, type);
	}

	public Local local(String variableName) {
		return locals.local(variableName);
	}

	public Type getResultType() {
		return locals.getResultType();
	}

	public Type[] getArgumentTypes() {
		return locals.getArgumentTypes();
	}

	public int[] getArguments() {
		return locals.getArguments();
	}

	public Sequence then(SequenceInstruction insn) {
		insns.add(insn);
		return this;
	}

	public InsnList build() {
		InsnList insnList = new InsnList();
		for (SequenceInstruction insn : insns) {
			insnList.add(insn.build(this));
		}
		return insnList;
	}
	
	@Override
	public InsnList build(Sequence sequence) {
		this.locals = sequence.locals;
		return build();
	}

}
