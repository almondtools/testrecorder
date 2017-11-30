package net.amygdalum.testrecorder;

import static net.amygdalum.testrecorder.InstrumentationUnit.instrument;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.tree.InsnList;

import net.amygdalum.testrecorder.util.ByteCode;

public class FakeIOTransformerTest {

	private FakeIOTransformer fakeIOTransformer;

	@Before
	public void before() throws Exception {
		fakeIOTransformer = new FakeIOTransformer();
	}

	@Test
	public void testCreateDirectIOFakePrimitiveResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "primitiveResultNoArgs");
		InsnList insnlist = fakeIOTransformer.createDirectIOFake(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"ALOAD 0",
			"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
			"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
			"NEW java/lang/Throwable",
			"DUP",
			"INVOKESPECIAL java/lang/Throwable.<init> ()V",
			"INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;",
			"ALOAD 0",
			"LDC \"primitiveResultNoArgs\"",
			"LDC \"()Z\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKESTATIC net/amygdalum/testrecorder/runtime/FakeIO.callFake (Ljava/lang/String;[Ljava/lang/StackTraceElement;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
			"DUP",
			"GETSTATIC net/amygdalum/testrecorder/runtime/FakeIO.NO_RESULT : Ljava/lang/Object;",
			"IF_ACMPEQ L0",
			"CHECKCAST java/lang/Boolean",
			"INVOKEVIRTUAL java/lang/Boolean.booleanValue ()Z",
			"IRETURN",
			"L0",
			"POP"));
	}

	@Test
	public void testCreateDirectIOFakeObjectResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultNoArgs");
		InsnList insnlist = fakeIOTransformer.createDirectIOFake(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"ALOAD 0",
			"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
			"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
			"NEW java/lang/Throwable",
			"DUP",
			"INVOKESPECIAL java/lang/Throwable.<init> ()V",
			"INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;",
			"ALOAD 0",
			"LDC \"objectResultNoArgs\"",
			"LDC \"()Lnet/amygdalum/testrecorder/ResultObject;\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKESTATIC net/amygdalum/testrecorder/runtime/FakeIO.callFake (Ljava/lang/String;[Ljava/lang/StackTraceElement;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
			"DUP",
			"GETSTATIC net/amygdalum/testrecorder/runtime/FakeIO.NO_RESULT : Ljava/lang/Object;",
			"IF_ACMPEQ L0",
			"CHECKCAST net/amygdalum/testrecorder/ResultObject",
			"ARETURN",
			"L0",
			"POP"));
	}

	@Test
	public void testCreateDirectIOFakeNoResultPrimitiveArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultPrimitiveArg");
		InsnList insnlist = fakeIOTransformer.createDirectIOFake(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"ALOAD 0",
			"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
			"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
			"NEW java/lang/Throwable",
			"DUP",
			"INVOKESPECIAL java/lang/Throwable.<init> ()V",
			"INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;",
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
			"INVOKESTATIC net/amygdalum/testrecorder/runtime/FakeIO.callFake (Ljava/lang/String;[Ljava/lang/StackTraceElement;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
			"DUP",
			"GETSTATIC net/amygdalum/testrecorder/runtime/FakeIO.NO_RESULT : Ljava/lang/Object;",
			"IF_ACMPEQ L0",
			"POP",
			"RETURN",
			"L0",
			"POP"));
	}

	@Test
	public void testCreateDirectIOFakeNoResultArrayArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultArrayArg");
		InsnList insnlist = fakeIOTransformer.createDirectIOFake(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"ALOAD 0",
			"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
			"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
			"NEW java/lang/Throwable",
			"DUP",
			"INVOKESPECIAL java/lang/Throwable.<init> ()V",
			"INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;",
			"ALOAD 0",
			"LDC \"noResultArrayArg\"",
			"LDC \"([C)V\"",
			"LDC 1",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"ALOAD 1",
			"AASTORE",
			"INVOKESTATIC net/amygdalum/testrecorder/runtime/FakeIO.callFake (Ljava/lang/String;[Ljava/lang/StackTraceElement;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
			"DUP",
			"GETSTATIC net/amygdalum/testrecorder/runtime/FakeIO.NO_RESULT : Ljava/lang/Object;",
			"IF_ACMPEQ L0",
			"POP",
			"RETURN",
			"L0",
			"POP"));
	}

	@Test
	public void testCreateDirectIOFakeNoResultObjectArrayArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultObjectArrayArg");
		InsnList insnlist = fakeIOTransformer.createDirectIOFake(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"ALOAD 0",
			"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
			"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
			"NEW java/lang/Throwable",
			"DUP",
			"INVOKESPECIAL java/lang/Throwable.<init> ()V",
			"INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;",
			"ALOAD 0",
			"LDC \"noResultObjectArrayArg\"",
			"LDC \"([Ljava/lang/String;)V\"",
			"LDC 1",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"ALOAD 1",
			"AASTORE",
			"INVOKESTATIC net/amygdalum/testrecorder/runtime/FakeIO.callFake (Ljava/lang/String;[Ljava/lang/StackTraceElement;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
			"DUP",
			"GETSTATIC net/amygdalum/testrecorder/runtime/FakeIO.NO_RESULT : Ljava/lang/Object;",
			"IF_ACMPEQ L0",
			"POP",
			"RETURN",
			"L0",
			"POP"));
	}

	@Test
	public void testCreateDirectIOFakeNoResultObjectArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultObjectArg");
		InsnList insnlist = fakeIOTransformer.createDirectIOFake(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"ALOAD 0",
			"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
			"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
			"NEW java/lang/Throwable",
			"DUP",
			"INVOKESPECIAL java/lang/Throwable.<init> ()V",
			"INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;",
			"ALOAD 0",
			"LDC \"noResultObjectArg\"",
			"LDC \"(Lnet/amygdalum/testrecorder/ArgumentObject;)V\"",
			"LDC 1",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"ALOAD 1",
			"AASTORE",
			"INVOKESTATIC net/amygdalum/testrecorder/runtime/FakeIO.callFake (Ljava/lang/String;[Ljava/lang/StackTraceElement;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
			"DUP",
			"GETSTATIC net/amygdalum/testrecorder/runtime/FakeIO.NO_RESULT : Ljava/lang/Object;",
			"IF_ACMPEQ L0",
			"POP",
			"RETURN",
			"L0",
			"POP"));
	}

	@Test
	public void testCreateDirectIOFakeObjectResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultMixedArgs");
		InsnList insnlist = fakeIOTransformer.createDirectIOFake(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"ALOAD 0",
			"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
			"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
			"NEW java/lang/Throwable",
			"DUP",
			"INVOKESPECIAL java/lang/Throwable.<init> ()V",
			"INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;",
			"ALOAD 0",
			"LDC \"objectResultMixedArgs\"",
			"LDC \"(DLnet/amygdalum/testrecorder/ArgumentObject;)Lnet/amygdalum/testrecorder/ResultObject;\"",
			"LDC 2",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"DLOAD 1",
			"INVOKESTATIC java/lang/Double.valueOf (D)Ljava/lang/Double;",
			"AASTORE",
			"DUP",
			"LDC 1",
			"ALOAD 2",
			"AASTORE",
			"INVOKESTATIC net/amygdalum/testrecorder/runtime/FakeIO.callFake (Ljava/lang/String;[Ljava/lang/StackTraceElement;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
			"DUP",
			"GETSTATIC net/amygdalum/testrecorder/runtime/FakeIO.NO_RESULT : Ljava/lang/Object;",
			"IF_ACMPEQ L0",
			"CHECKCAST net/amygdalum/testrecorder/ResultObject",
			"ARETURN",
			"L0",
			"POP"));
	}

	@Test
	public void testCreateDirectIOFakeStaticPrimitiveResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "staticPrimitiveResultMixedArgs");
		InsnList insnlist = fakeIOTransformer.createDirectIOFake(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"LDC \"net.amygdalum.testrecorder.Example\"",
			"NEW java/lang/Throwable",
			"DUP",
			"INVOKESPECIAL java/lang/Throwable.<init> ()V",
			"INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;",
			"ACONST_NULL",
			"LDC \"staticPrimitiveResultMixedArgs\"",
			"LDC \"(Lnet/amygdalum/testrecorder/ArgumentObject;C)J\"",
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
			"INVOKESTATIC net/amygdalum/testrecorder/runtime/FakeIO.callFake (Ljava/lang/String;[Ljava/lang/StackTraceElement;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
			"DUP",
			"GETSTATIC net/amygdalum/testrecorder/runtime/FakeIO.NO_RESULT : Ljava/lang/Object;",
			"IF_ACMPEQ L0",
			"CHECKCAST java/lang/Long",
			"INVOKEVIRTUAL java/lang/Long.longValue ()J",
			"LRETURN",
			"L0",
			"POP"));
	}

	@Test
	public void testCreateBridgedIOFakePrimitiveResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "primitiveResultNoArgs");
		InsnList insnlist = fakeIOTransformer.createBridgedIOFake(unit.classNode, unit.methodNode);
		
		assertThat(ByteCode.toString(insnlist), contains(
			"ALOAD 0",
			"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
			"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
			"NEW java/lang/Throwable",
			"DUP",
			"INVOKESPECIAL java/lang/Throwable.<init> ()V",
			"INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;",
			"ALOAD 0",
			"LDC \"primitiveResultNoArgs\"",
			"LDC \"()Z\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKESTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.callFake (Ljava/lang/String;[Ljava/lang/StackTraceElement;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
			"DUP",
			"GETSTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.NO_RESULT : Ljava/lang/Object;",
			"IF_ACMPEQ L0",
			"CHECKCAST java/lang/Boolean",
			"INVOKEVIRTUAL java/lang/Boolean.booleanValue ()Z",
			"IRETURN",
			"L0",
			"POP"));
	}
	
	@Test
	public void testCreateBridgedIOFakeObjectResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultNoArgs");
		InsnList insnlist = fakeIOTransformer.createBridgedIOFake(unit.classNode, unit.methodNode);
		
		assertThat(ByteCode.toString(insnlist), contains(
			"ALOAD 0",
			"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
			"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
			"NEW java/lang/Throwable",
			"DUP",
			"INVOKESPECIAL java/lang/Throwable.<init> ()V",
			"INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;",
			"ALOAD 0",
			"LDC \"objectResultNoArgs\"",
			"LDC \"()Lnet/amygdalum/testrecorder/ResultObject;\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKESTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.callFake (Ljava/lang/String;[Ljava/lang/StackTraceElement;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
			"DUP",
			"GETSTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.NO_RESULT : Ljava/lang/Object;",
			"IF_ACMPEQ L0",
			"CHECKCAST net/amygdalum/testrecorder/ResultObject",
			"ARETURN",
			"L0",
			"POP"));
	}
	
	@Test
	public void testCreateBridgedIOFakeNoResultPrimitiveArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultPrimitiveArg");
		InsnList insnlist = fakeIOTransformer.createBridgedIOFake(unit.classNode, unit.methodNode);
		
		assertThat(ByteCode.toString(insnlist), contains(
			"ALOAD 0",
			"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
			"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
			"NEW java/lang/Throwable",
			"DUP",
			"INVOKESPECIAL java/lang/Throwable.<init> ()V",
			"INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;",
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
			"INVOKESTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.callFake (Ljava/lang/String;[Ljava/lang/StackTraceElement;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
			"DUP",
			"GETSTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.NO_RESULT : Ljava/lang/Object;",
			"IF_ACMPEQ L0",
			"POP",
			"RETURN",
			"L0",
			"POP"));
	}
	
	@Test
	public void testCreateBridgedIOFakeNoResultArrayArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultArrayArg");
		InsnList insnlist = fakeIOTransformer.createBridgedIOFake(unit.classNode, unit.methodNode);
		
		assertThat(ByteCode.toString(insnlist), contains(
			"ALOAD 0",
			"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
			"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
			"NEW java/lang/Throwable",
			"DUP",
			"INVOKESPECIAL java/lang/Throwable.<init> ()V",
			"INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;",
			"ALOAD 0",
			"LDC \"noResultArrayArg\"",
			"LDC \"([C)V\"",
			"LDC 1",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"ALOAD 1",
			"AASTORE",
			"INVOKESTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.callFake (Ljava/lang/String;[Ljava/lang/StackTraceElement;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
			"DUP",
			"GETSTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.NO_RESULT : Ljava/lang/Object;",
			"IF_ACMPEQ L0",
			"POP",
			"RETURN",
			"L0",
			"POP"));
	}
	
	@Test
	public void testCreateBridgedIOFakeNoResultObjectArrayArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultObjectArrayArg");
		InsnList insnlist = fakeIOTransformer.createBridgedIOFake(unit.classNode, unit.methodNode);
		
		assertThat(ByteCode.toString(insnlist), contains(
			"ALOAD 0",
			"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
			"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
			"NEW java/lang/Throwable",
			"DUP",
			"INVOKESPECIAL java/lang/Throwable.<init> ()V",
			"INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;",
			"ALOAD 0",
			"LDC \"noResultObjectArrayArg\"",
			"LDC \"([Ljava/lang/String;)V\"",
			"LDC 1",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"ALOAD 1",
			"AASTORE",
			"INVOKESTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.callFake (Ljava/lang/String;[Ljava/lang/StackTraceElement;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
			"DUP",
			"GETSTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.NO_RESULT : Ljava/lang/Object;",
			"IF_ACMPEQ L0",
			"POP",
			"RETURN",
			"L0",
			"POP"));
	}
	
	@Test
	public void testCreateBridgedIOFakeNoResultObjectArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultObjectArg");
		InsnList insnlist = fakeIOTransformer.createBridgedIOFake(unit.classNode, unit.methodNode);
		
		assertThat(ByteCode.toString(insnlist), contains(
			"ALOAD 0",
			"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
			"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
			"NEW java/lang/Throwable",
			"DUP",
			"INVOKESPECIAL java/lang/Throwable.<init> ()V",
			"INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;",
			"ALOAD 0",
			"LDC \"noResultObjectArg\"",
			"LDC \"(Lnet/amygdalum/testrecorder/ArgumentObject;)V\"",
			"LDC 1",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"ALOAD 1",
			"AASTORE",
			"INVOKESTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.callFake (Ljava/lang/String;[Ljava/lang/StackTraceElement;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
			"DUP",
			"GETSTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.NO_RESULT : Ljava/lang/Object;",
			"IF_ACMPEQ L0",
			"POP",
			"RETURN",
			"L0",
			"POP"));
	}
	
	@Test
	public void testCreateBridgedIOFakeObjectResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultMixedArgs");
		InsnList insnlist = fakeIOTransformer.createBridgedIOFake(unit.classNode, unit.methodNode);
		
		assertThat(ByteCode.toString(insnlist), contains(
			"ALOAD 0",
			"INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;",
			"INVOKEVIRTUAL java/lang/Class.getName ()Ljava/lang/String;",
			"NEW java/lang/Throwable",
			"DUP",
			"INVOKESPECIAL java/lang/Throwable.<init> ()V",
			"INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;",
			"ALOAD 0",
			"LDC \"objectResultMixedArgs\"",
			"LDC \"(DLnet/amygdalum/testrecorder/ArgumentObject;)Lnet/amygdalum/testrecorder/ResultObject;\"",
			"LDC 2",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"DLOAD 1",
			"INVOKESTATIC java/lang/Double.valueOf (D)Ljava/lang/Double;",
			"AASTORE",
			"DUP",
			"LDC 1",
			"ALOAD 2",
			"AASTORE",
			"INVOKESTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.callFake (Ljava/lang/String;[Ljava/lang/StackTraceElement;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
			"DUP",
			"GETSTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.NO_RESULT : Ljava/lang/Object;",
			"IF_ACMPEQ L0",
			"CHECKCAST net/amygdalum/testrecorder/ResultObject",
			"ARETURN",
			"L0",
			"POP"));
	}

	
	@Test
	public void testCreateBridgedIOFakeStaticPrimitiveResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "staticPrimitiveResultMixedArgs");
		InsnList insnlist = fakeIOTransformer.createBridgedIOFake(unit.classNode, unit.methodNode);
		
		assertThat(ByteCode.toString(insnlist), contains(
			"LDC \"net.amygdalum.testrecorder.Example\"",
			"NEW java/lang/Throwable",
			"DUP",
			"INVOKESPECIAL java/lang/Throwable.<init> ()V",
			"INVOKEVIRTUAL java/lang/Throwable.getStackTrace ()[Ljava/lang/StackTraceElement;",
			"ACONST_NULL",
			"LDC \"staticPrimitiveResultMixedArgs\"",
			"LDC \"(Lnet/amygdalum/testrecorder/ArgumentObject;C)J\"",
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
			"INVOKESTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.callFake (Ljava/lang/String;[Ljava/lang/StackTraceElement;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
			"DUP",
			"GETSTATIC net/amygdalum/testrecorder/bridge/BridgedFakeIO.NO_RESULT : Ljava/lang/Object;",
			"IF_ACMPEQ L0",
			"CHECKCAST java/lang/Long",
			"INVOKEVIRTUAL java/lang/Long.longValue ()J",
			"LRETURN",
			"L0",
			"POP"));
	}
	
}
