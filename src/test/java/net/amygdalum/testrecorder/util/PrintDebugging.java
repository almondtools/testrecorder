package net.amygdalum.testrecorder.util;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.POP;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;

import net.amygdalum.testrecorder.asm.ByteCode;

public class PrintDebugging {

	@Rule
	public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

	@Test
	public void testDebugPrint() throws Exception {
		String result = Debug.print("text");

		assertThat(result, equalTo("text"));
		assertThat(systemOutRule.getLog(), containsString("text"));
	}

	@Test
	public void testByteCodePrint() throws Exception {
		InsnNode insn = new InsnNode(POP);

		InsnNode result = ByteCode.print(insn);

		assertThat(result, equalTo(insn));
		assertThat(systemOutRule.getLog(), containsString("POP"));
	}

	@Test
	public void testByteCodePrintList() throws Exception {
		InsnList list = new InsnList();
		list.add(new InsnNode(DUP));
		
		InsnList result = ByteCode.print(list);
		
		assertThat(result, equalTo(list));
		assertThat(systemOutRule.getLog(), containsString("[DUP]"));
	}
	
}
