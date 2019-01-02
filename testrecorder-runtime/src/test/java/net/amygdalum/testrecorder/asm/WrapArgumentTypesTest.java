package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.InsnList;

public class WrapArgumentTypesTest {

	private MethodContext context;

	@Nested
	class NoArguments {
		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNode());
		}

		@Test
		void testWrapArguments() throws Exception {
			InsnList insns = new WrapArgumentTypes()
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"LDC 0",
					"ANEWARRAY java/lang/reflect/Type");
		}
	}

	@Nested
	class SingleArgument {
		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodePassing(int.class));
		}

		@Test
		void testWrapArguments() throws Exception {
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
	}
	@Nested
	class MultipleArguments {
		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodePassing(double.class, Object.class));
		}
		
		@Test
		void testWrapArguments() throws Exception {
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
