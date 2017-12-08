package net.amygdalum.testrecorder.asm;

import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.RETURN;

import java.util.ListIterator;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

public class WrapMethod implements SequenceInstruction {

	private LabelNode returnLabel;

	private SequenceInstruction prependInstructions;
	private SequenceInstruction appendInstructions;

	public WrapMethod() {
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
	public InsnList build(MethodContext context) {
		Type returnType = context.getResultType();

		InsnList insnList = new InsnList();

		insnList.add(prependInstructions.build(context));

		ListIterator<AbstractInsnNode> instructions = context.instructions().iterator();
		while (instructions.hasNext()) {
			AbstractInsnNode insn = instructions.next();
			if (returnLabel != null && isReturn(insn)) {
				insnList.add(new JumpInsnNode(GOTO, returnLabel));
			} else {
				insnList.add(insn);
			}
		}

		if (returnLabel != null) {
			insnList.add(returnLabel);

			insnList.add(appendInstructions.build(context));

			insnList.add(new InsnNode(returnType.getOpcode(IRETURN)));
		}

		return insnList;
	}

	private boolean isReturn(AbstractInsnNode insn) {
		return IRETURN <= insn.getOpcode() && insn.getOpcode() <= RETURN;
	}

}
