package net.amygdalum.testrecorder.asm;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public class GetClassName implements SequenceInstruction {

	public GetClassName() {
	}

	@Override
	public InsnList build(MethodContext context) {
		return context.isStatic()
			? new Ldc(Type.getObjectType(context.getClassName()).getClassName())
				.build(context)
			: new InvokeVirtual(Class.class, "getName")
				.withBase(new InvokeVirtual(Object.class, "getClass")
					.withBase(new This()))
				.build(context);
	}

}
