package net.amygdalum.testrecorder;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class InstrumentationUnit {
	public ClassNode classNode;
	public MethodNode methodNode;

	public InstrumentationUnit(ClassNode classNode, MethodNode methodNode) {
		this.classNode = classNode;
		this.methodNode = methodNode;
	}

	public static InstrumentationUnit instrument(Class<?> clazz, String methodName) throws IOException, NoSuchMethodException {
		Method method = Arrays.stream(clazz.getDeclaredMethods())
			.filter(m -> m.getName().equals(methodName))
			.findFirst()
			.orElse(null);
		String className = Type.getInternalName(clazz);
		String methodDesc = Type.getMethodDescriptor(method);
	
		ClassReader cr = new ClassReader(className);
		ClassNode classNode = new ClassNode();
	
		cr.accept(classNode, 0);
	
		MethodNode methodNode = classNode.methods.stream()
			.filter(m -> m.name.equals(methodName) && m.desc.equals(methodDesc))
			.findFirst()
			.orElseThrow(() -> new NoSuchMethodException(methodName + methodDesc));
	
		return new InstrumentationUnit(classNode, methodNode);
	}

}