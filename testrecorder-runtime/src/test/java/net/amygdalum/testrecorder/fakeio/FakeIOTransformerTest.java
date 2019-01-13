package net.amygdalum.testrecorder.fakeio;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.util.InstrumentationMethod.instrumentMethod;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.InsnList;

import net.amygdalum.testrecorder.asm.ByteCode;
import net.amygdalum.testrecorder.asm.MethodContext;
import net.amygdalum.testrecorder.util.InstrumentationMethod;
import net.amygdalum.testrecorder.util.testobjects.Example;

public class FakeIOTransformerTest {

	@Nested
	class createIOFake {

		@Nested
		class Direct {
			@Test
			void onPrimitiveResultNoArgs() throws Exception {
				InstrumentationMethod unit = instrumentMethod(Example.class, "primitiveResultNoArgs");
				InsnList insnlist = new FakeIOTransformer.DefaultTask(unit.classNode, emptyList()).createIOFake(unit.methodNode).build(new MethodContext(unit.classNode, unit.methodNode));

				assertThat(ByteCode.toString(insnlist)).containsExactly(
					"ALOAD 0",
					"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
					"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
					"ALOAD 0",
					"LDC \"primitiveResultNoArgs\"",
					"LDC \"()Z\"",
					"LDC 0",
					"ANEWARRAY java/lang/Object",
					"INVOKESTATIC net/amygdalum/testrecorder/fakeio/FakeIO.callFake (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
					"DUP",
					"GETSTATIC net/amygdalum/testrecorder/fakeio/FakeIO.NO_RESULT : Ljava/lang/Object;",
					"IF_ACMPEQ L0",
					"CHECKCAST java/lang/Boolean",
					"INVOKEVIRTUAL java/lang/Boolean.booleanValue ()Z",
					"IRETURN",
					"L0",
					"POP");
			}

			@Test
			void onObjectResultNoArgs() throws Exception {
				InstrumentationMethod unit = instrumentMethod(Example.class, "objectResultNoArgs");
				InsnList insnlist = new FakeIOTransformer.DefaultTask(unit.classNode, emptyList()).createIOFake(unit.methodNode).build(new MethodContext(unit.classNode, unit.methodNode));

				assertThat(ByteCode.toString(insnlist)).containsExactly(
					"ALOAD 0",
					"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
					"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
					"ALOAD 0",
					"LDC \"objectResultNoArgs\"",
					"LDC \"()Lnet/amygdalum/testrecorder/util/testobjects/ResultObject;\"",
					"LDC 0",
					"ANEWARRAY java/lang/Object",
					"INVOKESTATIC net/amygdalum/testrecorder/fakeio/FakeIO.callFake (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
					"DUP",
					"GETSTATIC net/amygdalum/testrecorder/fakeio/FakeIO.NO_RESULT : Ljava/lang/Object;",
					"IF_ACMPEQ L0",
					"CHECKCAST net/amygdalum/testrecorder/util/testobjects/ResultObject",
					"ARETURN",
					"L0",
					"POP");
			}

			@Test
			void onNoResultPrimitiveArg() throws Exception {
				InstrumentationMethod unit = instrumentMethod(Example.class, "noResultPrimitiveArg");
				InsnList insnlist = new FakeIOTransformer.DefaultTask(unit.classNode, emptyList()).createIOFake(unit.methodNode).build(new MethodContext(unit.classNode, unit.methodNode));

				assertThat(ByteCode.toString(insnlist)).containsExactly(
					"ALOAD 0",
					"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
					"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
					"ALOAD 0",
					"LDC \"noResultPrimitiveArg\"",
					"LDC \"(I)V\"",
					"LDC 1",
					"ANEWARRAY java/lang/Object",
					"DUP",
					"LDC 0",
					"ILOAD 1",
					"INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;",
					"AASTORE",
					"INVOKESTATIC net/amygdalum/testrecorder/fakeio/FakeIO.callFake (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
					"DUP",
					"GETSTATIC net/amygdalum/testrecorder/fakeio/FakeIO.NO_RESULT : Ljava/lang/Object;",
					"IF_ACMPEQ L0",
					"POP",
					"RETURN",
					"L0",
					"POP");
			}

