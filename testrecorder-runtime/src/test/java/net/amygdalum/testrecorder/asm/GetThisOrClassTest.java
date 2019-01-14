package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.InsnList;

public class GetThisOrClassTest {

	private MethodContext context;

	@Nested
	class testGetThis {

		@Test
		void inStaticMethod() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.staticMethodNode());
			InsnList insns = new GetThisOrClass()
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"LDC L" + AClass.class.getName().replace('.', '/') + ";.class");
		}

		@Test
		void inVirtualMethod() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNode());
			InsnList insns = new GetThisOrClass()
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"ALOAD 0");
		}

	}
}
