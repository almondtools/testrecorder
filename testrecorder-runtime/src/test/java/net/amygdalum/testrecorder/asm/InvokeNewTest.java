package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.InsnList;

import net.amygdalum.testrecorder.util.testobjects.Simple;

public class InvokeNewTest {

	private MethodContext context;

	@BeforeEach
	void before() {
		context = new MethodContext(AClass.classNode(), AClass.virtualMethodNode());
	}

	@Test
	void testInvokeNew() throws Exception {
		InvokeNew invokeNew = new InvokeNew(Simple.class, String.class)
			.withArgument(0, new GetThisOrNull());

		InsnList insns = invokeNew.build(context);
		
		assertThat(ByteCode.toString(insns))
			.containsExactly(
				"NEW " + Simple.class.getName().replace(".", "/"),
				"DUP",
				"ALOAD 0",
				"INVOKESPECIAL " + Simple.class.getName().replace(".", "/") + ".<init> (Ljava/lang/String;)V");
	}

}
