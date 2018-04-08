package net.amygdalum.testrecorder.asm;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public class WrapResultType implements SequenceInstruction {

	public WrapResultType() {
	}

	@Override
	public InsnList build(MethodContext context) {
		Type resultType = context.getResultType();

		InsnList insnList = new InsnList();

		insnList.add(new PushBoxedType(resultType).build(context));
		
		return insnList;
	}

}
