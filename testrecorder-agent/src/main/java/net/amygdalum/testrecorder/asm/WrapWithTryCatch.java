package net.amygdalum.testrecorder.asm;

import static org.objectweb.asm.Opcodes.ATHROW;
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
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

public class WrapWithTryCatch implements SequenceInstruction {

	private LabelNode tryLabel;
	private LabelNode catchLabel;
	private LabelNode returnLabel;

	private SequenceInstruction tryInstructions;
	private SequenceInstruction catchInstructions;
	private SequenceInstruction returnInstructions;

	public WrapWithTryCatch(MethodNode methodNode) {
		this.tryLabel = new LabelNode();
		this.catchLabel = new LabelNode();
		this.returnLabel = new LabelNode();
	}

	public WrapWithTryCatch before(SequenceInstruction tryInstructions) {
		this.tryInstructions = tryInstructions;
		return this;
	}

	public WrapWithTryCatch after(SequenceInstruction returnInstructions) {
		this.returnInstructions = returnInstructions;
		return this;
	}
	
	public WrapWithTryCatch handler(SequenceInstruction catchInstructions) {
		this.catchInstructions = catchInstructions;
		return this;
	}

	@Override
	public InsnList build(MethodContext context) {
		Type returnType = context.getResultType();

		context.tryCatchBlocks().add(new TryCatchBlockNode(tryLabel, catchLabel, catchLabel, null));

		InsnList insnList = new InsnList();
		insnList.add(tryLabel);
		insnList.add(tryInstructions.build(context));
		
		ListIterator<AbstractInsnNode> instructions = context.instructions().iterator();
		while (instructions.hasNext()) {
			AbstractInsnNode insn = instructions.next();
			if (isReturn(insn)) {
				insnList.add(new JumpInsnNode(GOTO, returnLabel));
			} else {
				insnList.add(insn);
			}
		}
		insnList.add(returnLabel);
		insnList.add(returnInstructions.build(context));
		insnList.add(new InsnNode(returnType.getOpcode(IRETURN)));
		insnList.add(catchLabel);
		insnList.add(catchInstructions.build(context));
		insnList.add(new InsnNode(ATHROW));
		
		return insnList;
	}

	private boolean isReturn(AbstractInsnNode insn) {
		return IRETURN <= insn.getOpcode() && insn.getOpcode() <= RETURN;
	}

}
