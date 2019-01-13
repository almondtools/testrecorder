package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.InsnList;

public class ReturnDummyTest {

	private MethodContext context;

	@Nested
	class testReturnObjectDummy {

		@Test
		void onObject() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(Object.class));
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly("ACONST_NULL", "ARETURN");
		}

		@Test
		void onString() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(String.class));
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("ACONST_NULL", "ARETURN");
		}

		@Test
		void onByte() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(byte.class));
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("ICONST_0", "IRETURN");
		}

		@Test
		void onShort() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(short.class));
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("ICONST_0", "IRETURN");
		}

		@Test
		void onInt() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(int.class));
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("ICONST_0", "IRETURN");
		}

		@Test
		void onLong() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(long.class));
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("LCONST_0", "LRETURN");
		}

		@Test
		void onFloat() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(float.class));
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("FCONST_0", "FRETURN");
		}

		@Test
		void onDouble() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(double.class));
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("DCONST_0", "DRETURN");
		}

		@Test
		void onBoolean() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(boolean.class));
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("ICONST_0", "IRETURN");
		}

		@Test
		void onChar() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(char.class));
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("ICONST_0", "IRETURN");
		}

		@Test
		void onVoid() throws Exception {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNode());
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("RETURN");
		}
	}
}
