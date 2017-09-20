package net.amygdalum.testrecorder.util;

import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import net.amygdalum.testrecorder.ByteCode;

public final class Debug {

	private Debug() {
		
	}
	
	public static InsnList print(InsnList instructions) {
		List<String> text = ByteCode.toString(instructions);
		System.out.println(text);
		return instructions;
	}

	public static <T extends AbstractInsnNode> T print(T node) {
		String text = ByteCode.toString(node);
		System.out.println(text);
		return node;
	}

	public static <T> T print(T object) {
		System.out.println(object);
		return object;
	}

}