			@Test
			void onNoResultArrayArg() throws Exception {
				InstrumentationMethod unit = instrumentMethod(Example.class, "noResultArrayArg");
				InsnList insnlist = new FakeIOTransformer.DefaultTask(unit.classNode, emptyList()).createIOFake(unit.methodNode).build(new MethodContext(unit.classNode, unit.methodNode));

				assertThat(ByteCode.toString(insnlist)).containsExactly(
					"ALOAD 0",
					"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
					"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
					"ALOAD 0",
					"LDC \"noResultArrayArg\"",
					"LDC \"([C)V\"",
					"LDC 1",
					"ANEWARRAY java/lang/Object",
					"DUP",
					"LDC 0",
					"ALOAD 1",
					"AASTORE",
					"INVOKESTATIC net/amygdalum/testrecorder/fakeio/FakeIO.callFake (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
					"DUP",
					"GETSTATIC net/amygdalum/testrecorder/fakeio/FakeIO.NO_RESULT : Ljava/lang/Object;",
					"IF_ACMPEQ L0",
					"POP",
					"RETURN",
					"L0",
					"POP");
			}

			@Test
			void onNoResultObjectArrayArg() throws Exception {
				InstrumentationMethod unit = instrumentMethod(Example.class, "noResultObjectArrayArg");
				InsnList insnlist = new FakeIOTransformer.DefaultTask(unit.classNode, emptyList()).createIOFake(unit.methodNode).build(new MethodContext(unit.classNode, unit.methodNode));

				assertThat(ByteCode.toString(insnlist)).containsExactly(
					"ALOAD 0",
					"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
					"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
					"ALOAD 0",
					"LDC \"noResultObjectArrayArg\"",
					"LDC \"([Ljava/lang/String;)V\"",
					"LDC 1",
					"ANEWARRAY java/lang/Object",
					"DUP",
					"LDC 0",
					"ALOAD 1",
					"AASTORE",
					"INVOKESTATIC net/amygdalum/testrecorder/fakeio/FakeIO.callFake (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
					"DUP",
					"GETSTATIC net/amygdalum/testrecorder/fakeio/FakeIO.NO_RESULT : Ljava/lang/Object;",
					"IF_ACMPEQ L0",
					"POP",
					"RETURN",
					"L0",
					"POP");
			}

			@Test
			void onNoResultObjectArg() throws Exception {
				InstrumentationMethod unit = instrumentMethod(Example.class, "noResultObjectArg");
				InsnList insnlist = new FakeIOTransformer.DefaultTask(unit.classNode, emptyList()).createIOFake(unit.methodNode).build(new MethodContext(unit.classNode, unit.methodNode));

				assertThat(ByteCode.toString(insnlist)).containsExactly(
					"ALOAD 0",
					"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
					"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
					"ALOAD 0",
					"LDC \"noResultObjectArg\"",
					"LDC \"(Lnet/amygdalum/testrecorder/util/testobjects/ArgumentObject;)V\"",
					"LDC 1",
					"ANEWARRAY java/lang/Object",
					"DUP",
					"LDC 0",
					"ALOAD 1",
					"AASTORE",
					"INVOKESTATIC net/amygdalum/testrecorder/fakeio/FakeIO.callFake (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
					"DUP",
					"GETSTATIC net/amygdalum/testrecorder/fakeio/FakeIO.NO_RESULT : Ljava/lang/Object;",
					"IF_ACMPEQ L0",
					"POP",
					"RETURN",
					"L0",
					"POP");
			}

