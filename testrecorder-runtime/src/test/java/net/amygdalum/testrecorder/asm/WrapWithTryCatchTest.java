package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class WrapWithTryCatchTest {

	private MethodNode node;
	private MethodContext context;

	@BeforeEach
	void before() {
		node = AClass.virtualMethodNode();
		context = new MethodContext(AClass.classNode(), node);
	}

	@Nested
	class Scenarios {
		@Test
		void wrappingWithTryCatch() throws Exception {
			node.instructions.add(new InsnNode(Opcodes.FCONST_0));
			node.instructions.add(new InsnNode(Opcodes.POP));
			InsnList insns = new WrapWithTryCatch()
				.before(Sequence.start().then(new InsnNode(Opcodes.ICONST_1)).then(new InsnNode(Opcodes.POP)))
				.after(Sequence.start().then(new InsnNode(Opcodes.ICONST_2)).then(new InsnNode(Opcodes.POP)))
				.handler(Sequence.start().then(new InsnNode(Opcodes.ICONST_3)).then(new InsnNode(Opcodes.POP)))
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"L0",
					// try
					// before
					"ICONST_1",
					"POP",
					// wrapped
					"FCONST_0",
					"POP",
					"L1",
					// after
					"ICONST_2",
					"POP",
					"RETURN",
					"L2",
					// catch
					"ICONST_3",
					"POP",
					"ATHROW");
			assertThat(node.tryCatchBlocks)
				.hasSize(1);
		}

		@Test
		void wrappingWithTryCatchWithReturns() throws Exception {
			node.instructions.add(new InsnNode(Opcodes.FCONST_0));
			node.instructions.add(new InsnNode(Opcodes.POP));
			node.instructions.add(new InsnNode(Opcodes.RETURN));
			node.instructions.add(new InsnNode(Opcodes.FCONST_1));
			node.instructions.add(new InsnNode(Opcodes.POP));
			node.instructions.add(new InsnNode(Opcodes.RETURN));
			node.instructions.add(new InsnNode(Opcodes.FCONST_2));
			node.instructions.add(new InsnNode(Opcodes.POP));
			InsnList insns = new WrapWithTryCatch()
				.before(Sequence.start()
					.then(new InsnNode(Opcodes.ICONST_1))
					.then(new InsnNode(Opcodes.POP)))
				.after(Sequence.start().then(new InsnNode(Opcodes.ICONST_2)).then(new InsnNode(Opcodes.POP)))
				.handler(Sequence.start().then(new InsnNode(Opcodes.ICONST_3)).then(new InsnNode(Opcodes.POP)))
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					"L0",
					// try
					// before
					"ICONST_1",
					"POP",
					// wrapped
					"FCONST_0",
					"POP",
					"GOTO L1",
					"FCONST_1",
					"POP",
					"GOTO L1",
					"FCONST_2",
					"POP",
					"L1",
					// after
					"ICONST_2",
					"POP",
					"RETURN",
					"L2",
					// catch
					"ICONST_3",
					"POP",
					"ATHROW");
			assertThat(node.tryCatchBlocks)
				.hasSize(1);
		}
	}
}
