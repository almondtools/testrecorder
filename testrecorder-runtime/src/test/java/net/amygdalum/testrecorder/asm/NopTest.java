package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.InsnList;

public class NopTest {

	private MethodContext context;

	@BeforeEach
	void before() {
		context = new MethodContext(AClass.classNode(), AClass.virtualMethodNode());
	}

	@Test
	public void testNop() throws Exception {
		Nop nop = Nop.NOP;

		InsnList insns = nop.build(context);

		assertThat(ByteCode.toString(insns)).isEmpty();
	}

}
