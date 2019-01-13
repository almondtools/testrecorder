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

public class GetInvokedMethodArgumentTypesTest {

	private MethodContext context;

	@BeforeEach
	void before() {
		context = new MethodContext(AClass.classNode(), AClass.staticMethodNode());
	}

	@Test
	void testGetInvokedMethodArgumentTypes() throws Exception {
		Class<PrintWriter> clazz = PrintWriter.class;
		Method method = clazz.getMethod("write", String.class, int.class, int.class);
		MethodInsnNode call = new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(clazz), "write", Type.getMethodDescriptor(method), false);
		InsnList insns = new GetInvokedMethodArgumentTypes(call)
			.build(context);

		assertThat(ByteCode.toString(insns))
			.containsExactly(
				"LDC 3",
			    "ANEWARRAY java/lang/reflect/Type",
			    "DUP",
			    "LDC 0",
			    "LDC Ljava/lang/String;.class",
			    "AASTORE",
			    "DUP",
			    "LDC 1",
			    "GETSTATIC java/lang/Integer.TYPE : Ljava/lang/Class;",
			    "AASTORE",
			    "DUP",
			    "LDC 2",
			    "GETSTATIC java/lang/Integer.TYPE : Ljava/lang/Class;",
			    "AASTORE");
	}
	

}
