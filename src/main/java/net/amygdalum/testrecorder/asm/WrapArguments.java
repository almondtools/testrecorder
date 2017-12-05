package net.amygdalum.testrecorder.asm;

import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.ILOAD;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class WrapArguments implements SequenceInstruction {

	public WrapArguments() {
	}

	@Override
	public InsnList build(MethodContext context) {
		Type[] argumentTypes = context.getArgumentTypes();
		int[] arguments = context.getArguments();

		InsnList insnList = new InsnList();

		insnList.add(new LdcInsnNode(arguments.length));
		insnList.add(new TypeInsnNode(Opcodes.ANEWARRAY, Type.getInternalName(Object.class)));

		for (int i = 0; i < arguments.length; i++) {
			insnList.add(new InsnNode(DUP));
			insnList.add(new LdcInsnNode(i));

			int index = arguments[i];
			Type type = argumentTypes[i];

			insnList.add(new VarInsnNode(type.getOpcode(ILOAD), index));

			insnList.add(new BoxPrimitives(type).build(context));

			insnList.add(new InsnNode(AASTORE));
		}
		return insnList;
	}

}
