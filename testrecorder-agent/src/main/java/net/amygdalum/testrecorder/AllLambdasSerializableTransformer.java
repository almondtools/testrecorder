package net.amygdalum.testrecorder;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.amygdalum.testrecorder.util.AttachableClassFileTransformer;
import net.amygdalum.testrecorder.util.Logger;

public class AllLambdasSerializableTransformer extends AttachableClassFileTransformer implements ClassFileTransformer {

	private static final int IS_SERIALIZABLE_PARAMETER_LOCAL = 7;

	public AllLambdasSerializableTransformer() {
	}
	
	@Override
	public Collection<Class<?>> filterClassesToRetransform(Class<?>[] loaded) {
		for (Class<?> clazz : loaded) {
			if ("java.lang.invoke.InnerClassLambdaMetafactory".equals(clazz.getName())) {
				return singleton(clazz);
			}
		}
		return emptyList();
	}

	@Override
	public Collection<Class<?>> getClassesToRetransform() {
		try {
			return singletonList(Class.forName("java.lang.invoke.InnerClassLambdaMetafactory"));
		} catch (ClassNotFoundException e) {
			return emptyList();
		}
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		try {
			if (className != null && className.equals("java/lang/invoke/InnerClassLambdaMetafactory")) {
				ClassReader cr = new ClassReader(classfileBuffer);
				ClassNode classNode = new ClassNode();

				cr.accept(classNode, 0);
				classNode.methods.stream()
					.filter(method -> "<init>".equals(method.name))
					.findFirst()
					.ifPresent(method -> {
						Optional<VarInsnNode> serialized = findIsSerializeableLocalVariable(method);
						serialized.ifPresent(val -> method.instructions.set(val, new InsnNode(Opcodes.ICONST_1)));
					});

				ClassWriter out = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
				classNode.accept(out);
				return out.toByteArray();
			}
			return null;
		} catch (Throwable e) {
			Logger.error("transformation error: ", e);
			return null;
		}

	}

	private Optional<VarInsnNode> findIsSerializeableLocalVariable(MethodNode method) {
		return stream(method.instructions.iterator())
			.filter(node -> node instanceof VarInsnNode)
			.map(node -> (VarInsnNode) node)
			.filter(node -> node.getOpcode() == Opcodes.ILOAD)
			.filter(node -> node.var == IS_SERIALIZABLE_PARAMETER_LOCAL)
			.findFirst();
	}

	private <T> Stream<T> stream(Iterator<T> iterator) {
		Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
		return StreamSupport.stream(spliterator, false);
	}

}