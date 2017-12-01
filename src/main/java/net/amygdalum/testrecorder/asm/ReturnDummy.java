package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.asm.ByteCode.isPrimitive;
import static net.amygdalum.testrecorder.asm.ByteCode.primitiveNull;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ReturnDummy implements SequenceInstruction {

	private MethodNode methodNode;

	public ReturnDummy(MethodNode methodNode) {
		this.methodNode = methodNode;
	}

	@Override
	public InsnList build(Sequence sequence) {
		Type returnType = Type.getReturnType(methodNode.desc);
		InsnList insnList = new InsnList();
		if (returnType.getSize() == 0) {
			insnList.add(new InsnNode(RETURN));
		} else if (isPrimitive(returnType)) {
			insnList.add(primitiveNull(returnType));
			insnList.add(new InsnNode(returnType.getOpcode(IRETURN)));
		} else {
			insnList.add(new InsnNode(ACONST_NULL));
			insnList.add(new InsnNode(ARETURN));
		}
		return insnList;
	}

}
