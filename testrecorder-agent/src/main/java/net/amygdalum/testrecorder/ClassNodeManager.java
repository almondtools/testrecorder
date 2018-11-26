package net.amygdalum.testrecorder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.amygdalum.testrecorder.util.IdentityWorkSet;

public class ClassNodeManager {

	private Map<String, ClassNode> cache;

	public ClassNodeManager() {
		cache = new HashMap<>();
	}

	public ClassNode fetch(String className, ClassLoader loader) throws IOException {
		ClassNode classNode = cache.get(className);
		if (classNode == null) {
			ClassReader cr = readerFor(className, loader);
			classNode = new ClassNode();

			cr.accept(classNode, 0);
			cache.put(className, classNode);
		}
		return classNode;
	}

	private ClassReader readerFor(String className, ClassLoader loader) throws IOException {
		if (loader == null) {
			return new ClassReader(className);
		}
		InputStream stream = loader.getResourceAsStream(className.replace('.', '/') + ".class");
		if (stream == null) {
			return new ClassReader(className);
		}
		return new ClassReader(stream);
	}

	public ClassNode register(byte[] buffer) {
		ClassReader cr = new ClassReader(buffer);
		ClassNode classNode = new ClassNode();

		cr.accept(classNode, 0);
		cache.put(classNode.name, classNode);
		return classNode;
	}

	public MethodNode fetch(ClassNode classNode, String methodName, String methodDesc, ClassLoader loader) throws IOException, NoSuchMethodException {
		ClassNode currentClassNode = classNode;
		IdentityWorkSet<String> interfaces = new IdentityWorkSet<>();
		while (currentClassNode != null) {
			for (MethodNode method : currentClassNode.methods) {
				if (method.name.equals(methodName) && method.desc.equals(methodDesc)) {
					return method;
				}
			}
			interfaces.addAll(currentClassNode.interfaces);
			currentClassNode = currentClassNode.superName == null ? null : fetch(currentClassNode.superName, loader);
		}

		while (interfaces.hasMoreElements()) {
			String interfaceName = interfaces.remove();
			currentClassNode = fetch(interfaceName, loader);
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