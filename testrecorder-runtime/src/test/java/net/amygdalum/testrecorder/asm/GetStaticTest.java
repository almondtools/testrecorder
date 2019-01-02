package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.InsnList;

import net.amygdalum.testrecorder.util.testobjects.Static;

public class GetStaticTest {

	private MethodContext context;

	@BeforeEach
	void before() {
		context = new MethodContext(AClass.classNode(), AClass.staticMethodNode());
	}

	@Test
	void testGetStatic() throws Exception {
		InsnList insns = new GetStatic(Static.class, "CONSTANT")
			.build(context);

		assertThat(ByteCode.toString(insns))
			.containsExactly(
				"GETSTATIC " + Static.class.getName().replace('.', '/') + ".CONSTANT : Ljava/lang/String;");
	}

}
