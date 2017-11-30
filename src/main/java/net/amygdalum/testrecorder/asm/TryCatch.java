package net.amygdalum.testrecorder.asm;

import static org.objectweb.asm.Opcodes.ATHROW;
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
import org.objectweb.asm.tree.TryCatchBlockNode;

public class TryCatch implements SequenceInstruction {

	private MethodNode methodNode;
	private LabelNode tryLabel;
	private LabelNode catchLabel;
	private LabelNode finallyLabel;

	private SequenceInstruction tryInstructions;
	private SequenceInstruction catchInstructions;
	private SequenceInstruction returnInstructions;

	public TryCatch(MethodNode methodNode) {
		this.methodNode = methodNode;
		this.tryLabel = new LabelNode();
		this.catchLabel = new LabelNode();
		this.finallyLabel = new LabelNode();
	}

	public TryCatch withTry(SequenceInstruction tryInstructions) {
		this.tryInstructions = tryInstructions;
		return this;
	}

	public TryCatch withCatch(SequenceInstruction catchInstructions) {
		this.catchInstructions = catchInstructions;
		return this;
	}

	public TryCatch withReturn(SequenceInstruction returnInstructions) {
		this.returnInstructions = returnInstructions;
		return this;
	}
	
	@Override
	public InsnList build(Sequence sequence) {
		Type returnType = Type.getReturnType(methodNode.desc);

		methodNode.tryCatchBlocks.add(new TryCatchBlockNode(tryLabel, catchLabel, catchLabel, null));

		InsnList insnList = new InsnList();
		insnList.add(tryLabel);
		insnList.add(tryInstructions.build(sequence));
		
		for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
			if (isReturn(insn)) {
				insnList.add(new JumpInsnNode(GOTO, finallyLabel));
			} else {
				insnList.add(insn);
			}
		}
		insnList.add(new JumpInsnNode(GOTO, finallyLabel));
		insnList.add(catchLabel);
		insnList.add(catchInstructions.build(sequence));
		insnList.add(new InsnNode(ATHROW));
		insnList.add(finallyLabel);
		insnList.add(returnInstructions.build(sequence));
		insnList.add(new InsnNode(returnType.getOpcode(IRETURN)));
		
		return insnList;
	}

	private boolean isReturn(AbstractInsnNode insn) {
		return IRETURN <= insn.getOpcode() && insn.getOpcode() <= RETURN;
	}

}
