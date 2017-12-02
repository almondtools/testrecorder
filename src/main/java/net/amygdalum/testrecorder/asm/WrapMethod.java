package net.amygdalum.testrecorder.asm;

import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

public class WrapMethod implements SequenceInstruction {

	private MethodNode methodNode;
	private LabelNode returnLabel;

	private SequenceInstruction prependInstructions;
	private SequenceInstruction appendInstructions;

	public WrapMethod(MethodNode methodNode) {
		this.methodNode = methodNode;
		this.prependInstructions = Nop.NOP;
		this.appendInstructions = Nop.NOP;
	}

	public WrapMethod prepend(SequenceInstruction prependInstructions) {
		this.prependInstructions = prependInstructions;
		return this;
	}

	public WrapMethod append(SequenceInstruction appendInstructions) {
		this.appendInstructions = appendInstructions;
		if (appendInstructions != Nop.NOP) {
			this.returnLabel = new LabelNode();
		}
		return this;
	}

	@Override
	public InsnList build(Sequence sequence) {
		Type returnType = Type.getReturnType(methodNode.desc);

		InsnList insnList = new InsnList();

		insnList.add(prependInstructions.build(sequence));

		for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
			if (returnLabel != null && isReturn(insn)) {
				insnList.add(new JumpInsnNode(GOTO, returnLabel));
			} else {
				insnList.add(insn);
			}
		}

		if (returnLabel != null) {
			insnList.add(returnLabel);

			insnList.add(appendInstructions.build(sequence));

			insnList.add(new InsnNode(returnType.getOpcode(IRETURN)));
		}

		return insnList;
	}

	private boolean isReturn(AbstractInsnNode insn) {
		return IRETURN <= insn.getOpcode() && insn.getOpcode() <= RETURN;
	}

}
