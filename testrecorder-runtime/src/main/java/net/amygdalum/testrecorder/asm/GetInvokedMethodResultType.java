package net.amygdalum.testrecorder.asm;



import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

public class GetInvokedMethodResultType implements SequenceInstruction {

	private MethodInsnNode call;

	public GetInvokedMethodResultType(MethodInsnNode call) {
		this.call = call;
	}

	@Override
	public InsnList build(MethodContext context) {
		Type resultType = Type.getReturnType(call.desc);
		return new PushBoxedType(resultType).build(context);
	}

}
