package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;

public class SequenceTest {

	private MethodContext context;

	@BeforeEach
	void before() {
		context = new MethodContext(AClass.classNode(), AClass.virtualMethodNode());
	}

	@Test
	void testThenAbstractInsnNode() throws Exception {
		InsnList insns = Sequence.start().then(new InsnNode(Opcodes.ICONST_0)).build(context);

		assertThat(ByteCode.toString(insns))
			.containsExactly("ICONST_0");
	}

	@Test
	void testThenSequenceInstruction() throws Exception {
		InsnList insns = Sequence.start().then(new This()).build(context);

		assertThat(ByteCode.toString(insns))
			.containsExactly("ALOAD 0");
	}

}
