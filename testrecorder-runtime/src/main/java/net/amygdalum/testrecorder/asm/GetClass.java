package net.amygdalum.testrecorder.asm;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;

public class GetClass implements SequenceInstruction {

	public GetClass() {
	}

	@Override
	public InsnList build(MethodContext context) {
		return context.isStatic()
			? list(new LdcInsnNode(context.getClassType()))
			: new InvokeVirtual(Object.class, "getClass")
				.withBase(new This())
				.build(context);
	}

}
