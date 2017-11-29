package net.amygdalum.testrecorder.asm;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

public class Locals {

	private int nextLocal;

	public Locals(MethodNode methodNode) {
		this.nextLocal = methodNode.maxLocals;
	}

	public int newLocal(Type returnType) {
		int newLocal = nextLocal;
		nextLocal += returnType.getSize();
		return newLocal;
	}

	public int newLocalObject() {
		int newLocal = nextLocal;
		nextLocal++;
		return newLocal;
	}

}