			@Test
			void onObjectResultMixedArgs() throws Exception {
				InstrumentationMethod unit = instrumentMethod(Example.class, "objectResultMixedArgs");
				InsnList insnlist = new FakeIOTransformer.DefaultTask(unit.classNode, emptyList()).createIOFake(unit.methodNode).build(new MethodContext(unit.classNode, unit.methodNode));

				assertThat(ByteCode.toString(insnlist)).containsExactly(
					"ALOAD 0",
					"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
					"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
					"ALOAD 0",
					"LDC \"objectResultMixedArgs\"",
					"LDC \"(DLnet/amygdalum/testrecorder/util/testobjects/ArgumentObject;)Lnet/amygdalum/testrecorder/util/testobjects/ResultObject;\"",
					"LDC 2",
					"ANEWARRAY java/lang/Object",
					"DUP",
					"LDC 0",
					"DLOAD 1",
					"INVOKESTATIC java/lang/Double.valueOf (D)Ljava/lang/Double;",
					"AASTORE",
					"DUP",
					"LDC 1",
					"ALOAD 3",
					"AASTORE",
					"INVOKESTATIC net/amygdalum/testrecorder/fakeio/FakeIO.callFake (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
					"DUP",
					"GETSTATIC net/amygdalum/testrecorder/fakeio/FakeIO.NO_RESULT : Ljava/lang/Object;",
					"IF_ACMPEQ L0",
					"CHECKCAST net/amygdalum/testrecorder/util/testobjects/ResultObject",
					"ARETURN",
					"L0",
					"POP");
			}

			@Test
			void onStaticPrimitiveResultMixedArgs() throws Exception {
				InstrumentationMethod unit = instrumentMethod(Example.class, "staticPrimitiveResultMixedArgs");
				InsnList insnlist = new FakeIOTransformer.DefaultTask(unit.classNode, emptyList()).createIOFake(unit.methodNode).build(new MethodContext(unit.classNode, unit.methodNode));

				assertThat(ByteCode.toString(insnlist)).containsExactly(
					"LDC \"net.amygdalum.testrecorder.util.testobjects.Example\"",
					"ACONST_NULL",
					"LDC \"staticPrimitiveResultMixedArgs\"",
					"LDC \"(Lnet/amygdalum/testrecorder/util/testobjects/ArgumentObject;C)J\"",
					"LDC 2",
					"ANEWARRAY java/lang/Object",
					"DUP",
					"LDC 0",
					"ALOAD 0",
					"AASTORE",
					"DUP",
					"LDC 1",
					"ILOAD 1",
					"INVOKESTATIC java/lang/Character.valueOf (C)Ljava/lang/Character;",
					"AASTORE",
					"INVOKESTATIC net/amygdalum/testrecorder/fakeio/FakeIO.callFake (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
					"DUP",
					"GETSTATIC net/amygdalum/testrecorder/fakeio/FakeIO.NO_RESULT : Ljava/lang/Object;",
					"IF_ACMPEQ L0",
					"CHECKCAST java/lang/Long",
					"INVOKEVIRTUAL java/lang/Long.longValue ()J",
					"LRETURN",
					"L0",
					"POP");
			}
		}

		@Nested
		class Bridged {

			@Test
			void onPrimitiveResultNoArgs() throws Exception {
				InstrumentationMethod unit = instrumentMethod(Example.class, "primitiveResultNoArgs");
				InsnList insnlist = new FakeIOTransformer.BridgedTask(unit.classNode, emptyList()).createIOFake(unit.methodNode).build(new MethodContext(unit.classNode, unit.methodNode));

				assertThat(ByteCode.toString(insnlist)).containsExactly(
					"ALOAD 0",
					"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
					"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
					"ALOAD 0",
					"LDC \"primitiveResultNoArgs\"",
					"LDC \"()Z\"",
					"LDC 0",
					"ANEWARRAY java/lang/Object",
					"INVOKESTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.callFake (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
					"DUP",
					"GETSTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.NO_RESULT : Ljava/lang/Object;",
					"IF_ACMPEQ L0",
					"CHECKCAST java/lang/Boolean",
					"INVOKEVIRTUAL java/lang/Boolean.booleanValue ()Z",
					"IRETURN",
					"L0",
					"POP");
			}

