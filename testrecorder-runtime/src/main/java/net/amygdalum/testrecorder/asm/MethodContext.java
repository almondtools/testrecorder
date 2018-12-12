package net.amygdalum.testrecorder.asm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

public class MethodContext {

	private ClassNode classNode;
	private MethodNode methodNode;
	private int nextLocal;
	private Map<String, Local> variables;

	public MethodContext(ClassNode classNode, MethodNode methodNode) {
		this.classNode = classNode;
		this.methodNode = methodNode;
		this.nextLocal = methodNode.maxLocals;
		this.variables = new HashMap<>();
	}
	
	public Local newLocal(String variableName, Type type) {
		Local newLocal = new Local(nextLocal, type);
		nextLocal += type.getSize();
		variables.put(variableName, newLocal);
		return newLocal;
	}

	public Local newLocal(Type type) {
		Local newLocal = new Local(nextLocal, type);
		nextLocal += type.getSize();
		return newLocal;
	}

	public boolean isStatic() {
		return ByteCode.isStatic(methodNode);
	}
	
	public String getClassName() {
		return classNode.name;
	}

	public Type getClassType() {
		return Type.getObjectType(classNode.name);
	}

	public String getMethodName() {
		return methodNode.name;
	}

	public String getMethodDescriptor() {
		return methodNode.desc;
	}
	
	public Local local(String variableName) {
		return variables.get(variableName);
	}

	public int[] getArguments() {
		Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
		int index = isStatic() ? 0 : 1;
		int[] arguments = new int[argumentTypes.length];
		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = index;
			index += argumentTypes[i].getSize();
		}
		return arguments;
	}

	public Type[] getArgumentTypes() {
		return Type.getArgumentTypes(methodNode.desc);
	}
	
	public Type getResultType() {
		return Type.getReturnType(methodNode.desc);
	}

	public List<TryCatchBlockNode> tryCatchBlocks() {
		return methodNode.tryCatchBlocks;
	}

	public InsnList instructions() {
		return methodNode.instructions;
	}

}
