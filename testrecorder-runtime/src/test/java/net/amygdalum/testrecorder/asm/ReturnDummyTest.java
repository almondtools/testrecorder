package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.InsnList;

public class ReturnDummyTest {

	private MethodContext context;

	@Nested
	class Object {
		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(Object.class));
		}

		@Test
		public void testReturnObjectDummy() throws Exception {
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly("ACONST_NULL", "ARETURN");
		}
	}

	@Nested
	class String {
		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(String.class));
		}

		@Test
		public void testReturnObjectDummy() throws Exception {
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("ACONST_NULL", "ARETURN");
		}
	}

	@Nested
	class Byte {
		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(byte.class));
		}

		@Test
		public void testReturnPrimitiveDummy() throws Exception {
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("ICONST_0", "IRETURN");
		}
	}

	@Nested
	class Short {
		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(short.class));
		}

		@Test
		public void testReturnPrimitiveDummy() throws Exception {
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("ICONST_0", "IRETURN");
		}
	}

	@Nested
	class Int {
		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(int.class));
		}

		@Test
		public void testReturnPrimitiveDummy() throws Exception {
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("ICONST_0", "IRETURN");
		}
	}

	@Nested
	class Long {
		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(long.class));
		}

		@Test
		public void testReturnPrimitiveDummy() throws Exception {
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("LCONST_0", "LRETURN");
		}
	}

	@Nested
	class Float {
		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(float.class));
		}

		@Test
		public void testReturnPrimitiveDummy() throws Exception {
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("FCONST_0", "FRETURN");
		}
	}

	@Nested
	class Double {
		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(double.class));
		}

		@Test
		public void testReturnPrimitiveDummy() throws Exception {
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("DCONST_0", "DRETURN");
		}
	}

	@Nested
	class Boolean {
		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(boolean.class));
		}

		@Test
		public void testReturnPrimitiveDummy() throws Exception {
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("ICONST_0", "IRETURN");
		}
	}

	@Nested
	class Char {
		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNodeReturning(char.class));
		}

		@Test
		public void testReturnPrimitiveDummy() throws Exception {
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("ICONST_0", "IRETURN");
		}
	}

	@Nested
	class Void {
		@BeforeEach
		void before() {
			context = new MethodContext(AClass.classNode(), AClass.virtualMethodNode());
		}

		@Test
		public void testReturnPrimitiveDummy() throws Exception {
			ReturnDummy invokeNew = new ReturnDummy();

			InsnList insns = invokeNew.build(context);

			assertThat(ByteCode.toString(insns)).containsExactly("RETURN");
		}
	}
}
