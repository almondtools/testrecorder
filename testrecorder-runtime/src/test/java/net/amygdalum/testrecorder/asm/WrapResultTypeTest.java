package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.InsnList;

public class WrapResultTypeTest {

	private MethodContext context;

	@BeforeEach
	void before() {
		context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(int.class));
	}

	@Test
	void testAssign() throws Exception {
		InsnList insns = new WrapResultType()
			.build(context);

		assertThat(ByteCode.toString(insns))
			.containsExactly(
				"GETSTATIC java/lang/Integer.TYPE : Ljava/lang/Class;");
	}
}
