package net.amygdalum.testrecorder;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;

import com.almondtools.conmatch.conventions.UtilityClassMatcher;

public class ByteCodeTest {

	@Test
	public void testByteCode() throws Exception {
		assertThat(ByteCode.class, UtilityClassMatcher.isUtilityClass());
	}

	@Test
	public void testMemorizeLocal0() throws Exception {
		InsnList memorizeVoid = ByteCode.memorizeLocal(Type.getType(void.class), 1);

		assertThat(memorizeVoid.size(), equalTo(0));
	}

	@Test
	public void testMemorizeLocal1() throws Exception {
		InsnList memorizeInt = ByteCode.memorizeLocal(Type.getType(int.class), 1);

		assertThat(memorizeInt.size(), equalTo(3));
		assertThat(toString(memorizeInt), contains(
			"DUP",
			"INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;",
			"ASTORE 1"));
	}

	@Test
	public void testMemorizeLocal2() throws Exception {
		InsnList memorizeLong = ByteCode.memorizeLocal(Type.getType(long.class), 2);

		assertThat(memorizeLong.size(), equalTo(3));
		assertThat(toString(memorizeLong), contains(
			"DUP2",
			"INVOKESTATIC java/lang/Long.valueOf (J)Ljava/lang/Long;",
			"ASTORE 2"));
	}

	@Test
	public void testRecallLocal() throws Exception {
		AbstractInsnNode insn = ByteCode.recallLocal(3);

		assertThat(toString(insn), equalTo("ALOAD 3"));
	}

	@Test
	public void testPushTypes() throws Exception {
		InsnList pushTypes = ByteCode.pushTypes(Type.getType(int.class), Type.getType(double.class), Type.getType(Object.class));
		assertThat(toString(pushTypes), contains(
			"LDC 3",
			"ANEWARRAY java/lang/reflect/Type",
			"DUP",
			"LDC 0",
			"GETSTATIC java/lang/Integer.TYPE : Ljava/lang/Class;",
			"AASTORE",
			"DUP",
			"LDC 1",
			"GETSTATIC java/lang/Double.TYPE : Ljava/lang/Class;",
			"AASTORE",
			"DUP",
			"LDC 2",
			"LDC Ljava/lang/Object;.class",
			"AASTORE"));
	}

	@Test
	public void testPushType() throws Exception {
		assertThat(toString(ByteCode.pushType(Type.getType(byte.class))), equalTo(
			"GETSTATIC java/lang/Byte.TYPE : Ljava/lang/Class;"));
		assertThat(toString(ByteCode.pushType(Type.getType(short.class))), equalTo(
			"GETSTATIC java/lang/Short.TYPE : Ljava/lang/Class;"));
		assertThat(toString(ByteCode.pushType(Type.getType(int.class))), equalTo(
			"GETSTATIC java/lang/Integer.TYPE : Ljava/lang/Class;"));
		assertThat(toString(ByteCode.pushType(Type.getType(long.class))), equalTo(
			"GETSTATIC java/lang/Long.TYPE : Ljava/lang/Class;"));
		assertThat(toString(ByteCode.pushType(Type.getType(float.class))), equalTo(
			"GETSTATIC java/lang/Float.TYPE : Ljava/lang/Class;"));
		assertThat(toString(ByteCode.pushType(Type.getType(double.class))), equalTo(
			"GETSTATIC java/lang/Double.TYPE : Ljava/lang/Class;"));
		assertThat(toString(ByteCode.pushType(Type.getType(boolean.class))), equalTo(
			"GETSTATIC java/lang/Boolean.TYPE : Ljava/lang/Class;"));
		assertThat(toString(ByteCode.pushType(Type.getType(char.class))), equalTo(
			"GETSTATIC java/lang/Character.TYPE : Ljava/lang/Class;"));
		assertThat(toString(ByteCode.pushType(Type.getType(String.class))), equalTo(
			"LDC Ljava/lang/String;.class"));
	}

	@Test
	public void testGetRawType() throws Exception {
		assertThat(ByteCode.getRawType('Z'), equalTo(boolean.class));
		assertThat(ByteCode.getRawType('C'), equalTo(char.class));
		assertThat(ByteCode.getRawType('B'), equalTo(byte.class));
		assertThat(ByteCode.getRawType('S'), equalTo(short.class));
		assertThat(ByteCode.getRawType('I'), equalTo(int.class));
		assertThat(ByteCode.getRawType('F'), equalTo(float.class));
		assertThat(ByteCode.getRawType('J'), equalTo(long.class));
		assertThat(ByteCode.getRawType('D'), equalTo(double.class));
		assertThat(ByteCode.getRawType('V'), equalTo(void.class));
		assertThat(ByteCode.getRawType(' '), equalTo(void.class));
	}

	@Test
	public void testGetBoxedType() throws Exception {
		assertThat(ByteCode.getBoxedType('Z'), equalTo(Boolean.class));
		assertThat(ByteCode.getBoxedType('C'), equalTo(Character.class));
		assertThat(ByteCode.getBoxedType('B'), equalTo(Byte.class));
		assertThat(ByteCode.getBoxedType('S'), equalTo(Short.class));
		assertThat(ByteCode.getBoxedType('I'), equalTo(Integer.class));
		assertThat(ByteCode.getBoxedType('F'), equalTo(Float.class));
		assertThat(ByteCode.getBoxedType('J'), equalTo(Long.class));
		assertThat(ByteCode.getBoxedType('D'), equalTo(Double.class));
		assertThat(ByteCode.getBoxedType('V'), equalTo(Void.class));
		assertThat(ByteCode.getBoxedType(' '), equalTo(Void.class));
	}

	@Test
	public void testConstructorDescriptor() throws Exception {
		assertThat(ByteCode.constructorDescriptor(String.class), equalTo("()V"));
		assertThat(ByteCode.constructorDescriptor(String.class, char[].class), equalTo("([C)V"));
	}

	@Test
	public void testMethodDescriptor() throws Exception {
		assertThat(ByteCode.methodDescriptor(String.class, "getBytes"), equalTo("()[B"));
		assertThat(ByteCode.methodDescriptor(String.class, "valueOf", char[].class), equalTo("([C)Ljava/lang/String;"));
	}

	@Test
	public void testPushAsArrayIndexType() throws Exception {
		InsnList push = ByteCode.pushAsArray(new int[] { 2, 5 }, Type.getType(char.class), Type.getType(Double.class));
		assertThat(toString(push), contains(
			"LDC 2",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"ILOAD 2",
			"INVOKESTATIC java/lang/Character.valueOf (C)Ljava/lang/Character;",
			"AASTORE",
			"DUP",
			"LDC 1",
			"ALOAD 5",
			"AASTORE"));
	}

	@Test
	public void testPushAsArrayLocalVariableNode() throws Exception {
		InsnList push = ByteCode.pushAsArray(asList(
			new LocalVariableNode("Var2", "C", null, null, null, 2),
			new LocalVariableNode("Var5", "Ljava/lang/Double;", null, null, null, 5)));
		assertThat(toString(push), contains(
			"LDC 2",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"ILOAD 2",
			"INVOKESTATIC java/lang/Character.valueOf (C)Ljava/lang/Character;",
			"AASTORE",
			"DUP",
			"LDC 1",
			"ALOAD 5",
			"AASTORE"));
	}

	private List<String> toString(InsnList insns) {
		return ByteCode.toString(insns).stream()
			.map(String::trim)
			.collect(toList());
	}

	private String toString(AbstractInsnNode insn) {
		return ByteCode.toString(insn).trim();
	}

}
