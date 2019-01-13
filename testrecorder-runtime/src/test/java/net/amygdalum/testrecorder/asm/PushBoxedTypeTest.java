package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public class PushBoxedTypeTest {

	private MethodContext context;

	@BeforeEach
	void before() {
		context = new MethodContext(AClass.classNode(), AClass.virtualMethodNode());
	}

	@Nested
	class testPushBoxed {
		@Test
		void onTypeObject() throws Exception {
			InsnList insns = new PushBoxedType(Type.getType(String.class)).build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly("LDC Ljava/lang/String;.class");
		}

		@Test
		void onTypePrimitive() throws Exception {
			InsnList insns = new PushBoxedType(Type.getType(int.class)).build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly("GETSTATIC java/lang/Integer.TYPE : Ljava/lang/Class;");
		}
	}
}
