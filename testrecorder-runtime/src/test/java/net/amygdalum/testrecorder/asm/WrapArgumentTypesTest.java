package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.InsnList;

public class WrapArgumentTypesTest {

	private MethodContext context;

	@Nested
	class testWrapArguments {
		@Test
		void onNoArguments() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNode());
			InsnList insns = new WrapArgumentTypes()
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"LDC 0",
					"ANEWARRAY java/lang/reflect/Type");
		}

		@Test
		void onSingleArgument() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodePassing(int.class));
			InsnList insns = new WrapArgumentTypes()
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"LDC 1",
					"ANEWARRAY java/lang/reflect/Type",
					"DUP",
					"LDC 0",
					"GETSTATIC java/lang/Integer.TYPE : Ljava/lang/Class;",
					"AASTORE");
		}

		@Test
		void onMultipleArguments() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodePassing(double.class, Object.class));
			InsnList insns = new WrapArgumentTypes()
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"LDC 2",
					"ANEWARRAY java/lang/reflect/Type",
					"DUP",
					"LDC 0",
					"GETSTATIC java/lang/Double.TYPE : Ljava/lang/Class;",
					"AASTORE",
					"DUP",
					"LDC 1",
					"LDC Ljava/lang/Object;.class",
					"AASTORE");
		}
	}
}
