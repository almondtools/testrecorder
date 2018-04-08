package net.amygdalum.testrecorder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.amygdalum.testrecorder.util.WorkSet;

public class ClassNodeManager {

	private Map<String, ClassNode> cache;

	public ClassNodeManager() {
		cache = new HashMap<>();
	}

	public ClassNode fetch(String className) throws IOException {
		ClassNode classNode = cache.get(className);
		if (classNode == null) {
			ClassReader cr = new ClassReader(className);
			classNode = new ClassNode();

			cr.accept(classNode, 0);
			cache.put(className, classNode);
		}
		return classNode;
	}

	public ClassNode register(byte[] buffer) {
		ClassReader cr = new ClassReader(buffer);
		ClassNode classNode = new ClassNode();

		cr.accept(classNode, 0);
		cache.put(classNode.name, classNode);
		return classNode;
	}

	public MethodNode fetch(ClassNode classNode, String methodName, String methodDesc) throws IOException, NoSuchMethodException {
		ClassNode currentClassNode = classNode;
		WorkSet<String> interfaces = new WorkSet<>();
		while (currentClassNode != null) {
			for (MethodNode method : currentClassNode.methods) {
				if (method.name.equals(methodName) && method.desc.equals(methodDesc)) {
					return method;
				}
			}
			interfaces.addAll(currentClassNode.interfaces);
			currentClassNode = currentClassNode.superName == null ? null : fetch(currentClassNode.superName);
		}

		while (interfaces.hasMoreElements()) {
			String interfaceName = interfaces.remove();
			currentClassNode = fetch(interfaceName);
			for (MethodNode method : currentClassNode.methods) {
				if (method.name.equals(methodName) && method.desc.equals(methodDesc)) {
					return method;
				}
			}
			interfaces.addAll(currentClassNode.interfaces);
		}
		throw new NoSuchMethodException(methodName + methodDesc);
	}

}