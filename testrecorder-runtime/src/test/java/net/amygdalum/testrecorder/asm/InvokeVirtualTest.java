package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.PrintWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.InsnList;

public class InvokeVirtualTest {

	private MethodContext context;

	@BeforeEach
	void before() {
		context = new MethodContext(AClass.classNode(), AClass.virtualMethodNode());
	}

	@Test
	void testInvokeVirtual() throws Exception {
		InsnList insns = new InvokeVirtual(PrintWriter.class, "write", String.class, int.class, int.class)
			.withBase(new This())
			.withArgument(0, new Ldc("str"))
			.withArgument(1, new Ldc(0))
			.withArgument(2, new Ldc(1))
			.build(context);

		assertThat(ByteCode.toString(insns))
			.containsExactly(
				"ALOAD 0",
				"LDC \"str\"",
				"LDC 0",
				"LDC 1",
				"INVOKEVIRTUAL java/io/PrintWriter.write (Ljava/lang/String;II)V");
	}

}
