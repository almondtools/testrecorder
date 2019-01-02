package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.InsnList;

public class LdcTest {

	private MethodContext context;

	@BeforeEach
	void before() {
		context = new MethodContext(AClass.classNode(), AClass.virtualMethodNode());
	}

	@Test
	void testLdcString() throws Exception {
		InsnList insns = new Ldc("astring").build(context);

		assertThat(ByteCode.toString(insns))
			.containsExactly("LDC \"astring\"");
	}

	@Test
	void testLdcNumber() throws Exception {
		InsnList insns = new Ldc(1f).build(context);

		assertThat(ByteCode.toString(insns))
			.containsExactly("LDC 1.0");
	}

	@Test
	void testLdcChar() throws Exception {
		InsnList insns = new Ldc('a').build(context);

		assertThat(ByteCode.toString(insns))
			.containsExactly("LDC a");
	}

	@Test
	void testLdcClass() throws Exception {
		InsnList insns = new Ldc(Ldc.class).build(context);

		assertThat(ByteCode.toString(insns))
			.containsExactly("LDC class net.amygdalum.testrecorder.asm.Ldc");
	}

}
