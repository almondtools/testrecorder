package net.amygdalum.testrecorder.asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public class Sequence implements SequenceInstruction {

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
