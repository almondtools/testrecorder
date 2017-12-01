package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.asm.ByteCode.isStatic;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public class GetClassName implements SequenceInstruction {

	private ClassNode classNode;
	private MethodNode methodNode;

	public GetClassName(ClassNode classNode, MethodNode methodNode) {
		this.classNode = classNode;
		this.methodNode = methodNode;
	}

	@Override
	public InsnList build(Sequence sequence) {
		return isStatic(methodNode)
			? new Ldc(Type.getObjectType(classNode.name).getClassName())
				.build(sequence)
			: new InvokeVirtual(Class.class, "getName")
				.withBase(new InvokeVirtual(Object.class, "getClass")
					.withBase(new This()))
				.build(sequence);
	}

}
