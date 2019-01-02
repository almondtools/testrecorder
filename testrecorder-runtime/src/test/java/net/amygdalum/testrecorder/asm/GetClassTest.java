package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.InsnList;

public class GetClassTest {

	private MethodContext context;

	@Nested
	class InVirtualMethod {

		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNode());
		}

		@Test
		void testGetClass() throws Exception {
			InsnList insns = new GetClass()
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"ALOAD 0",
					"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;");
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
			InsnList insns = new GetClass()
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"LDC L" + AClass.class.getName().replace('.', '/') + ";.class");
		}
	}
}
