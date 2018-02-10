package net.amygdalum.testrecorder;

import static net.amygdalum.testrecorder.InstrumentationUnit.instrument;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.objectweb.asm.Opcodes.POP;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.amygdalum.testrecorder.asm.ByteCode;
import net.amygdalum.testrecorder.asm.MethodContext;
import net.amygdalum.testrecorder.asm.Sequence;

public class SnapshotInstrumentorTest {

	private DefaultTestRecorderAgentConfig config;
	private ClassNodeManager classes;
	private IOManager io;

	@BeforeEach
	public void before() throws Exception {
		config = new DefaultTestRecorderAgentConfig();
		classes = new ClassNodeManager();
		io = new IOManager();
	}

	@Test
	public void testSetupVariablesWithNoResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultNoArgs");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.setupVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:noResultNoArgs()V\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.setupVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testSetupVariablesWithPrimitiveResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "primitiveResultNoArgs");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.setupVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:primitiveResultNoArgs()Z\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.setupVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testSetupVariablesWithObjectResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultNoArgs");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.setupVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:objectResultNoArgs()Lnet/amygdalum/testrecorder/ResultObject;\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.setupVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testSetupVariablesWithNoResultPrimitiveArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultPrimitiveArg");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.setupVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:noResultPrimitiveArg(I)V\"",
			"LDC 1",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"ILOAD 1",
			"INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;",
			"AASTORE",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.setupVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testSetupVariablesWithNoResultObjectArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultObjectArg");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.setupVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:noResultObjectArg(Lnet/amygdalum/testrecorder/ArgumentObject;)V\"",
			"LDC 1",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"ALOAD 1",
			"AASTORE",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.setupVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testSetupVariablesWithObjectResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultMixedArgs");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.setupVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:objectResultMixedArgs(DLnet/amygdalum/testrecorder/ArgumentObject;)Lnet/amygdalum/testrecorder/ResultObject;\"",
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
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.setupVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testSetupVariablesWithStaticPrimitiveResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "staticPrimitiveResultMixedArgs");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.setupVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ACONST_NULL",
			"LDC \"net/amygdalum/testrecorder/Example:staticPrimitiveResultMixedArgs(Lnet/amygdalum/testrecorder/ArgumentObject;C)J\"",
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
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.setupVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testExpectVariablesWithNoResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultNoArgs");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.expectVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:noResultNoArgs()V\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.expectVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testExpectVariablesWithPrimitiveResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "primitiveResultNoArgs");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.expectVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"DUP",
			"INVOKESTATIC java/lang/Boolean.valueOf (Z)Ljava/lang/Boolean;",
			"ASTORE 1",
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:primitiveResultNoArgs()Z\"",
			"ALOAD 1",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.expectVariables (Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)V");
	}

	@Test
	public void testExpectVariablesWithObjectResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultNoArgs");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.expectVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"DUP",
			"ASTORE 1",
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:objectResultNoArgs()Lnet/amygdalum/testrecorder/ResultObject;\"",
			"ALOAD 1",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.expectVariables (Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)V");
	}

	@Test
	public void testExpectVariablesWithNoResultPrimitiveArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultPrimitiveArg");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.expectVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:noResultPrimitiveArg(I)V\"",
			"LDC 1",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"ILOAD 1",
			"INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;",
			"AASTORE",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.expectVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testExpectVariablesWithNoResultObjectArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultObjectArg");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.expectVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:noResultObjectArg(Lnet/amygdalum/testrecorder/ArgumentObject;)V\"",
			"LDC 1",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"ALOAD 1",
			"AASTORE",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.expectVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testExpectVariablesWithObjectResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultMixedArgs");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.expectVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"DUP",
			"ASTORE 4",
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:objectResultMixedArgs(DLnet/amygdalum/testrecorder/ArgumentObject;)Lnet/amygdalum/testrecorder/ResultObject;\"",
			"ALOAD 4",
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
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.expectVariables (Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)V");
	}

	@Test
	public void testExpectVariablesWithStaticPrimitiveResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "staticPrimitiveResultMixedArgs");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.expectVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"DUP2",
			"INVOKESTATIC java/lang/Long.valueOf (J)Ljava/lang/Long;",
			"ASTORE 2",
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ACONST_NULL",
			"LDC \"net/amygdalum/testrecorder/Example:staticPrimitiveResultMixedArgs(Lnet/amygdalum/testrecorder/ArgumentObject;C)J\"",
			"ALOAD 2",
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
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.expectVariables (Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)V");
	}

	@Test
	public void testThrowVariablesWithNoResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultNoArgs");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.throwVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"DUP",
			"ASTORE 1",
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 1",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:noResultNoArgs()V\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.throwVariables (Ljava/lang/Throwable;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testThrowVariablesWithPrimitiveResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "primitiveResultNoArgs");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.throwVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"DUP",
			"ASTORE 1",
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 1",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:primitiveResultNoArgs()Z\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.throwVariables (Ljava/lang/Throwable;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testThrowVariablesWithObjectResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultNoArgs");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.throwVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"DUP",
			"ASTORE 1",
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 1",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:objectResultNoArgs()Lnet/amygdalum/testrecorder/ResultObject;\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.throwVariables (Ljava/lang/Throwable;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testThrowVariablesWithNoResultPrimitiveArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultPrimitiveArg");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.throwVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"DUP",
			"ASTORE 2",
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 2",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:noResultPrimitiveArg(I)V\"",
			"LDC 1",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"ILOAD 1",
			"INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;",
			"AASTORE",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.throwVariables (Ljava/lang/Throwable;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testThrowVariablesWithNoResultObjectArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultObjectArg");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.throwVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"DUP",
			"ASTORE 2",
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 2",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:noResultObjectArg(Lnet/amygdalum/testrecorder/ArgumentObject;)V\"",
			"LDC 1",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"ALOAD 1",
			"AASTORE",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.throwVariables (Ljava/lang/Throwable;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testThrowVariablesWithObjectResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultMixedArgs");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.throwVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"DUP",
			"ASTORE 4",
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 4",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:objectResultMixedArgs(DLnet/amygdalum/testrecorder/ArgumentObject;)Lnet/amygdalum/testrecorder/ResultObject;\"",
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
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.throwVariables (Ljava/lang/Throwable;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testThrowVariablesWithStaticPrimitiveResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "staticPrimitiveResultMixedArgs");
		InsnList insnlist = new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode)
			.throwVariables(unit.methodNode)
			.build(new MethodContext(unit.classNode, unit.methodNode));

		assertThat(ByteCode.toString(insnlist)).containsExactly(
			"DUP",
			"ASTORE 2",
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 2",
			"ACONST_NULL",
			"LDC \"net/amygdalum/testrecorder/Example:staticPrimitiveResultMixedArgs(Lnet/amygdalum/testrecorder/ArgumentObject;C)J\"",
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
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.throwVariables (Ljava/lang/Throwable;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V");
	}

	@Test
	public void testInstrumentSnapshotMethodWithNoResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultNoArgs");

		stubbedSnapshotInstrumentor(new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode))
			.instrumentSnapshotMethod(unit.methodNode);

		assertThat(ByteCode.toString(unit.methodNode.instructions)).containsExactly(
			"L0",
			"LDC \"setupVariables\"",
			"POP",
			"L1",
			"LINENUMBER 5 L1",
			"GOTO L2",
			"L3",
			"L2",
			"LDC \"expectVariables\"",
			"POP",
			"RETURN",
			"L4",
			"LDC \"throwVariables\"",
			"POP",
			"ATHROW");
	}

	@Test
	public void testInstrumentSnapshotMethodWithPrimitiveResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "primitiveResultNoArgs");

		stubbedSnapshotInstrumentor(new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode))
			.instrumentSnapshotMethod(unit.methodNode);

		assertThat(ByteCode.toString(unit.methodNode.instructions)).containsExactly(
			"L0",
			"LDC \"setupVariables\"",
			"POP",
			"L1",
			"LINENUMBER 8 L1",
			"ICONST_1",
			"GOTO L2",
			"L3",
			"L2",
			"LDC \"expectVariables\"",
			"POP",
			"IRETURN",
			"L4",
			"LDC \"throwVariables\"",
			"POP",
			"ATHROW");
	}

	@Test
	public void testInstrumentSnapshotMethodWithObjectResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultNoArgs");

		stubbedSnapshotInstrumentor(new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode))
			.instrumentSnapshotMethod(unit.methodNode);

		assertThat(ByteCode.toString(unit.methodNode.instructions)).containsExactly(
			"L0",
			"LDC \"setupVariables\"",
			"POP",
			"L1",
			"LINENUMBER 12 L1",
			"ACONST_NULL",
			"GOTO L2",
			"L3",
			"L2",
			"LDC \"expectVariables\"",
			"POP",
			"ARETURN",
			"L4",
			"LDC \"throwVariables\"",
			"POP",
			"ATHROW");
	}

	@Test
	public void testInstrumentSnapshotMethodWithNoResultPrimitiveArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultPrimitiveArg");

		stubbedSnapshotInstrumentor(new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode))
			.instrumentSnapshotMethod(unit.methodNode);

		assertThat(ByteCode.toString(unit.methodNode.instructions)).containsExactly(
			"L0",
			"LDC \"setupVariables\"",
			"POP",
			"L1",
			"LINENUMBER 16 L1",
			"GOTO L2",
			"L3",
			"L2",
			"LDC \"expectVariables\"",
			"POP",
			"RETURN",
			"L4",
			"LDC \"throwVariables\"",
			"POP",
			"ATHROW");
	}

	@Test
	public void testInstrumentSnapshotMethodWithNoResultObjectArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultObjectArg");

		stubbedSnapshotInstrumentor(new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode))
			.instrumentSnapshotMethod(unit.methodNode);

		assertThat(ByteCode.toString(unit.methodNode.instructions)).containsExactly(
			"L0",
			"LDC \"setupVariables\"",
			"POP",
			"L1",
			"LINENUMBER 25 L1",
			"GOTO L2",
			"L3",
			"L2",
			"LDC \"expectVariables\"",
			"POP",
			"RETURN",
			"L4",
			"LDC \"throwVariables\"",
			"POP",
			"ATHROW");
	}

	@Test
	public void testInstrumentSnapshotMethodWithObjectResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultMixedArgs");

		stubbedSnapshotInstrumentor(new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode))
			.instrumentSnapshotMethod(unit.methodNode);

		assertThat(ByteCode.toString(unit.methodNode.instructions)).containsExactly(
			"L0",
			"LDC \"setupVariables\"",
			"POP",
			"L1",
			"LINENUMBER 28 L1",
			"ACONST_NULL",
			"GOTO L2",
			"L3",
			"L2",
			"LDC \"expectVariables\"",
			"POP",
			"ARETURN",
			"L4",
			"LDC \"throwVariables\"",
			"POP",
			"ATHROW");
	}

	@Test
	public void testInstrumentSnapshotMethodWithStaticPrimitiveResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "staticPrimitiveResultMixedArgs");

		stubbedSnapshotInstrumentor(new SnapshotInstrumentor.DefaultTask(config, classes, io, unit.classNode))
			.instrumentSnapshotMethod(unit.methodNode);

		assertThat(ByteCode.toString(unit.methodNode.instructions)).containsExactly(
			"L0",
			"LDC \"setupVariables\"",
			"POP",
			"L1",
			"LINENUMBER 32 L1",
			"LCONST_1",
			"GOTO L2",
			"L3",
			"L2",
			"LDC \"expectVariables\"",
			"POP",
			"LRETURN",
			"L4",
			"LDC \"throwVariables\"",
			"POP",
			"ATHROW");
	}

	private SnapshotInstrumentor.Task stubbedSnapshotInstrumentor(SnapshotInstrumentor.Task task) {
		SnapshotInstrumentor.Task spy = Mockito.spy(task);
		doReturn(Sequence.start().then(new LdcInsnNode("setupVariables")).then(new InsnNode(POP)))
			.when(spy).setupVariables(Mockito.any(MethodNode.class));
		doReturn(Sequence.start().then(new LdcInsnNode("expectVariables")).then(new InsnNode(POP)))
			.when(spy).expectVariables(Mockito.any(MethodNode.class));
		doReturn(Sequence.start().then(new LdcInsnNode("throwVariables")).then(new InsnNode(POP)))
			.when(spy).throwVariables(Mockito.any(MethodNode.class));
		return spy;
	}

}