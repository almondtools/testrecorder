package net.amygdalum.testrecorder.asm;

import org.objectweb.asm.tree.InsnList;

public class GetStackTrace implements SequenceInstruction {

	public GetStackTrace() {
	}

	@Override
	public InsnList build(Sequence sequence) {
		return new InvokeVirtual(Throwable.class, "getStackTrace")
			.withBase(new InvokeNew(Throwable.class))
			.build(sequence);
	}

}
