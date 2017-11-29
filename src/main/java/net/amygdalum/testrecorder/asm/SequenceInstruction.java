package net.amygdalum.testrecorder.asm;

import org.objectweb.asm.tree.InsnList;

public interface SequenceInstruction {

	InsnList build(Sequence sequence);

}
