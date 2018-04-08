package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.amygdalum.testrecorder.util.testobjects.Simple;

public class InvokeNewTest {

	@Test
	public void testInvokeNew() throws Exception {
		InvokeNew invokeNew = new InvokeNew(Simple.class, String.class)
			.withArgument(0, new GetThisOrNull());

		String simpleType = Simple.class.getName().replace(".", "/");
		assertThat(ByteCode.toString(invokeNew.build(methodContext()))).containsExactly(
			"NEW " + simpleType,
			"DUP",
			"ALOAD 0",
			"INVOKESPECIAL " + simpleType + ".<init> (Ljava/lang/String;)V");
	}

	private MethodContext methodContext() {
		return new MethodContext(new ClassNode(), new MethodNode());
	}

}
