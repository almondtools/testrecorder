package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.InsnList;

public class GetThisOrClassTest {

	private MethodContext context;

	@Nested
	class InVirtualMethod {

		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNode());
		}

		@Test
		void testGetThis() throws Exception {
			InsnList insns = new GetThisOrClass()
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"ALOAD 0");
		}
	}

	@Nested
	class InStaticMethod {

		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.staticMethodNode());
		}

		@Test
		void testGetClass() throws Exception {
			InsnList insns = new GetThisOrClass()
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"LDC L" + AClass.class.getName().replace('.', '/') + ";.class");
		}
	}
}
