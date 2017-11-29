package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.util.ByteCode.boxPrimitives;
import static net.amygdalum.testrecorder.util.ByteCode.isStatic;
import static net.amygdalum.testrecorder.util.ByteCode.range;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.ILOAD;

import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class WrapArguments implements InsnListBuilder {

	private MethodNode methodNode;

	public WrapArguments(MethodNode methodNode) {
		this.methodNode = methodNode;
	}

	@Override
	public InsnList build() {
		int localVariableIndex = isStatic(methodNode) ? 0 : 1;
		Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
		List<LocalVariableNode> arguments = range(methodNode.localVariables, localVariableIndex, argumentTypes.length);
		
		InsnList insnList = new InsnList();

		insnList.add(new LdcInsnNode(arguments.size()));
		insnList.add(new TypeInsnNode(Opcodes.ANEWARRAY, Type.getInternalName(Object.class)));

		for (int i = 0; i < arguments.size(); i++) {
			insnList.add(new InsnNode(DUP));
			insnList.add(new LdcInsnNode(i));

			LocalVariableNode argument = arguments.get(i);
			int index = argument.index;
			Type type = Type.getType(argument.desc);

			insnList.add(new VarInsnNode(type.getOpcode(ILOAD), index));

			insnList.add(boxPrimitives(type));

			insnList.add(new InsnNode(AASTORE));
		}
		return insnList;
	}

}
