package net.amygdalum.testrecorder.util;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

public class Debug {

	public static InsnList print(InsnList instructions) {
		Printer p = new Textifier();
		TraceMethodVisitor mp = new TraceMethodVisitor(p);
		instructions.accept(mp);
		System.out.println(p.getText());
		return instructions;
	}

	public static <T> T print(T object) {
		System.out.println(object);
		return object;
	}

}
