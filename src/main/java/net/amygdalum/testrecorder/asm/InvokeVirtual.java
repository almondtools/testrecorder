package net.amygdalum.testrecorder.asm;

import static net.amygdalum.testrecorder.util.ByteCode.methodDescriptor;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

public class InvokeVirtual extends AbstractInsnListBuilder {

	private Class<?> clazz;
	private String method;
	private Class<?>[] argumentTypes;
	
	private InsnListBuilder base;
	private InsnListBuilder[] arguments;

	public InvokeVirtual(Class<?> clazz, String method, Class<?>... argumentTypes) {
		this.clazz = clazz;
		this.method = method;
		this.argumentTypes = argumentTypes;
		this.arguments = new InsnListBuilder[argumentTypes.length];
	}

	public InvokeVirtual withBase(InsnListBuilder base) {
		this.base = base;
		return this;
	}

	public InvokeVirtual withArgument(int i, InsnListBuilder argument) {
		arguments[i] = argument;
		return this;
	}

	@Override
	public InsnList build() {
		InsnList insnList = new InsnList();

		insnList.add(base.build());

		for (InsnListBuilder argument : arguments) {
			insnList.add(argument.build());
		}
		
		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(clazz), method, methodDescriptor(clazz, method, argumentTypes), false));
		
		return insnList;
	}

}
