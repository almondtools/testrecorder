package net.amygdalum.testrecorder.asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public class Sequence {

	public static final Sequence NULL = new Sequence(null);
	
	private List<SequenceInstruction> insns;
	private Locals locals;
	private Map<String, Integer> variables;

	public Sequence(Locals locals) {
		this.insns = new ArrayList<>();
		this.locals = locals;
		this.variables = new HashMap<>();
	}

	public static Sequence sequence(Locals locals) {
		return new Sequence(locals);
	}

	public int newLocal(String variableName, Type type) {
		int newLocal = locals.newLocal(type);
		variables.put(variableName, newLocal);
		return newLocal;
	}

	public int local(String variableName) {
		return variables.get(variableName);
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

}
