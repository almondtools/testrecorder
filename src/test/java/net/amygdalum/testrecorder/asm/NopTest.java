package net.amygdalum.testrecorder.asm;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class NopTest {

	@Test
	public void testBuild() throws Exception {
		Nop nop = Nop.NOP;
		
		assertThat(ByteCode.toString(nop.build(methodContext())), empty());
	}

	private MethodContext methodContext() {
		return new MethodContext(new ClassNode(), new MethodNode());
	}

}