			@Test
			void onObjectResultNoArgs() throws Exception {
				InstrumentationMethod unit = instrumentMethod(Example.class, "objectResultNoArgs");
				InsnList insnlist = new FakeIOTransformer.BridgedTask(unit.classNode, emptyList()).createIOFake(unit.methodNode).build(new MethodContext(unit.classNode, unit.methodNode));

				assertThat(ByteCode.toString(insnlist)).containsExactly(
					"ALOAD 0",
					"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
					"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
					"ALOAD 0",
					"LDC \"objectResultNoArgs\"",
					"LDC \"()Lnet/amygdalum/testrecorder/util/testobjects/ResultObject;\"",
					"LDC 0",
					"ANEWARRAY java/lang/Object",
					"INVOKESTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.callFake (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
					"DUP",
					"GETSTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.NO_RESULT : Ljava/lang/Object;",
					"IF_ACMPEQ L0",
					"CHECKCAST net/amygdalum/testrecorder/util/testobjects/ResultObject",
					"ARETURN",
					"L0",
					"POP");
			}

			@Test
			void onNoResultPrimitiveArg() throws Exception {
				InstrumentationMethod unit = instrumentMethod(Example.class, "noResultPrimitiveArg");
				InsnList insnlist = new FakeIOTransformer.BridgedTask(unit.classNode, emptyList()).createIOFake(unit.methodNode).build(new MethodContext(unit.classNode, unit.methodNode));

				assertThat(ByteCode.toString(insnlist)).containsExactly(
					"ALOAD 0",
					"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
					"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
					"ALOAD 0",
					"LDC \"noResultPrimitiveArg\"",
					"LDC \"(I)V\"",
					"LDC 1",
					"ANEWARRAY java/lang/Object",
					"DUP",
					"LDC 0",
					"ILOAD 1",
					"INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;",
					"AASTORE",
					"INVOKESTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.callFake (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
					"DUP",
					"GETSTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.NO_RESULT : Ljava/lang/Object;",
					"IF_ACMPEQ L0",
					"POP",
					"RETURN",
					"L0",
					"POP");
			}

			@Test
			void onNoResultArrayArg() throws Exception {
				InstrumentationMethod unit = instrumentMethod(Example.class, "noResultArrayArg");
				InsnList insnlist = new FakeIOTransformer.BridgedTask(unit.classNode, emptyList()).createIOFake(unit.methodNode).build(new MethodContext(unit.classNode, unit.methodNode));

				assertThat(ByteCode.toString(insnlist)).containsExactly(
					"ALOAD 0",
					"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
					"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
					"ALOAD 0",
					"LDC \"noResultArrayArg\"",
					"LDC \"([C)V\"",
					"LDC 1",
					"ANEWARRAY java/lang/Object",
					"DUP",
					"LDC 0",
					"ALOAD 1",
					"AASTORE",
					"INVOKESTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.callFake (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
					"DUP",
					"GETSTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.NO_RESULT : Ljava/lang/Object;",
					"IF_ACMPEQ L0",
					"POP",
					"RETURN",
					"L0",
					"POP");
			}

			@Test
			void onNoResultObjectArrayArg() throws Exception {
				InstrumentationMethod unit = instrumentMethod(Example.class, "noResultObjectArrayArg");
				InsnList insnlist = new FakeIOTransformer.BridgedTask(unit.classNode, emptyList()).createIOFake(unit.methodNode).build(new MethodContext(unit.classNode, unit.methodNode));

				assertThat(ByteCode.toString(insnlist)).containsExactly(
					"ALOAD 0",
					"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
					"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
					"ALOAD 0",
					"LDC \"noResultObjectArrayArg\"",
					"LDC \"([Ljava/lang/String;)V\"",
					"LDC 1",
					"ANEWARRAY java/lang/Object",
					"DUP",
					"LDC 0",
					"ALOAD 1",
					"AASTORE",
					"INVOKESTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.callFake (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
					"DUP",
					"GETSTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.NO_RESULT : Ljava/lang/Object;",
					"IF_ACMPEQ L0",
					"POP",
					"RETURN",
					"L0",
					"POP");
			}

