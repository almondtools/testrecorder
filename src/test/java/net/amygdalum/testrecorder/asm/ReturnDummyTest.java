package net.amygdalum.testrecorder.asm;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ReturnDummyTest {

	@Test
	public void testReturnObjectDummy() throws Exception {
		ReturnDummy invokeNew = new ReturnDummy();

		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(Object.class)))), contains("ACONST_NULL", "ARETURN"));
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(String.class)))), contains("ACONST_NULL", "ARETURN"));
	}

	@Test
	public void testReturnPrimitiveDummy() throws Exception {
		ReturnDummy invokeNew = new ReturnDummy();

		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(byte.class)))), contains("ICONST_0", "IRETURN"));
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(short.class)))), contains("ICONST_0", "IRETURN"));
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(int.class)))), contains("ICONST_0", "IRETURN"));
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(long.class)))), contains("LCONST_0", "LRETURN"));
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(float.class)))), contains("FCONST_0", "FRETURN"));
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(double.class)))), contains("DCONST_0", "DRETURN"));
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(boolean.class)))), contains("ICONST_0", "IRETURN"));
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(char.class)))), contains("ICONST_0", "IRETURN"));
		assertThat(ByteCode.toString(invokeNew.build(methodContext(Type.getType(void.class)))), contains("RETURN"));
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
