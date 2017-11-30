package net.amygdalum.testrecorder;

import static net.amygdalum.testrecorder.InstrumentationUnit.instrument;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.tree.InsnList;

import net.amygdalum.testrecorder.util.ByteCode;

public class SnapshotInstrumentorTest {

	private SnapshotInstrumentor snapshotInstrumentor;

	@Before
	public void before() throws Exception {
		snapshotInstrumentor = new SnapshotInstrumentor(new DefaultTestRecorderAgentConfig());
	}

	@Test
	public void testSetupVariablesWithNoResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultNoArgs");
		InsnList insnlist = snapshotInstrumentor.setupVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:noResultNoArgs()V\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.setupVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testSetupVariablesWithPrimitiveResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "primitiveResultNoArgs");
		InsnList insnlist = snapshotInstrumentor.setupVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:primitiveResultNoArgs()Z\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.setupVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testSetupVariablesWithObjectResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultNoArgs");
		InsnList insnlist = snapshotInstrumentor.setupVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:objectResultNoArgs()Lnet/amygdalum/testrecorder/ResultObject;\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.setupVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testSetupVariablesWithNoResultPrimitiveArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultPrimitiveArg");
		InsnList insnlist = snapshotInstrumentor.setupVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
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
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.setupVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testSetupVariablesWithNoResultObjectArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultObjectArg");
		InsnList insnlist = snapshotInstrumentor.setupVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:noResultObjectArg(Lnet/amygdalum/testrecorder/ArgumentObject;)V\"",
			"LDC 1",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"ALOAD 1",
			"AASTORE",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.setupVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testSetupVariablesWithObjectResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultMixedArgs");
		InsnList insnlist = snapshotInstrumentor.setupVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
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
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.setupVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testSetupVariablesWithStaticPrimitiveResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "staticPrimitiveResultMixedArgs");
		InsnList insnlist = snapshotInstrumentor.setupVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
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
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.setupVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testExpectVariablesWithNoResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultNoArgs");
		InsnList insnlist = snapshotInstrumentor.expectVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:noResultNoArgs()V\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.expectVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testExpectVariablesWithPrimitiveResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "primitiveResultNoArgs");
		InsnList insnlist = snapshotInstrumentor.expectVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"DUP",
			"INVOKESTATIC java/lang/Boolean.valueOf (Z)Ljava/lang/Boolean;",
			"ASTORE 1",
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:primitiveResultNoArgs()Z\"",
			"ALOAD 1",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.expectVariables (Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testExpectVariablesWithObjectResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultNoArgs");
		InsnList insnlist = snapshotInstrumentor.expectVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"DUP",
			"ASTORE 1",
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:objectResultNoArgs()Lnet/amygdalum/testrecorder/ResultObject;\"",
			"ALOAD 1",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.expectVariables (Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testExpectVariablesWithNoResultPrimitiveArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultPrimitiveArg");
		InsnList insnlist = snapshotInstrumentor.expectVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
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
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.expectVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testExpectVariablesWithNoResultObjectArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultObjectArg");
		InsnList insnlist = snapshotInstrumentor.expectVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:noResultObjectArg(Lnet/amygdalum/testrecorder/ArgumentObject;)V\"",
			"LDC 1",
			"ANEWARRAY java/lang/Object",
			"DUP",
			"LDC 0",
			"ALOAD 1",
			"AASTORE",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.expectVariables (Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testExpectVariablesWithObjectResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultMixedArgs");
		InsnList insnlist = snapshotInstrumentor.expectVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
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
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.expectVariables (Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testExpectVariablesWithStaticPrimitiveResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "staticPrimitiveResultMixedArgs");
		InsnList insnlist = snapshotInstrumentor.expectVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
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
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.expectVariables (Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testThrowVariablesWithNoResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultNoArgs");
		InsnList insnlist = snapshotInstrumentor.throwVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"DUP",
			"ASTORE 1",
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 1",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:noResultNoArgs()V\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.throwVariables (Ljava/lang/Throwable;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testThrowVariablesWithPrimitiveResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "primitiveResultNoArgs");
		InsnList insnlist = snapshotInstrumentor.throwVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"DUP",
			"ASTORE 1",
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 1",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:primitiveResultNoArgs()Z\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.throwVariables (Ljava/lang/Throwable;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testThrowVariablesWithObjectResultNoArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultNoArgs");
		InsnList insnlist = snapshotInstrumentor.throwVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
			"DUP",
			"ASTORE 1",
			"GETSTATIC net/amygdalum/testrecorder/SnapshotManager.MANAGER : Lnet/amygdalum/testrecorder/SnapshotManager;",
			"ALOAD 1",
			"ALOAD 0",
			"LDC \"net/amygdalum/testrecorder/Example:objectResultNoArgs()Lnet/amygdalum/testrecorder/ResultObject;\"",
			"LDC 0",
			"ANEWARRAY java/lang/Object",
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.throwVariables (Ljava/lang/Throwable;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testThrowVariablesWithNoResultPrimitiveArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultPrimitiveArg");
		InsnList insnlist = snapshotInstrumentor.throwVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
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
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.throwVariables (Ljava/lang/Throwable;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testThrowVariablesWithNoResultObjectArg() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "noResultObjectArg");
		InsnList insnlist = snapshotInstrumentor.throwVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
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
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.throwVariables (Ljava/lang/Throwable;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testThrowVariablesWithObjectResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "objectResultMixedArgs");
		InsnList insnlist = snapshotInstrumentor.throwVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
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
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.throwVariables (Ljava/lang/Throwable;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}

	@Test
	public void testThrowVariablesWithStaticPrimitiveResultMixedArgs() throws Exception {
		InstrumentationUnit unit = instrument(Example.class, "staticPrimitiveResultMixedArgs");
		InsnList insnlist = snapshotInstrumentor.throwVariables(unit.classNode, unit.methodNode);

		assertThat(ByteCode.toString(insnlist), contains(
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
			"INVOKEVIRTUAL net/amygdalum/testrecorder/SnapshotManager.throwVariables (Ljava/lang/Throwable;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;)V"));
	}
}