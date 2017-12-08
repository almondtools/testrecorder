package net.amygdalum.testrecorder.asm;

import static com.almondtools.conmatch.exceptions.ExceptionMatcher.matchesException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.almondtools.conmatch.conventions.UtilityClassMatcher;

import net.amygdalum.testrecorder.runtime.Throwables;
import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.PublicEnum;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class ByteCodeTest {

	@Test
	public void testByteCode() throws Exception {
		assertThat(ByteCode.class, UtilityClassMatcher.isUtilityClass());
	}

	@Test
	public void testConstructorDescriptor() throws Exception {
		assertThat(ByteCode.constructorDescriptor(String.class), equalTo("()V"));
		assertThat(ByteCode.constructorDescriptor(String.class, char[].class), equalTo("([C)V"));
		assertThat(Throwables.capture(() -> ByteCode.constructorDescriptor(String.class, int.class)), matchesException(ByteCodeException.class).withCause(NoSuchMethodException.class));
	}

	@Test
	public void testMethodDescriptor() throws Exception {
		assertThat(ByteCode.methodDescriptor(String.class, "getBytes"), equalTo("()[B"));
		assertThat(ByteCode.methodDescriptor(String.class, "valueOf", char[].class), equalTo("([C)Ljava/lang/String;"));
		assertThat(Throwables.capture(() -> ByteCode.methodDescriptor(String.class, "valueOf", String.class)), matchesException(ByteCodeException.class).withCause(NoSuchMethodException.class));
	}

	@Test
	public void testFieldDescriptor() throws Exception {
		assertThat(ByteCode.fieldDescriptor(System.class, "out"), equalTo("Ljava/io/PrintStream;"));
		assertThat(Throwables.capture(() -> ByteCode.fieldDescriptor(System.class, "inout")), matchesException(ByteCodeException.class).withCause(NoSuchFieldException.class));
	}
	
	@Test
	public void testBoxedType() throws Exception {
		assertThat(ByteCode.boxedType(Type.BOOLEAN_TYPE), equalTo(Type.getType(Boolean.class)));
		assertThat(ByteCode.boxedType(Type.BYTE_TYPE), equalTo(Type.getType(Byte.class)));
		assertThat(ByteCode.boxedType(Type.SHORT_TYPE), equalTo(Type.getType(Short.class)));
		assertThat(ByteCode.boxedType(Type.INT_TYPE), equalTo(Type.getType(Integer.class)));
		assertThat(ByteCode.boxedType(Type.LONG_TYPE), equalTo(Type.getType(Long.class)));
		assertThat(ByteCode.boxedType(Type.FLOAT_TYPE), equalTo(Type.getType(Float.class)));
		assertThat(ByteCode.boxedType(Type.DOUBLE_TYPE), equalTo(Type.getType(Double.class)));
		assertThat(ByteCode.boxedType(Type.CHAR_TYPE), equalTo(Type.getType(Character.class)));
		assertThat(ByteCode.boxedType(Type.VOID_TYPE), equalTo(Type.getType(Void.class)));
		assertThat(ByteCode.boxedType(Type.getType(Object.class)), equalTo(Type.getType(Object.class)));
	}

	@Test
	public void testUnboxedType() throws Exception {
		assertThat(ByteCode.unboxedType(Type.BOOLEAN_TYPE), sameInstance(Type.BOOLEAN_TYPE));
		assertThat(ByteCode.unboxedType(Type.BYTE_TYPE), sameInstance(Type.BYTE_TYPE));
		assertThat(ByteCode.unboxedType(Type.SHORT_TYPE), sameInstance(Type.SHORT_TYPE));
		assertThat(ByteCode.unboxedType(Type.INT_TYPE), sameInstance(Type.INT_TYPE));
		assertThat(ByteCode.unboxedType(Type.LONG_TYPE), sameInstance(Type.LONG_TYPE));
		assertThat(ByteCode.unboxedType(Type.FLOAT_TYPE), sameInstance(Type.FLOAT_TYPE));
		assertThat(ByteCode.unboxedType(Type.DOUBLE_TYPE), sameInstance(Type.DOUBLE_TYPE));
		assertThat(ByteCode.unboxedType(Type.CHAR_TYPE), sameInstance(Type.CHAR_TYPE));
		assertThat(ByteCode.unboxedType(Type.VOID_TYPE), sameInstance(Type.VOID_TYPE));
		assertThat(ByteCode.unboxedType(Type.getType(Boolean.class)), sameInstance(Type.BOOLEAN_TYPE));
		assertThat(ByteCode.unboxedType(Type.getType(Byte.class)), sameInstance(Type.BYTE_TYPE));
		assertThat(ByteCode.unboxedType(Type.getType(Short.class)), sameInstance(Type.SHORT_TYPE));
		assertThat(ByteCode.unboxedType(Type.getType(Integer.class)), sameInstance(Type.INT_TYPE));
		assertThat(ByteCode.unboxedType(Type.getType(Long.class)), sameInstance(Type.LONG_TYPE));
		assertThat(ByteCode.unboxedType(Type.getType(Float.class)), sameInstance(Type.FLOAT_TYPE));
		assertThat(ByteCode.unboxedType(Type.getType(Double.class)), sameInstance(Type.DOUBLE_TYPE));
		assertThat(ByteCode.unboxedType(Type.getType(Character.class)), sameInstance(Type.CHAR_TYPE));
		assertThat(ByteCode.unboxedType(Type.getType(Void.class)), sameInstance(Type.VOID_TYPE));
		assertThat(ByteCode.unboxedType(Type.getType(Object.class)), nullValue());
	}

	@Test
	public void testUnboxingFactory() throws Exception {
		assertThat(ByteCode.unboxingFactory(Type.BOOLEAN_TYPE), equalTo("booleanValue"));
		assertThat(ByteCode.unboxingFactory(Type.BYTE_TYPE), equalTo("byteValue"));
		assertThat(ByteCode.unboxingFactory(Type.SHORT_TYPE), equalTo("shortValue"));
		assertThat(ByteCode.unboxingFactory(Type.INT_TYPE), equalTo("intValue"));
		assertThat(ByteCode.unboxingFactory(Type.LONG_TYPE), equalTo("longValue"));
		assertThat(ByteCode.unboxingFactory(Type.FLOAT_TYPE), equalTo("floatValue"));
		assertThat(ByteCode.unboxingFactory(Type.DOUBLE_TYPE), equalTo("doubleValue"));
		assertThat(ByteCode.unboxingFactory(Type.CHAR_TYPE), equalTo("charValue"));
		assertThat(ByteCode.unboxingFactory(Type.VOID_TYPE), nullValue());
	}

	@Test
	public void testBoxingFactory() throws Exception {
		assertThat(ByteCode.boxingFactory(Type.BOOLEAN_TYPE), equalTo("valueOf"));
		assertThat(ByteCode.boxingFactory(Type.BYTE_TYPE), equalTo("valueOf"));
		assertThat(ByteCode.boxingFactory(Type.SHORT_TYPE), equalTo("valueOf"));
		assertThat(ByteCode.boxingFactory(Type.INT_TYPE), equalTo("valueOf"));
		assertThat(ByteCode.boxingFactory(Type.LONG_TYPE), equalTo("valueOf"));
		assertThat(ByteCode.boxingFactory(Type.FLOAT_TYPE), equalTo("valueOf"));
		assertThat(ByteCode.boxingFactory(Type.DOUBLE_TYPE), equalTo("valueOf"));
		assertThat(ByteCode.boxingFactory(Type.CHAR_TYPE), equalTo("valueOf"));
		assertThat(ByteCode.boxingFactory(Type.VOID_TYPE), nullValue());
	}

	@Test
	public void testIsStatic() throws Exception {
		assertThat(ByteCode.isStatic(methodWithModifiers(Opcodes.ACC_STATIC)), is(true));
		assertThat(ByteCode.isStatic(methodWithModifiers(~Opcodes.ACC_STATIC)), is(false));
	}

	@Test
	public void testIsNative() throws Exception {
		assertThat(ByteCode.isNative(methodWithModifiers(Opcodes.ACC_NATIVE)), is(true));
		assertThat(ByteCode.isNative(methodWithModifiers(~Opcodes.ACC_NATIVE)), is(false));
	}
	
	@Test
	public void testReturnsResult() throws Exception {
		assertThat(ByteCode.returnsResult(methodWithDesc("()I")), is(true));
		assertThat(ByteCode.returnsResult(methodWithDesc("()V")), is(false));
		assertThat(ByteCode.returnsResult(methodInsnWithDesc("()I")), is(true));
		assertThat(ByteCode.returnsResult(methodInsnWithDesc("()V")), is(false));
	}
	
	private MethodNode methodWithModifiers(int modifiers) {
		return new MethodNode(modifiers, null, null, null, null);
	}

	private MethodNode methodWithDesc(String desc) {
		return new MethodNode(0, null, desc, null, null);
	}
	
	private MethodInsnNode methodInsnWithDesc(String desc) {
		return new MethodInsnNode(0, null, null, desc, false);
	}

	@Test
	public void testIsPrimitive() throws Exception {
		assertThat(ByteCode.isPrimitive(Type.INT_TYPE), is(true));
		assertThat(ByteCode.isPrimitive(Type.getType(int[].class)), is(false));
		assertThat(ByteCode.isPrimitive(Type.getType(Integer.class)), is(false));
		assertThat(ByteCode.isPrimitive(Type.getType(Object.class)), is(false));
	}
	
	@Test
	public void testIsArray() throws Exception {
		assertThat(ByteCode.isArray(Type.getType(int[].class)), is(true));
		assertThat(ByteCode.isArray(Type.getType(int[][].class)), is(true));
		assertThat(ByteCode.isArray(Type.getType(Object[].class)), is(true));
		assertThat(ByteCode.isArray(Type.INT_TYPE), is(false));
		assertThat(ByteCode.isArray(Type.getType(Integer.class)), is(false));
		assertThat(ByteCode.isArray(Type.getType(Object.class)), is(false));
	}

	@Test
	public void testClassFrom() throws Exception {
		assertThat(ByteCode.classFrom("net/amygdalum/testrecorder/util/testobjects/PublicEnum"), equalTo(PublicEnum.class));
		assertThat(ByteCode.classFrom("net/amygdalum/testrecorder/util/testobjects/Simple", ByteCodeTest.class.getClassLoader()), equalTo(Simple.class));
		assertThat(ByteCode.classFrom(Type.getType(Complex[].class)), equalTo(Complex[].class));
		assertThat(ByteCode.classFrom(Type.getType(int.class)), equalTo(int.class));
		assertThat(Throwables.capture(() -> ByteCode.classFrom("net/amygdalum/testrecorder/util/testobjects/NotExisting")), matchesException(ByteCodeException.class).withCause(ClassNotFoundException.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testArgumentTypes() throws Exception {
		assertThat(ByteCode.argumentTypesFrom("()V"), emptyArray());
		assertThat(ByteCode.argumentTypesFrom("(Ljava/lang/Object;)I"), arrayContaining(Object.class));
		assertThat(ByteCode.argumentTypesFrom("(IC)V"), arrayContaining(int.class, char.class));
	}
	
	@Test
	public void testResultType() throws Exception {
		assertThat(ByteCode.resultTypeFrom("()V"), equalTo(void.class));
		assertThat(ByteCode.resultTypeFrom("(Ljava/lang/Object;)I"), equalTo(int.class));
		assertThat(ByteCode.resultTypeFrom("(IC)Ljava/lang/String;"), equalTo(String.class));
	}
	
}
