package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.asm.ByteCode.boxedType;
import static net.amygdalum.testrecorder.asm.ByteCode.unboxingFactory;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class UnboxPrimitives implements SequenceInstruction {

	private Type type;

	public UnboxPrimitives(Type type) {
		this.type = type;
	}

	@Override
	public InsnList build(MethodContext context) {
		char desc = type.getDescriptor().charAt(0);

		InsnList insnList = new InsnList();

		String factoryDesc = "()" + desc;
		Type boxedType = boxedType(type);
		String factoryName = unboxingFactory(type);

		insnList.add(new TypeInsnNode(CHECKCAST, boxedType.getInternalName()));
		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, boxedType.getInternalName(), factoryName, factoryDesc, false));
		return insnList;
	}

}
