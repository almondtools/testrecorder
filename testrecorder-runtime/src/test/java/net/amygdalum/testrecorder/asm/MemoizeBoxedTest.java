package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public class MemoizeBoxedTest {

	private MethodContext context;

	@Nested
	class testMemoizeBoxed {

		@Test
		void onShortType() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.staticMethodNode());
			InsnList insns = new MemoizeBoxed("x", Type.getType(int.class))
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"DUP",
					"INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;",
					"ASTORE 0");
			assertThat(context.local("x").index)
				.isEqualTo(0);
		}

		@Test
		void onLongType() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.staticMethodNode());
			InsnList insns = new MemoizeBoxed("x", Type.getType(double.class))
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"DUP2",
					"INVOKESTATIC java/lang/Double.valueOf (D)Ljava/lang/Double;",
					"ASTORE 0");
			assertThat(context.local("x").index)
				.isEqualTo(0);
		}
	}

}
