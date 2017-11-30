package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.util.ByteCode.list;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class GetMethodName implements SequenceInstruction {

	private MethodNode methodNode;

	public GetMethodName(MethodNode methodNode) {
		this.methodNode = methodNode;
	}

	@Override
	public InsnList build(Sequence sequence) {
		return list(new LdcInsnNode(methodNode.name));
	}

}
