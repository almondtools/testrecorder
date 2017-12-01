package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.asm.ByteCode.boxPrimitives;
import static net.amygdalum.testrecorder.asm.ByteCode.boxedType;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP2;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class Memoize implements SequenceInstruction {

	private String variableName;
	private Type type;

	public Memoize(String variableName, Type type) {
		this.variableName = variableName;
		this.type = type;
	}

	@Override
	public InsnList build(Sequence sequence) {
		InsnList insnList = new InsnList();
		if (type.getSize() == 1) {
			insnList.add(new InsnNode(DUP));
		} else if (type.getSize() == 2) {
			insnList.add(new InsnNode(DUP2));
		}
		insnList.add(boxPrimitives(type));
		int newLocal = sequence.newLocal(variableName, boxedType(type));
		insnList.add(new VarInsnNode(ASTORE, newLocal));
		return insnList;
	}

}
