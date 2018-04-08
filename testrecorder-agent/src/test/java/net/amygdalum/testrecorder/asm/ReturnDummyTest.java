package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ReturnDummyTest {

	@Test
	public void testReturnObjectDummy() throws Exception {
		ReturnDummy invokeNew = new ReturnDummy();

		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(Object.class))))).containsExactly("ACONST_NULL", "ARETURN");
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(String.class))))).containsExactly("ACONST_NULL", "ARETURN");
	}

	@Test
	public void testReturnPrimitiveDummy() throws Exception {
		ReturnDummy invokeNew = new ReturnDummy();

		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(byte.class))))).containsExactly("ICONST_0", "IRETURN");
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(short.class))))).containsExactly("ICONST_0", "IRETURN");
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(int.class))))).containsExactly("ICONST_0", "IRETURN");
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(long.class))))).containsExactly("LCONST_0", "LRETURN");
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(float.class))))).containsExactly("FCONST_0", "FRETURN");
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(double.class))))).containsExactly("DCONST_0", "DRETURN");
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(boolean.class))))).containsExactly("ICONST_0", "IRETURN");
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(char.class))))).containsExactly("ICONST_0", "IRETURN");
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(void.class))))).containsExactly("RETURN");
	}

	private MethodContext methodContext(Type resultType) {
		return new MethodContext(new ClassNode(), new MethodNode()) {
			@Override
			public Type getResultType() {
				return resultType;
			}
		};
	}

}
