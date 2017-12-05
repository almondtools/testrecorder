package net.amygdalum.testrecorder.asm;

import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.ISTORE;

import java.util.Arrays;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CaptureCall implements SequenceInstruction {

	private MethodInsnNode call;
	private String variableBase;
	private String variableArguments;

	public CaptureCall(MethodInsnNode call, String variableBase, String variableArguments) {
		this.call = call;
		this.variableBase = variableBase;
		this.variableArguments = variableArguments;
	}

	@Override
	public InsnList build(MethodContext context) {
		InsnList insnList = new InsnList();
		
		Type ownerType = Type.getObjectType(call.owner);
		Type methodType = Type.getMethodType(call.desc);
		Type[] argumentTypes = methodType.getArgumentTypes();

		Local base = context.newLocal(variableBase, Type.getType(Object.class));
		if (call.getOpcode() == INVOKESTATIC) {
			insnList.add(new LdcInsnNode(ownerType));
			insnList.add(new VarInsnNode(ASTORE, base.index));
		} else {
			insnList.add(new InsnNode(DUP));
			insnList.add(new VarInsnNode(ASTORE, base.index));
		}
		
		Local[] arguments = Arrays.stream(argumentTypes).map(type -> context.newLocal(type)).toArray(Local[]::new);
		insnList.add(new LdcInsnNode(arguments.length));
		insnList.add(new TypeInsnNode(Opcodes.ANEWARRAY, Type.getInternalName(Object.class)));
		for (int i = 0; i < arguments.length; i++) {
			Local arg = arguments[i];
			insnList.add(new InsnNode(DUP));
			insnList.add(new LdcInsnNode(i));

			insnList.add(new VarInsnNode(arg.type.getOpcode(ILOAD), arg.index));

			insnList.add(new BoxPrimitives(arg.type).build(context));

			insnList.add(new InsnNode(AASTORE));
		}
		Local args = context.newLocal(variableArguments, Type.getType(Object[].class));
		insnList.add(new VarInsnNode(ASTORE, args.index));
		
		for (Local arg : arguments) {
			int storecode = arg.type.getOpcode(ISTORE);
			insnList.insert(new VarInsnNode(storecode, arg.index));
			int loadcode = arg.type.getOpcode(ILOAD);
			insnList.add(new VarInsnNode(loadcode, arg.index));
		}
		
		return insnList;
	}

}
