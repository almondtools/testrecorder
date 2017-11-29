package net.amygdalum.testrecorder;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

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
			"LDC \"net/amygdalum/testrecorder/SnapshotInstrumentorTest$Example:noResultNoArgs()V\"",
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
			"LDC \"net/amygdalum/testrecorder/SnapshotInstrumentorTest$Example:primitiveResultNoArgs()Z\"",
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
			"LDC \"net/amygdalum/testrecorder/SnapshotInstrumentorTest$Example:objectResultNoArgs()Lnet/amygdalum/testrecorder/SnapshotInstrumentorTest$ResultObject;\"",
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
			"LDC \"net/amygdalum/testrecorder/SnapshotInstrumentorTest$Example:noResultPrimitiveArg(I)V\"",
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
			"LDC \"net/amygdalum/testrecorder/SnapshotInstrumentorTest$Example:noResultObjectArg(Lnet/amygdalum/testrecorder/SnapshotInstrumentorTest$ArgumentObject;)V\"",
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
			"LDC \"net/amygdalum/testrecorder/SnapshotInstrumentorTest$Example:objectResultMixedArgs(DLnet/amygdalum/testrecorder/SnapshotInstrumentorTest$ArgumentObject;)Lnet/amygdalum/testrecorder/SnapshotInstrumentorTest$ResultObject;\"",
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
			"LDC \"net/amygdalum/testrecorder/SnapshotInstrumentorTest$Example:staticPrimitiveResultMixedArgs(Lnet/amygdalum/testrecorder/SnapshotInstrumentorTest$ArgumentObject;C)J\"",
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
	
	private InstrumentationUnit instrument(Class<?> clazz, String methodName) throws IOException, NoSuchMethodException {
		Method method = Arrays.stream(clazz.getDeclaredMethods())
			.filter(m -> m.getName().equals(methodName))
			.findFirst()
			.orElse(null);
		String className = Type.getInternalName(clazz);
		String methodDesc = Type.getMethodDescriptor(method);
		ClassNode classNode = snapshotInstrumentor.fetchClassNode(className);
		MethodNode methodNode = snapshotInstrumentor.fetchMethodNode(className, methodName, methodDesc);

		return new InstrumentationUnit(classNode, methodNode);
	}

	private static class InstrumentationUnit {
		public ClassNode classNode;
		public MethodNode methodNode;

		public InstrumentationUnit(ClassNode classNode, MethodNode methodNode) {
			this.classNode = classNode;
			this.methodNode = methodNode;
		}

	}

	public static class Example {
		public void noResultNoArgs() {
		}

		public boolean primitiveResultNoArgs() {
			return true;
		}

		public ResultObject objectResultNoArgs() {
			return null;
		}

		public void noResultPrimitiveArg(int i) {
		}

		public void noResultObjectArg(ArgumentObject o) {
		}

		public ResultObject objectResultMixedArgs(double d, ArgumentObject o) {
			return null;
		}

		public static long staticPrimitiveResultMixedArgs(ArgumentObject o, char c) {
			return 1l;
		}

	}
	
	public class ResultObject {
		
	}

	public class ArgumentObject {
		
	}
}