package net.amygdalum.testrecorder.asm;



import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class GetInvokedMethodName implements SequenceInstruction {

	private MethodInsnNode call;

	public GetInvokedMethodName(MethodInsnNode call) {
		this.call = call;
	}

	@Override
	public InsnList build(MethodContext context) {
		return list(new LdcInsnNode(call.name));
	}

}
