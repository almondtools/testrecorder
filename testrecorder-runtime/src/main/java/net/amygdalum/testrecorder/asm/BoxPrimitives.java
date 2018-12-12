package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.asm.ByteCode.boxedType;
import static net.amygdalum.testrecorder.asm.ByteCode.boxingFactory;
import static net.amygdalum.testrecorder.asm.ByteCode.isPrimitive;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

public class BoxPrimitives implements SequenceInstruction {

	private Type type;

	public BoxPrimitives(Type type) {
		this.type = type;
	}

	@Override
	public InsnList build(MethodContext context) {
		InsnList insnList = new InsnList();
		if (isPrimitive(type)) {
			char desc = type.getDescriptor().charAt(0);
			
			Type boxedType = boxedType(type);
			String boxingFactory = boxingFactory(type);
			
			String factoryDesc = "(" + desc + ")" + boxedType.getDescriptor();
			insnList.add(new MethodInsnNode(INVOKESTATIC, boxedType.getInternalName(), boxingFactory, factoryDesc, false));
		}
		return insnList;
	}

}
