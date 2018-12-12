package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.asm.ByteCode.isPrimitive;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;

public class ReturnDummy implements SequenceInstruction {

	public ReturnDummy() {
	}

	@Override
	public InsnList build(MethodContext context) {
		Type returnType = context.getResultType();
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

	private AbstractInsnNode primitiveNull(Type type) {
		char desc = type.getDescriptor().charAt(0);
		switch (desc) {
		case 'C':
		case 'Z':
		case 'B':
		case 'S':
		case 'I':
			return new InsnNode(Opcodes.ICONST_0);
		case 'J':
			return new InsnNode(Opcodes.LCONST_0);
		case 'F':
			return new InsnNode(Opcodes.FCONST_0);
		case 'D':
			return new InsnNode(Opcodes.DCONST_0);
		default:
			return null;
		}
	}

}
