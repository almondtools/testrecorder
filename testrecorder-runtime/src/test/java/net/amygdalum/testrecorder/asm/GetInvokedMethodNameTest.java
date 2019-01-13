package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import java.io.PrintWriter;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

public class GetInvokedMethodNameTest {

	private MethodContext context;

	@BeforeEach
	void before() {
		context = new MethodContext(AClass.classNode(), AClass.staticMethodNode());
	}

	@Test
	void testGetInvokedMethodName() throws Exception {
		Class<PrintWriter> clazz = PrintWriter.class;
		Method method = clazz.getMethod("write", String.class, int.class, int.class);
		MethodInsnNode call = new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(clazz), "write", Type.getMethodDescriptor(method), false);
		InsnList insns = new GetInvokedMethodName(call)
			.build(context);

		assertThat(ByteCode.toString(insns))
			.containsExactly(
				"LDC \"write\"");
	}
}
