package net.amygdalum.testrecorder;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

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
		Set<String> interfaces = new LinkedHashSet<>();
		while (currentClassNode != null) {
			for (MethodNode method : currentClassNode.methods) {
				if (method.name.equals(methodName) && method.desc.equals(methodDesc)) {
					return method;
				}
			}
			interfaces.addAll(currentClassNode.interfaces);
			currentClassNode = currentClassNode.superName == null ? null : fetch(currentClassNode.superName);
		}
		for (String interfaceName : interfaces) {
			currentClassNode = fetch(interfaceName);
			for (MethodNode method : currentClassNode.methods) {
				if (method.name.equals(methodName) && method.desc.equals(methodDesc)) {
					return method;
				}
			}
		}
		return null;
	}

}