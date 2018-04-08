package net.amygdalum.testrecorder.asm;

import static net.amygdalum.extensions.assertj.conventions.UtilityClass.utilityClass;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.POP;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.amygdalum.testrecorder.util.LogLevel;
import net.amygdalum.testrecorder.util.LoggerExtension;
import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.PublicEnum;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class ByteCodeTest {

	@Test
	public void testByteCode() throws Exception {
		assertThat(ByteCode.class).satisfies(utilityClass().conventions());
	}

	@Test
	public void testConstructorDescriptor() throws Exception {
		assertThat(ByteCode.constructorDescriptor(String.class)).isEqualTo("()V");
		assertThat(ByteCode.constructorDescriptor(String.class, char[].class)).isEqualTo("([C)V");
		assertThatThrownBy(() -> ByteCode.constructorDescriptor(String.class, int.class))
			.isInstanceOf(ByteCodeException.class)
			.hasCauseExactlyInstanceOf(NoSuchMethodException.class);
	}

	@Test
	public void testMethodDescriptor() throws Exception {
		assertThat(ByteCode.methodDescriptor(String.class, "getBytes")).isEqualTo("()[B");
		assertThat(ByteCode.methodDescriptor(String.class, "valueOf", char[].class)).isEqualTo("([C)Ljava/lang/String;");
		assertThatThrownBy(() -> ByteCode.methodDescriptor(String.class, "valueOf", String.class))
			.isInstanceOf(ByteCodeException.class)
			.hasCauseExactlyInstanceOf(NoSuchMethodException.class);
	}

	@Test
	public void testFieldDescriptor() throws Exception {
		assertThat(ByteCode.fieldDescriptor(System.class, "out")).isEqualTo("Ljava/io/PrintStream;");
		assertThatThrownBy(() -> ByteCode.fieldDescriptor(System.class, "inout"))
			.isInstanceOf(ByteCodeException.class)
			.hasCauseExactlyInstanceOf(NoSuchFieldException.class);
	}

	@Test
	public void testBoxedType() throws Exception {
		assertThat(ByteCode.boxedType(Type.BOOLEAN_TYPE)).isEqualTo(Type.getType(Boolean.class));
		assertThat(ByteCode.boxedType(Type.BYTE_TYPE)).isEqualTo(Type.getType(Byte.class));
		assertThat(ByteCode.boxedType(Type.SHORT_TYPE)).isEqualTo(Type.getType(Short.class));
		assertThat(ByteCode.boxedType(Type.INT_TYPE)).isEqualTo(Type.getType(Integer.class));
		assertThat(ByteCode.boxedType(Type.LONG_TYPE)).isEqualTo(Type.getType(Long.class));
		assertThat(ByteCode.boxedType(Type.FLOAT_TYPE)).isEqualTo(Type.getType(Float.class));
		assertThat(ByteCode.boxedType(Type.DOUBLE_TYPE)).isEqualTo(Type.getType(Double.class));
		assertThat(ByteCode.boxedType(Type.CHAR_TYPE)).isEqualTo(Type.getType(Character.class));
		assertThat(ByteCode.boxedType(Type.VOID_TYPE)).isEqualTo(Type.getType(Void.class));
		assertThat(ByteCode.boxedType(Type.getType(Object.class))).isEqualTo(Type.getType(Object.class));
	}

	@Test
	public void testUnboxedType() throws Exception {
		assertThat(ByteCode.unboxedType(Type.BOOLEAN_TYPE)).isSameAs(Type.BOOLEAN_TYPE);
		assertThat(ByteCode.unboxedType(Type.BYTE_TYPE)).isSameAs(Type.BYTE_TYPE);
		assertThat(ByteCode.unboxedType(Type.SHORT_TYPE)).isSameAs(Type.SHORT_TYPE);
		assertThat(ByteCode.unboxedType(Type.INT_TYPE)).isSameAs(Type.INT_TYPE);
		assertThat(ByteCode.unboxedType(Type.LONG_TYPE)).isSameAs(Type.LONG_TYPE);
		assertThat(ByteCode.unboxedType(Type.FLOAT_TYPE)).isSameAs(Type.FLOAT_TYPE);
		assertThat(ByteCode.unboxedType(Type.DOUBLE_TYPE)).isSameAs(Type.DOUBLE_TYPE);
		assertThat(ByteCode.unboxedType(Type.CHAR_TYPE)).isSameAs(Type.CHAR_TYPE);
		assertThat(ByteCode.unboxedType(Type.VOID_TYPE)).isSameAs(Type.VOID_TYPE);
		assertThat(ByteCode.unboxedType(Type.getType(Boolean.class))).isSameAs(Type.BOOLEAN_TYPE);
		assertThat(ByteCode.unboxedType(Type.getType(Byte.class))).isSameAs(Type.BYTE_TYPE);
		assertThat(ByteCode.unboxedType(Type.getType(Short.class))).isSameAs(Type.SHORT_TYPE);
		assertThat(ByteCode.unboxedType(Type.getType(Integer.class))).isSameAs(Type.INT_TYPE);
		assertThat(ByteCode.unboxedType(Type.getType(Long.class))).isSameAs(Type.LONG_TYPE);
		assertThat(ByteCode.unboxedType(Type.getType(Float.class))).isSameAs(Type.FLOAT_TYPE);
		assertThat(ByteCode.unboxedType(Type.getType(Double.class))).isSameAs(Type.DOUBLE_TYPE);
		assertThat(ByteCode.unboxedType(Type.getType(Character.class))).isSameAs(Type.CHAR_TYPE);
		assertThat(ByteCode.unboxedType(Type.getType(Void.class))).isSameAs(Type.VOID_TYPE);
		assertThat(ByteCode.unboxedType(Type.getType(Object.class))).isNull();
	}

	@Test
	public void testUnboxingFactory() throws Exception {
		assertThat(ByteCode.unboxingFactory(Type.BOOLEAN_TYPE)).isEqualTo("booleanValue");
		assertThat(ByteCode.unboxingFactory(Type.BYTE_TYPE)).isEqualTo("byteValue");
		assertThat(ByteCode.unboxingFactory(Type.SHORT_TYPE)).isEqualTo("shortValue");
		assertThat(ByteCode.unboxingFactory(Type.INT_TYPE)).isEqualTo("intValue");
		assertThat(ByteCode.unboxingFactory(Type.LONG_TYPE)).isEqualTo("longValue");
		assertThat(ByteCode.unboxingFactory(Type.FLOAT_TYPE)).isEqualTo("floatValue");
		assertThat(ByteCode.unboxingFactory(Type.DOUBLE_TYPE)).isEqualTo("doubleValue");
		assertThat(ByteCode.unboxingFactory(Type.CHAR_TYPE)).isEqualTo("charValue");
		assertThat(ByteCode.unboxingFactory(Type.VOID_TYPE)).isNull();
	}

	@Test
	public void testBoxingFactory() throws Exception {
		assertThat(ByteCode.boxingFactory(Type.BOOLEAN_TYPE)).isEqualTo("valueOf");
		assertThat(ByteCode.boxingFactory(Type.BYTE_TYPE)).isEqualTo("valueOf");
		assertThat(ByteCode.boxingFactory(Type.SHORT_TYPE)).isEqualTo("valueOf");
		assertThat(ByteCode.boxingFactory(Type.INT_TYPE)).isEqualTo("valueOf");
		assertThat(ByteCode.boxingFactory(Type.LONG_TYPE)).isEqualTo("valueOf");
		assertThat(ByteCode.boxingFactory(Type.FLOAT_TYPE)).isEqualTo("valueOf");
		assertThat(ByteCode.boxingFactory(Type.DOUBLE_TYPE)).isEqualTo("valueOf");
		assertThat(ByteCode.boxingFactory(Type.CHAR_TYPE)).isEqualTo("valueOf");
		assertThat(ByteCode.boxingFactory(Type.VOID_TYPE)).isNull();
	}

	@Test
	public void testIsStatic() throws Exception {
		assertThat(ByteCode.isStatic(methodWithModifiers(Opcodes.ACC_STATIC))).isTrue();
		assertThat(ByteCode.isStatic(methodWithModifiers(~Opcodes.ACC_STATIC))).isFalse();
	}

	@Test
	public void testIsNative() throws Exception {
		assertThat(ByteCode.isNative(methodWithModifiers(Opcodes.ACC_NATIVE))).isTrue();
		assertThat(ByteCode.isNative(methodWithModifiers(~Opcodes.ACC_NATIVE))).isFalse();
	}

	@Test
	public void testReturnsResult() throws Exception {
		assertThat(ByteCode.returnsResult(methodWithDesc("()I"))).isTrue();
		assertThat(ByteCode.returnsResult(methodWithDesc("()V"))).isFalse();
		assertThat(ByteCode.returnsResult(methodInsnWithDesc("()I"))).isTrue();
		assertThat(ByteCode.returnsResult(methodInsnWithDesc("()V"))).isFalse();
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
		assertThat(ByteCode.isPrimitive(Type.INT_TYPE)).isTrue();
		assertThat(ByteCode.isPrimitive(Type.getType(int[].class))).isFalse();
		assertThat(ByteCode.isPrimitive(Type.getType(Integer.class))).isFalse();
		assertThat(ByteCode.isPrimitive(Type.getType(Object.class))).isFalse();
	}

	@Test
	public void testIsArray() throws Exception {
		assertThat(ByteCode.isArray(Type.getType(int[].class))).isTrue();
		assertThat(ByteCode.isArray(Type.getType(int[][].class))).isTrue();
		assertThat(ByteCode.isArray(Type.getType(Object[].class))).isTrue();
		assertThat(ByteCode.isArray(Type.INT_TYPE)).isFalse();
		assertThat(ByteCode.isArray(Type.getType(Integer.class))).isFalse();
		assertThat(ByteCode.isArray(Type.getType(Object.class))).isFalse();
	}

	@Test
	public void testClassFrom() throws Exception {
		assertThat(ByteCode.classFrom("net/amygdalum/testrecorder/util/testobjects/PublicEnum")).isEqualTo(PublicEnum.class);
		assertThat(ByteCode.classFrom("net/amygdalum/testrecorder/util/testobjects/Simple", ByteCodeTest.class.getClassLoader())).isEqualTo(Simple.class);
		assertThat(ByteCode.classFrom(Type.getType(Complex[].class))).isEqualTo(Complex[].class);
		assertThat(ByteCode.classFrom(Type.getType(int.class))).isEqualTo(int.class);
		assertThatThrownBy(() -> ByteCode.classFrom("net/amygdalum/testrecorder/util/testobjects/NotExisting"))
			.isInstanceOf(ByteCodeException.class)
			.hasCauseExactlyInstanceOf(ClassNotFoundException.class);
	}

	@Test
	public void testArgumentTypes() throws Exception {
		assertThat(ByteCode.argumentTypesFrom("()V")).isEmpty();
		assertThat(ByteCode.argumentTypesFrom("(Ljava/lang/Object;)I")).containsExactly(Object.class);
		assertThat(ByteCode.argumentTypesFrom("(IC)V")).containsExactly(int.class, char.class);
	}

	@Test
	public void testResultType() throws Exception {
		assertThat(ByteCode.resultTypeFrom("()V")).isEqualTo(void.class);
		assertThat(ByteCode.resultTypeFrom("(Ljava/lang/Object;)I")).isEqualTo(int.class);
		assertThat(ByteCode.resultTypeFrom("(IC)Ljava/lang/String;")).isEqualTo(String.class);
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testByteCodePrint(@LogLevel("info") ByteArrayOutputStream info) throws Exception {
		InsnNode insn = new InsnNode(POP);

		InsnNode result = ByteCode.print(insn);

		assertThat(result).isEqualTo(insn);
		assertThat(info.toString()).contains("POP");
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testByteCodePrintList(@LogLevel("info") ByteArrayOutputStream info) throws Exception {
		InsnList list = new InsnList();
		list.add(new InsnNode(DUP));
		
		InsnList result = ByteCode.print(list);
		
		assertThat(result).isEqualTo(list);
		assertThat(info.toString()).contains("[DUP]");
	}
	
}
