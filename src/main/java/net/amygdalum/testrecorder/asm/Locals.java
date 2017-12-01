package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.asm.ByteCode.isStatic;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

public class Locals {

	private Type[] argumentTypes;
	private int[] arguments;
	private int nextLocal;

	public Locals(MethodNode methodNode) {
		this.argumentTypes = Type.getArgumentTypes(methodNode.desc);
		this.arguments = computeLocals(argumentTypes, isStatic(methodNode));
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

	public int[] getArguments() {
		return arguments;
	}

	private static int[] computeLocals(Type[] argumentTypes, boolean isStatic) {
		int index = isStatic ? 0 : 1;
		int[] arguments = new int[argumentTypes.length];
		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = index;
			index += argumentTypes[i].getSize();
		}
		return arguments;
	}

	public Type[] getArgumentTypes() {
		return argumentTypes;
	}

}