			@Test
			void onNoResultObjectArg() throws Exception {
				InstrumentationMethod unit = instrumentMethod(Example.class, "noResultObjectArg");
				InsnList insnlist = new FakeIOTransformer.BridgedTask(unit.classNode, emptyList()).createIOFake(unit.methodNode).build(new MethodContext(unit.classNode, unit.methodNode));

				assertThat(ByteCode.toString(insnlist)).containsExactly(
					"ALOAD 0",
					"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
					"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
					"ALOAD 0",
					"LDC \"noResultObjectArg\"",
					"LDC \"(Lnet/amygdalum/testrecorder/util/testobjects/ArgumentObject;)V\"",
					"LDC 1",
					"ANEWARRAY java/lang/Object",
					"DUP",
					"LDC 0",
					"ALOAD 1",
					"AASTORE",
					"INVOKESTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.callFake (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
					"DUP",
					"GETSTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.NO_RESULT : Ljava/lang/Object;",
					"IF_ACMPEQ L0",
					"POP",
					"RETURN",
					"L0",
					"POP");
			}

			@Test
			void onObjectResultMixedArgs() throws Exception {
				InstrumentationMethod unit = instrumentMethod(Example.class, "objectResultMixedArgs");
				InsnList insnlist = new FakeIOTransformer.BridgedTask(unit.classNode, emptyList()).createIOFake(unit.methodNode).build(new MethodContext(unit.classNode, unit.methodNode));

				assertThat(ByteCode.toString(insnlist)).containsExactly(
					"ALOAD 0",
					"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
					"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
					"ALOAD 0",
					"LDC \"objectResultMixedArgs\"",
					"LDC \"(DLnet/amygdalum/testrecorder/util/testobjects/ArgumentObject;)Lnet/amygdalum/testrecorder/util/testobjects/ResultObject;\"",
					"LDC 2",
					"ANEWARRAY java/lang/Object",
					"DUP",
					"LDC 0",
					"DLOAD 1",
					"INVOKESTATIC java/lang/Double.valueOf (D)Ljava/lang/Double;",
					"AASTORE",
					"DUP",
					"LDC 1",
					"ALOAD 3",
					"AASTORE",
					"INVOKESTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.callFake (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
					"DUP",
					"GETSTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.NO_RESULT : Ljava/lang/Object;",
					"IF_ACMPEQ L0",
					"CHECKCAST net/amygdalum/testrecorder/util/testobjects/ResultObject",
					"ARETURN",
					"L0",
					"POP");
			}

			@Test
			void onStaticPrimitiveResultMixedArgs() throws Exception {
				InstrumentationMethod unit = instrumentMethod(Example.class, "staticPrimitiveResultMixedArgs");
				InsnList insnlist = new FakeIOTransformer.BridgedTask(unit.classNode, emptyList()).createIOFake(unit.methodNode).build(new MethodContext(unit.classNode, unit.methodNode));

				assertThat(ByteCode.toString(insnlist)).containsExactly(
					"LDC \"net.amygdalum.testrecorder.util.testobjects.Example\"",
					"ACONST_NULL",
					"LDC \"staticPrimitiveResultMixedArgs\"",
					"LDC \"(Lnet/amygdalum/testrecorder/util/testobjects/ArgumentObject;C)J\"",
					"LDC 2",
					"ANEWARRAY java/lang/Object",
					"DUP",
					"LDC 0",
					"ALOAD 0",
					"AASTORE",
					"DUP",
					"LDC 1",
					"ILOAD 1",
					"INVOKESTATIC java/lang/Character.valueOf (C)Ljava/lang/Character;",
					"AASTORE",
					"INVOKESTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.callFake (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
					"DUP",
					"GETSTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.NO_RESULT : Ljava/lang/Object;",
					"IF_ACMPEQ L0",
					"CHECKCAST java/lang/Long",
					"INVOKEVIRTUAL java/lang/Long.longValue ()J",
					"LRETURN",
					"L0",
					"POP");
			}

		}
	}

}
