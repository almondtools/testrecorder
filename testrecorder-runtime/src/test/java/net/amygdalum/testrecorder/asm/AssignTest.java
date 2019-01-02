package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public class AssignTest {

	private MethodContext context;

	@BeforeEach
	void before() {
		context = new MethodContext(AClass.classNode(), AClass.staticMethodNode());
	}

	@Test
	void testAssign() throws Exception {
		InsnList insns = new Assign("x", Type.getType(String.class))
			.value(new Ldc("str"))
			.build(context);

		assertThat(ByteCode.toString(insns))
			.containsExactly(
				"LDC \"str\"",
				"ASTORE 0");
		assertThat(context.local("x").index)
			.isEqualTo(0);
	}

}
