package net.amygdalum.testrecorder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.util.ByteCode.constructorDescriptor;
import static net.amygdalum.testrecorder.util.ByteCode.isNative;
import static net.amygdalum.testrecorder.util.ByteCode.isPrimitive;
import static net.amygdalum.testrecorder.util.ByteCode.isStatic;
import static net.amygdalum.testrecorder.util.ByteCode.methodDescriptor;
import static net.amygdalum.testrecorder.util.ByteCode.primitiveNull;
import static net.amygdalum.testrecorder.util.ByteCode.pushAsArray;
import static net.amygdalum.testrecorder.util.ByteCode.unboxPrimitives;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.RETURN;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.amygdalum.testrecorder.bridge.BridgedFakeIO;
import net.amygdalum.testrecorder.runtime.FakeIO;

public class FakeIOTransformer extends AttachableClassFileTransformer implements ClassFileTransformer {

	private Instrumentation inst;

	private Set<Class<?>> classes;
	private Set<String> methods;

	private Set<Class<?>> nativeClasses;

	public FakeIOTransformer() {
		this.classes = new LinkedHashSet<>();
		this.methods = new LinkedHashSet<>();
		this.nativeClasses = new LinkedHashSet<>();
	}

	@Override
	public FakeIOTransformer attach(Instrumentation inst) {
		this.inst = inst;
		FakeIOTransformer attach = (FakeIOTransformer) super.attach(inst);
		return attach;
	}

	public void addClasses(Collection<Class<?>> classes) {
		this.classes.addAll(classes);
	}

	public void addMethods(Collection<String> methods) {
		this.methods.addAll(methods);
	}

