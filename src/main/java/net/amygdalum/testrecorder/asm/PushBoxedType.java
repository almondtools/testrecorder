package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.asm.ByteCode.boxedType;
import static net.amygdalum.testrecorder.asm.ByteCode.isPrimitive;
import static org.objectweb.asm.Opcodes.GETSTATIC;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;

public class PushBoxedType implements SequenceInstruction {

	private Type type;

	public PushBoxedType(Type type) {
		this.type = type;
	}

	@Override
	public InsnList build(MethodContext context) {
		if (isPrimitive(type)) {
			Type boxedType = boxedType(type);
			return list(new FieldInsnNode(GETSTATIC, boxedType.getInternalName(), "TYPE", Type.getDescriptor(Class.class)));
		} else {
			return list(new LdcInsnNode(type));
		}
	}

}
