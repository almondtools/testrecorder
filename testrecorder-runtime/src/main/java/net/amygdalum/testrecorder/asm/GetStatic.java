package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.asm.ByteCode.fieldDescriptor;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;


public class GetStatic implements SequenceInstruction {

	private Class<?> clazz;
	private String field;

	public GetStatic(Class<?> clazz, String field) {
		this.clazz = clazz;
		this.field = field;
	}

	@Override
	public InsnList build(MethodContext context) {
		return list(new FieldInsnNode(Opcodes.GETSTATIC, Type.getInternalName(clazz), field, fieldDescriptor(clazz, field)));
	}

}