	public void restart(Class<?>... classes) {
		try {
			if (classes != null && classes.length != 0) {
				inst.retransformClasses(classes);
			}
		} catch (UnmodifiableClassException e) {
			System.err.println("unexpected class transforming restriction: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}

	public Class<?>[] classesToRetransform() throws ClassNotFoundException {
		return classes.toArray(new Class[0]);
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		try {
			if (className != null && matches(className)) {
				ClassReader cr = new ClassReader(classfileBuffer);
				ClassNode classNode = new ClassNode();

				cr.accept(classNode, 0);
				insertIOFakes(classNode, classBeingRedefined);

				ClassWriter out = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
				classNode.accept(out);
				return out.toByteArray();
			}
			return null;
		} catch (Throwable e) {
			System.err.println("transformation error: " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
	}

	private boolean matches(String className) {
		for (Class<?> clazz : classes) {
			if (Type.getInternalName(clazz).equals(className)) {
				return true;
			}
		}
		return false;
	}

	private boolean needsBridging(ClassNode classNode, Class<?> clazz) {
		if (clazz != null && clazz.getClassLoader() == null) {
			return true;
		}
		return false;
	}

	private void insertIOFakes(ClassNode classNode, Class<?> clazz) {
		for (MethodNode methodNode : fakedMethods(classNode)) {
			insertIOFakes(classNode, methodNode, clazz);
		}
	}

	private void insertIOFakes(ClassNode classNode, MethodNode methodNode, Class<?> clazz) {
		InsnList insnList = createIOFakes(classNode, methodNode, clazz);
		methodNode.instructions.insert(insnList);
	}

	private InsnList createIOFakes(ClassNode classNode, MethodNode methodNode, Class<?> clazz) {
		if (isNative(methodNode)) {
			return createNativeIOFakes(classNode, methodNode, clazz);
		} else {
			return createOrdinaryIOFakes(classNode, methodNode, clazz);
		}
	}

	private InsnList createNativeIOFakes(ClassNode classNode, MethodNode methodNode, Class<?> clazz) {
		nativeClasses.add(clazz);
		methodNode.access = methodNode.access & ~Opcodes.ACC_NATIVE;

		InsnList insnList = new InsnList();

		if (needsBridging(classNode, clazz)) {
			insnList.add(createBridgedIOFake(classNode, methodNode));
		} else {
			insnList.add(createDirectIOFake(classNode, methodNode));
		}

		Type returnType = Type.getReturnType(methodNode.desc);
		insnList.add(returnDummy(returnType));

		return insnList;
	}

	private InsnList createOrdinaryIOFakes(ClassNode classNode, MethodNode methodNode, Class<?> clazz) {
		InsnList insnList = new InsnList();

		if (needsBridging(classNode, clazz)) {
			insnList.add(createBridgedIOFake(classNode, methodNode));
		} else {
			insnList.add(createDirectIOFake(classNode, methodNode));
		}

		return insnList;
	}

	private InsnList createBridgedIOFake(ClassNode classNode, MethodNode methodNode) {
		InsnList insnList = new InsnList();

		Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
		Type returnType = Type.getReturnType(methodNode.desc);

		int[] argumentVars = resolveArgumentLocals(methodNode, argumentTypes);

		insnList.add(pushClassName(classNode, methodNode));
		insnList.add(pushStackTrace());
		insnList.add(pushInstance(methodNode));
		insnList.add(pushMethodName(methodNode));
		insnList.add(pushMethodDesc(methodNode));
		insnList.add(pushArguments(argumentTypes, argumentVars));
		insnList.add(callBridgedFake());

		insnList.add(returnBridgedFakeOrProceed(returnType));
		return insnList;
	}

	private InsnList createDirectIOFake(ClassNode classNode, MethodNode methodNode) {
		InsnList insnList = new InsnList();

		Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
		Type returnType = Type.getReturnType(methodNode.desc);

		int[] argumentVars = resolveArgumentLocals(methodNode, argumentTypes);

		insnList.add(pushClassName(classNode, methodNode));
		insnList.add(pushStackTrace());
		insnList.add(pushInstance(methodNode));
		insnList.add(pushMethodName(methodNode));
		insnList.add(pushMethodDesc(methodNode));
		insnList.add(pushArguments(argumentTypes, argumentVars));
		insnList.add(callFake());

		insnList.add(returnFakeOrProceed(returnType));
		return insnList;
	}

	/* 
	 * String name = this.getClass().getName();
	 */
	private InsnList pushClassName(ClassNode classNode, MethodNode methodNode) {
		InsnList insnList = new InsnList();
		if (isStatic(methodNode)) {
			insnList.add(new LdcInsnNode(Type.getObjectType(classNode.name).getClassName()));
		} else {
			insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(Object.class), "getClass", methodDescriptor(Object.class, "getClass"), false));
			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(Class.class), "getName", methodDescriptor(Class.class, "getName"), false));
		}
		return insnList;
	}

	/*
	 * StackTraceElement[] stackTrace = new Throwable().getStackTrace();
	 */
	private InsnList pushStackTrace() {
		InsnList insnList = new InsnList();
		insnList.add(new TypeInsnNode(NEW, Type.getInternalName(Throwable.class)));
		insnList.add(new InsnNode(DUP));
		insnList.add(new MethodInsnNode(INVOKESPECIAL, Type.getInternalName(Throwable.class), "<init>", constructorDescriptor(Throwable.class), false));
		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(Throwable.class), "getStackTrace", methodDescriptor(Throwable.class, "getStackTrace"), false));
		return insnList;
	}

	/*
	 * Object instance = <static ? null : this>;
	 */
	private AbstractInsnNode pushInstance(MethodNode methodNode) {
		if (isStatic(methodNode)) {
			return new InsnNode(ACONST_NULL);
		} else {
			return new VarInsnNode(ALOAD, 0);
		}
	}

	/*
	 * String methodName = <methodNode.name>;
	 */
	private LdcInsnNode pushMethodName(MethodNode methodNode) {
		return new LdcInsnNode(methodNode.name);
	}

	/*
	 * String methodDesc = <methodNode.desc>;
	 */
	private LdcInsnNode pushMethodDesc(MethodNode methodNode) {
		return new LdcInsnNode(methodNode.desc);
	}

	/*
	 * 	Object[] varargs = <argumentVars>;
	 */
	private InsnList pushArguments(Type[] argumentTypes, int[] argumentVars) {
		return pushAsArray(argumentVars, argumentTypes);
	}

	/*
	 * Object result = FakeIO.callFake(name, stackTrace, instance, methodName, methodDesc, varargs);
	 */
	private AbstractInsnNode callFake() {
		String desc = methodDescriptor(FakeIO.class, "callFake", String.class, StackTraceElement[].class, Object.class, String.class, String.class, Object[].class);
		return new MethodInsnNode(INVOKESTATIC, Type.getInternalName(FakeIO.class), "callFake", desc, false);
	}

	/*
	 * Object result = BridgedFakeIO.callFake(name, stackTrace, instance, methodName, methodDesc, varargs);
	 */
	private AbstractInsnNode callBridgedFake() {
		String desc = methodDescriptor(BridgedFakeIO.class, "callFake", String.class, StackTraceElement[].class, Object.class, String.class, String.class, Object[].class);
		return new MethodInsnNode(INVOKESTATIC, Type.getInternalName(BridgedFakeIO.class), "callFake", desc, false);
	}

	/*
	 * if (result != FakeIO.NO_RESULT) {
	 *   return result;
	 * }
	 */
	private InsnList returnFakeOrProceed(Type returnType) {
		InsnList insnList = new InsnList();
		LabelNode continueLabel = new LabelNode();
		insnList.add(new InsnNode(DUP));
		insnList.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(FakeIO.class), "NO_RESULT", Type.getDescriptor(Object.class)));
		insnList.add(new JumpInsnNode(Opcodes.IF_ACMPEQ, continueLabel));
		if (returnType.getSize() == 0) {
			insnList.add(new InsnNode(POP));
			insnList.add(new InsnNode(RETURN));
		} else if (isPrimitive(returnType)) {
			insnList.add(unboxPrimitives(returnType));
			insnList.add(new InsnNode(returnType.getOpcode(IRETURN)));
		} else {
			insnList.add(new TypeInsnNode(CHECKCAST, returnType.getInternalName()));
			insnList.add(new InsnNode(ARETURN));
		}
		insnList.add(continueLabel);
		insnList.add(new InsnNode(POP));
		return insnList;
	}

	/*
	 * if (result != BridgedFakeIO.NO_RESULT) {
	 *   return result;
	 * }
	 */
	private InsnList returnBridgedFakeOrProceed(Type returnType) {
		InsnList insnList = new InsnList();
		LabelNode continueLabel = new LabelNode();
		insnList.add(new InsnNode(DUP));
		insnList.add(new FieldInsnNode(GETSTATIC, Type.getInternalName(BridgedFakeIO.class), "NO_RESULT", Type.getDescriptor(Object.class)));
		insnList.add(new JumpInsnNode(Opcodes.IF_ACMPEQ, continueLabel));
		if (returnType.getSize() == 0) {
			insnList.add(new InsnNode(POP));
			insnList.add(new InsnNode(RETURN));
		} else if (isPrimitive(returnType)) {
			insnList.add(unboxPrimitives(returnType));
			insnList.add(new InsnNode(returnType.getOpcode(IRETURN)));
		} else {
			insnList.add(new TypeInsnNode(CHECKCAST, returnType.getInternalName()));
			insnList.add(new InsnNode(ARETURN));
		}
		insnList.add(continueLabel);
		insnList.add(new InsnNode(POP));
		return insnList;
	}

	/*
	 * return 0; // null value
	 */
	private InsnList returnDummy(Type returnType) {
		InsnList insnList = new InsnList();
		if (returnType.getSize() == 0) {
			insnList.add(new InsnNode(RETURN));
		} else if (isPrimitive(returnType)) {
			insnList.add(primitiveNull(returnType));
			insnList.add(new InsnNode(returnType.getOpcode(IRETURN)));
		} else {
			insnList.add(new InsnNode(ACONST_NULL));
			insnList.add(new InsnNode(ARETURN));
		}
		return insnList;
	}

	private int[] resolveArgumentLocals(MethodNode methodNode, Type[] argumentTypes) {
		int localVariableIndex = isStatic(methodNode) ? 0 : 1;
		int[] argumentVars = new int[argumentTypes.length];
		for (int i = 0; i < argumentVars.length; i++) {
			argumentVars[i] = localVariableIndex++;
		}
		return argumentVars;
	}

	private List<MethodNode> fakedMethods(ClassNode classNode) {
		return classNode.methods.stream()
			.filter(method -> methods.contains(method.name + method.desc))
			.collect(toList());
	}

	public void reset() {
		Class<?>[] classesToReset = classes.toArray(new Class[0]);
		Class<?>[] classesToRelink = nativeClasses.toArray(new Class[0]);
		classes.clear();
		methods.clear();
		nativeClasses.clear();
		if (classesToReset.length > 0) {
			try {
				inst.retransformClasses(classesToReset);
			} catch (UnmodifiableClassException e) {
				System.err.println("unexpected class transforming restriction: " + e.getMessage());
				e.printStackTrace(System.err);
			}
		}
		for (Class<?> clazz : classesToRelink) {
			relinkNativeMethods(clazz);
		}
	}

	private static void relinkNativeMethods(Class<?> clazz) {
		Method registerNatives = findRegisterNatives(clazz);

		if (registerNatives != null) {
			try {
				registerNatives.setAccessible(true);
				registerNatives.invoke(null);
			} catch (ReflectiveOperationException e) {
			}
		}
	}

	private static Method findRegisterNatives(Class<?> clazz) {
		try {
			return clazz.getDeclaredMethod("registerNatives");
		} catch (NoSuchMethodException e) {
		}
		try {
			return clazz.getDeclaredMethod("initIDs");
		} catch (NoSuchMethodException e) {
		}
		return null;
	}

}