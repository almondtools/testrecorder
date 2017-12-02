package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.asm.ByteCode.pushType;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public class WrapResultType implements SequenceInstruction {

	public WrapResultType() {
	}

	@Override
	public InsnList build(Sequence sequence) {
		Type resultType = sequence.getResultType();

		InsnList insnList = new InsnList();

		insnList.add(pushType(resultType));
		
		return insnList;
	}

}
