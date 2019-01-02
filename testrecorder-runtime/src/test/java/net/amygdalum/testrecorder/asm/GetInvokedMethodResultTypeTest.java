package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

import net.amygdalum.testrecorder.util.testobjects.Simple;

public class GetInvokedMethodResultTypeTest {
	private MethodContext context;

	@BeforeEach
	void before() {
		context = new MethodContext(AClass.classNode(), AClass.staticMethodNode());
	}

	@Test
	void testGetInvokedMethodResultType() throws Exception {
		Class<Simple> clazz = Simple.class;
		Method method = clazz.getMethod("getStr");
		MethodInsnNode call = new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(clazz), "getStr", Type.getMethodDescriptor(method), false);
		InsnList insns = new GetInvokedMethodResultType(call)
			.build(context);

		assertThat(ByteCode.toString(insns))
			.containsExactly(
				"LDC Ljava/lang/String;.class");
	}
	

}
