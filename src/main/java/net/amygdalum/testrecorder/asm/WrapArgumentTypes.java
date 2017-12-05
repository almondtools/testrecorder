package net.amygdalum.testrecorder.asm;

import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.DUP;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class WrapArgumentTypes implements SequenceInstruction {

	public WrapArgumentTypes() {
	}

	@Override
	public InsnList build(MethodContext context) {
		Type[] argumentTypes = context.getArgumentTypes();

		InsnList insnList = new InsnList();

		insnList.add(new LdcInsnNode(argumentTypes.length));
		insnList.add(new TypeInsnNode(Opcodes.ANEWARRAY, Type.getInternalName(java.lang.reflect.Type.class)));

		for (int i = 0; i < argumentTypes.length; i++) {
			insnList.add(new InsnNode(DUP));
			insnList.add(new LdcInsnNode(i));
			insnList.add(new PushBoxedType(argumentTypes[i]).build(context));
			insnList.add(new InsnNode(AASTORE));
		}
		return insnList;
	}

}
