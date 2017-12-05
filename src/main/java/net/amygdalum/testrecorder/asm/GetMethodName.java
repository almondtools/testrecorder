package net.amygdalum.testrecorder.asm;



import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;

public class GetMethodName implements SequenceInstruction {

	public GetMethodName() {
	}

	@Override
	public InsnList build(MethodContext context) {
		return list(new LdcInsnNode(context.getMethodName()));
	}

}
