package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class WrapMethodTest {

	private MethodNode node;
	private MethodContext context;

	@BeforeEach
	void before() {
		node = AClass.virtualMethodNode();
		context = new MethodContext(AClass.classNode(), node);
	}

	@Test
	void testWrapMethod() throws Exception {
		node.instructions.add(new InsnNode(Opcodes.FCONST_0));
		node.instructions.add(new InsnNode(Opcodes.POP));
		InsnList insns = new WrapMethod()
			.prepend(Sequence.start().then(new InsnNode(Opcodes.ICONST_1)).then(new InsnNode(Opcodes.POP)))
			.append(Sequence.start().then(new InsnNode(Opcodes.ICONST_2)).then(new InsnNode(Opcodes.POP)))
			.build(context);

		assertThat(ByteCode.toString(insns))
			.containsExactly(
				// before
				"ICONST_1",
				"POP",
				// wrapped
				"FCONST_0",
				"POP",
				"L0",
				// after
				"ICONST_2",
				"POP",
				"RETURN");
	}

	@Test
	void testMethodWithReturns() throws Exception {
		node.instructions.add(new InsnNode(Opcodes.FCONST_0));
		node.instructions.add(new InsnNode(Opcodes.POP));
		node.instructions.add(new InsnNode(Opcodes.RETURN));
		node.instructions.add(new InsnNode(Opcodes.FCONST_1));
		node.instructions.add(new InsnNode(Opcodes.POP));
		node.instructions.add(new InsnNode(Opcodes.RETURN));
		node.instructions.add(new InsnNode(Opcodes.FCONST_2));
		node.instructions.add(new InsnNode(Opcodes.POP));
		InsnList insns = new WrapMethod()
			.prepend(Sequence.start().then(new InsnNode(Opcodes.ICONST_1)).then(new InsnNode(Opcodes.POP)))
			.append(Sequence.start().then(new InsnNode(Opcodes.ICONST_2)).then(new InsnNode(Opcodes.POP)))
			.build(context);

		assertThat(ByteCode.toString(insns))
			.containsExactly(
				// before
				"ICONST_1",
			    "POP",
			    // wrapped
			    "FCONST_0",
			    "POP",
			    "GOTO L0",
			    "FCONST_1",
			    "POP",
			    "GOTO L0",
			    "FCONST_2",
			    "POP",
			    "L0",
				// after
			    "ICONST_2",
			    "POP",
			    "RETURN");
	}

}
