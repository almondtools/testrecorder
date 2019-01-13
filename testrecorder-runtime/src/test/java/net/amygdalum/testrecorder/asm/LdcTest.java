package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.InsnList;

public class LdcTest {

	private MethodContext context;

	@BeforeEach
	void before() {
		context = new MethodContext(AClass.classNode(), AClass.virtualMethodNode());
	}

	@Nested
	class on {
		@Test
		void onString() throws Exception {
			InsnList insns = new Ldc("astring").build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly("LDC \"astring\"");
		}

		@Test
		void onNumber() throws Exception {
			InsnList insns = new Ldc(1f).build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly("LDC 1.0");
		}

		@Test
		void onChar() throws Exception {
			InsnList insns = new Ldc('a').build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly("LDC a");
		}

		@Test
		void onClass() throws Exception {
			InsnList insns = new Ldc(Ldc.class).build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly("LDC class net.amygdalum.testrecorder.asm.Ldc");
		}
	}
}
