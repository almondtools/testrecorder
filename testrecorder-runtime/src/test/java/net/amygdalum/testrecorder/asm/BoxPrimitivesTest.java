package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public class BoxPrimitivesTest {

	private MethodContext context;

	@BeforeEach
	void before() {
		context = new MethodContext(AClass.classNode(), AClass.virtualMethodNode());
	}

	@Nested
	class testBoxPrimitives {
		@Test
		void onBoolean() throws Exception {
			InsnList insns = new BoxPrimitives(Type.getType(boolean.class))
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"INVOKESTATIC java/lang/Boolean.valueOf (Z)Ljava/lang/Boolean;");
		}

		@Test
		void onByte() throws Exception {
			InsnList insns = new BoxPrimitives(Type.getType(byte.class))
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"INVOKESTATIC java/lang/Byte.valueOf (B)Ljava/lang/Byte;");
		}

		@Test
		void onShort() throws Exception {
			InsnList insns = new BoxPrimitives(Type.getType(short.class))
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"INVOKESTATIC java/lang/Short.valueOf (S)Ljava/lang/Short;");
		}

		@Test
		void onInteger() throws Exception {
			InsnList insns = new BoxPrimitives(Type.getType(int.class))
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;");
		}

		@Test
		void onLong() throws Exception {
			InsnList insns = new BoxPrimitives(Type.getType(long.class))
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"INVOKESTATIC java/lang/Long.valueOf (J)Ljava/lang/Long;");
		}

		@Test
		void onFloat() throws Exception {
			InsnList insns = new BoxPrimitives(Type.getType(float.class))
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"INVOKESTATIC java/lang/Float.valueOf (F)Ljava/lang/Float;");
		}

		@Test
		void onDouble() throws Exception {
			InsnList insns = new BoxPrimitives(Type.getType(double.class))
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"INVOKESTATIC java/lang/Double.valueOf (D)Ljava/lang/Double;");
		}

		@Test
		void onChar() throws Exception {
			InsnList insns = new BoxPrimitives(Type.getType(char.class))
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"INVOKESTATIC java/lang/Character.valueOf (C)Ljava/lang/Character;");
		}
	}
}
