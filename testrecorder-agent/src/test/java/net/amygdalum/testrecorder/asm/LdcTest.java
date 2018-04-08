package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.amygdalum.testrecorder.util.testobjects.Simple;

public class LdcTest {

	@Test
	public void testLdcClass() throws Exception {
		Ldc ldc = new Ldc(Simple.class);
		assertThat(ByteCode.toString(ldc.build(methodContext()))).containsExactly("LDC class " + Simple.class.getName());
	}

	@Test
	public void testLdcCharacter() throws Exception {
		Ldc ldc = new Ldc('x');
		
		assertThat(ByteCode.toString(ldc.build(methodContext()))).containsExactly("LDC x");
	}

	@Test
	public void testLdcByte() throws Exception {
		Ldc ldc = new Ldc((byte) 0);
		
		assertThat(ByteCode.toString(ldc.build(methodContext()))).containsExactly("LDC 0");
	}

	@Test
	public void testLdcString() throws Exception {
		Ldc ldc = new Ldc("Hello World");

		assertThat(ByteCode.toString(ldc.build(methodContext()))).containsExactly("LDC \"Hello World\"");
	}

	private MethodContext methodContext() {
		return new MethodContext(new ClassNode(), new MethodNode());
	}

}
