package net.amygdalum.testrecorder.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.POP;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;

import net.amygdalum.testrecorder.asm.ByteCode;

@ExtendWith(LoggerExtension.class)
public class PrintDebugging {

	@Test
	public void testDebugPrint(@LogLevel("info") ByteArrayOutputStream info) throws Exception {
		String result = Debug.print("text");

		assertThat(result).isEqualTo("text");
		assertThat(info.toString()).contains("text");
	}

	@Test
	public void testByteCodePrint(@LogLevel("info") ByteArrayOutputStream info) throws Exception {
		InsnNode insn = new InsnNode(POP);

		InsnNode result = ByteCode.print(insn);

		assertThat(result).isEqualTo(insn);
		assertThat(info.toString()).contains("POP");
	}

	@Test
	public void testByteCodePrintList(@LogLevel("info") ByteArrayOutputStream info) throws Exception {
		InsnList list = new InsnList();
		list.add(new InsnNode(DUP));
		
		InsnList result = ByteCode.print(list);
		
		assertThat(result).isEqualTo(list);
		assertThat(info.toString()).contains("[DUP]");
	}
	
}
