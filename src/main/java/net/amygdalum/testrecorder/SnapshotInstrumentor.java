package net.amygdalum.testrecorder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.ByteCode.memorizeLocal;
import static net.amygdalum.testrecorder.ByteCode.pushAsArray;
import static net.amygdalum.testrecorder.ByteCode.pushType;
import static net.amygdalum.testrecorder.ByteCode.pushTypes;
import static net.amygdalum.testrecorder.ByteCode.range;
import static net.amygdalum.testrecorder.ByteCode.recallLocal;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACC_ANNOTATION;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SWAP;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.amygdalum.testrecorder.util.Types;

public class SnapshotInstrumentor implements ClassFileTransformer {

	private static final String STATIC_INIT_NAME = "<clinit>";

	private static final String GET_DECLARED_METHOD = "getDeclaredMethod";

	public static final String SNAPSHOT_MANAGER_FIELD_NAME = "MANAGER";
	private static final String REGISTER = "register";
	private static final String SETUP_VARIABLES = "setupVariables";
	private static final String INPUT_VARIABLES = "inputVariables";
	private static final String OUTPUT_VARIABLES = "outputVariables";
	private static final String THROW_VARIABLES = "throwVariables";
	private static final String EXPECT_VARIABLES = "expectVariables";

	private static final String Class_name = Type.getInternalName(Class.class);
	private static final String Types_name = Type.getInternalName(Types.class);
	private static final String SnapshotManager_name = Type.getInternalName(SnapshotManager.class);

	private static final String SnaphotManager_descriptor = Type.getDescriptor(SnapshotManager.class);
	private static final String Snapshot_descriptor = Type.getDescriptor(Snapshot.class);
	private static final String SnapshotInput_descriptor = Type.getDescriptor(SnapshotInput.class);
	private static final String SnapshotOutput_descriptor = Type.getDescriptor(SnapshotOutput.class);

	private static final String SnaphotManager_registerMethod_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, REGISTER, String.class, Method.class);
	private static final String SnaphotManager_setupVariables_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, SETUP_VARIABLES, Object.class, String.class, Object[].class);
	private static final String SnaphotManager_expectVariablesResult_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, EXPECT_VARIABLES, Object.class, Object.class, Object[].class);
	private static final String SnaphotManager_expectVariablesNoResult_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, EXPECT_VARIABLES, Object.class, Object[].class);
	private static final String SnaphotManager_throwVariables_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, THROW_VARIABLES, Object.class, Throwable.class, Object[].class);
	private static final String SnaphotManager_outputVariables_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, OUTPUT_VARIABLES, Class.class, String.class, java.lang.reflect.Type[].class, Object[].class);
	private static final String SnaphotManager_inputVariablesResult_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, INPUT_VARIABLES, Class.class, String.class, java.lang.reflect.Type.class, Object.class, java.lang.reflect.Type[].class, Object[].class);
	private static final String SnaphotManager_inputVariablesNoResult_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, INPUT_VARIABLES, Class.class, String.class, java.lang.reflect.Type[].class, Object[].class);

	private static final String Types_getDeclaredMethod_descriptor = ByteCode.methodDescriptor(Types.class, GET_DECLARED_METHOD, Class.class, String.class, Class[].class);

	private TestRecorderAgentConfig config;

	public SnapshotInstrumentor(TestRecorderAgentConfig config) {
		this.config = config;
		SnapshotManager.init(config);
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		for (String pkg : config.getPackages()) {
			pkg = pkg.replace('.', '/');
			if (className != null && className.startsWith(pkg)) {
				System.out.println("recording snapshots of " + className);
				return instrument(new ClassReader(classfileBuffer));
			}
		}
		return null;
	}

	public byte[] instrument(String className) throws IOException {
		return instrument(new ClassReader(className));
	}

	public byte[] instrument(ClassReader cr) {
		ClassNode classNode = new ClassNode();

		cr.accept(classNode, 0);
		if (isClass(classNode)) {

			instrumentStaticInitializer(classNode);

			instrumentSnapshotMethods(classNode);

			instrumentInputMethods(classNode);
			instrumentOutputMethods(classNode);
		}

		ClassWriter out = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(out);
		return out.toByteArray();
	}

	private boolean isClass(ClassNode classNode) {
		return (classNode.access & (ACC_INTERFACE | ACC_ANNOTATION)) == 0;
	}

	private void instrumentStaticInitializer(ClassNode classNode) {
		MethodNode method = findStaticInitializer(classNode.methods);

		InsnList insnList = new InsnList();

		insnList.add(new FieldInsnNode(Opcodes.GETSTATIC, SnapshotManager_name, SNAPSHOT_MANAGER_FIELD_NAME, SnaphotManager_descriptor));
		for (MethodNode methodNode : getSnapshotMethods(classNode)) {
			insnList.add(new InsnNode(DUP));
			insnList.add(new LdcInsnNode(keySignature(classNode, methodNode)));

			insnList.add(pushMethod(classNode, methodNode));

			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, REGISTER, SnaphotManager_registerMethod_descriptor, false));
		}
		insnList.add(new InsnNode(POP));

		method.instructions.insert(insnList);

	}

	private MethodNode findStaticInitializer(List<MethodNode> methods) {
		for (MethodNode method : methods) {
			if (method.name.equals(STATIC_INIT_NAME)) {
				return method;
			}
		}
		MethodNode method = new MethodNode(ACC_STATIC, STATIC_INIT_NAME, "()V", "()V", new String[0]);
		method.instructions.add(new InsnNode(RETURN));
		methods.add(method);
		return method;
	}

	private void instrumentSnapshotMethods(ClassNode classNode) {
		for (MethodNode method : getSnapshotMethods(classNode)) {
			LabelNode tryLabel = new LabelNode();
			LabelNode catchLabel = new LabelNode();
			LabelNode finallyLabel = new LabelNode();
			method.tryCatchBlocks.add(createTryCatchBlock(tryLabel, catchLabel));
			method.instructions.insert(createTry(tryLabel, setupVariables(classNode, method)));
			List<InsnNode> rets = findReturn(method.instructions);
			for (InsnNode ret : rets) {
				method.instructions.insert(ret, new JumpInsnNode(GOTO, finallyLabel));
				method.instructions.remove(ret);
			}
			int returnOpcode = rets.stream()
				.map(ret -> ret.getOpcode())
				.distinct()
				.findFirst()
				.orElse(RETURN);
			method.instructions.add(createCatchFinally(catchLabel, throwVariables(classNode, method), finallyLabel, expectVariables(classNode, method), new InsnNode(returnOpcode)));
		}
	}

	private void instrumentInputMethods(ClassNode classNode) {
		for (MethodNode method : getInputMethods(classNode)) {
			List<InsnNode> rets = findReturn(method.instructions);
			InsnList notifyInput = notifyInput(classNode, method);
			for (InsnNode ret : rets) {
				method.instructions.insertBefore(ret, notifyInput);
			}
		}
	}

	private void instrumentOutputMethods(ClassNode classNode) {
		for (MethodNode method : getOutputMethods(classNode)) {
			method.instructions.insert(notifyOutput(classNode, method));
		}
	}

	private <T> Stream<T> stream(Iterator<T> iterator) {
		Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
		return StreamSupport.stream(spliterator, false);
	}

	private List<InsnNode> findReturn(InsnList instructions) {
		return stream(instructions.iterator())
			.filter(insn -> insn instanceof InsnNode)
			.map(insn -> (InsnNode) insn)
			.filter(insn -> IRETURN <= insn.getOpcode() && insn.getOpcode() <= RETURN)
			.collect(toList());
	}

	private InsnList pushMethod(ClassNode classNode, MethodNode method) {
		Type[] argumentTypes = Type.getArgumentTypes(method.desc);
		int argCount = argumentTypes.length;

		InsnList insnList = new InsnList();

		insnList.add(new LdcInsnNode(Type.getObjectType(classNode.name)));

		insnList.add(new LdcInsnNode(method.name));

		insnList.add(new LdcInsnNode(argCount));
		insnList.add(new TypeInsnNode(Opcodes.ANEWARRAY, Class_name));
		for (int i = 0; i < argCount; i++) {
			insnList.add(new InsnNode(DUP));
			insnList.add(new LdcInsnNode(i));
			insnList.add(pushType(argumentTypes[i]));
			insnList.add(new InsnNode(AASTORE));
		}

		insnList.add(new MethodInsnNode(INVOKESTATIC, Types_name, GET_DECLARED_METHOD, Types_getDeclaredMethod_descriptor, false));
		return insnList;
	}

	private List<MethodNode> getSnapshotMethods(ClassNode classNode) {
		return classNode.methods.stream()
			.filter(method -> isSnapshotMethod(method))
			.collect(toList());
	}

	private boolean isSnapshotMethod(MethodNode method) {
		if (method.visibleAnnotations == null) {
			return false;
		}
		return method.visibleAnnotations.stream()
			.anyMatch(annotation -> annotation.desc.equals(Snapshot_descriptor));
	}

	private List<MethodNode> getInputMethods(ClassNode classNode) {
		return classNode.methods.stream()
			.filter(method -> isInputMethod(method))
			.collect(toList());
	}

	private boolean isInputMethod(MethodNode method) {
		if (method.visibleAnnotations == null) {
			return false;
		}
		return method.visibleAnnotations.stream()
			.anyMatch(annotation -> annotation.desc.equals(SnapshotInput_descriptor));
	}

	private List<MethodNode> getOutputMethods(ClassNode classNode) {
		return classNode.methods.stream()
			.filter(method -> isOutputMethod(method))
			.collect(toList());
	}

	private boolean isOutputMethod(MethodNode method) {
		if (method.visibleAnnotations == null) {
			return false;
		}
		return method.visibleAnnotations.stream()
			.anyMatch(annotation -> annotation.desc.equals(SnapshotOutput_descriptor));
	}

	private TryCatchBlockNode createTryCatchBlock(LabelNode tryLabel, LabelNode catchLabel) {
		return new TryCatchBlockNode(tryLabel, catchLabel, catchLabel, null);
	}

	private InsnList createTry(LabelNode tryLabel, InsnList onBegin) {
		InsnList insnList = new InsnList();
		insnList.add(tryLabel);
		insnList.add(onBegin);
		return insnList;
	}

	private InsnList createCatchFinally(LabelNode catchLabel, InsnList onError, LabelNode finallyLabel, InsnList onSuccess, InsnNode ret) {
		InsnList insnList = new InsnList();
		insnList.add(new JumpInsnNode(GOTO, finallyLabel));
		insnList.add(catchLabel);
		insnList.add(onError);
		insnList.add(new InsnNode(ATHROW));
		insnList.add(finallyLabel);
		insnList.add(onSuccess);
		insnList.add(ret);
		return insnList;
	}

	private InsnList setupVariables(ClassNode classNode, MethodNode methodNode) {
		Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
		List<LocalVariableNode> arguments = range(methodNode.localVariables, 1, argumentTypes.length);

		InsnList insnList = new InsnList();

		insnList.add(new FieldInsnNode(GETSTATIC, SnapshotManager_name, SNAPSHOT_MANAGER_FIELD_NAME, SnaphotManager_descriptor));

		insnList.add(new VarInsnNode(ALOAD, 0));

		insnList.add(new LdcInsnNode(keySignature(classNode, methodNode)));

		insnList.add(pushAsArray(arguments, argumentTypes));

		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, SETUP_VARIABLES, SnaphotManager_setupVariables_descriptor, false));

		return insnList;
	}

	private InsnList expectVariables(ClassNode classNode, MethodNode methodNode) {
		Type returnType = Type.getReturnType(methodNode.desc);
		Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
		List<LocalVariableNode> arguments = range(methodNode.localVariables, 1, argumentTypes.length);

		InsnList insnList = new InsnList();
		int newLocal = methodNode.maxLocals;

		if (returnType.getSize() > 0) {
			insnList.add(memorizeLocal(returnType, newLocal));
		}

		insnList.add(new FieldInsnNode(GETSTATIC, SnapshotManager_name, SNAPSHOT_MANAGER_FIELD_NAME, SnaphotManager_descriptor));

		insnList.add(new VarInsnNode(ALOAD, 0));

		if (returnType.getSize() > 0) {
			insnList.add(recallLocal(newLocal));
		}
		insnList.add(pushAsArray(arguments, argumentTypes));

		if (returnType.getSize() > 0) {
			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, EXPECT_VARIABLES, SnaphotManager_expectVariablesResult_descriptor, false));
		} else {
			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, EXPECT_VARIABLES, SnaphotManager_expectVariablesNoResult_descriptor, false));
		}

		return insnList;
	}

	private InsnList throwVariables(ClassNode classNode, MethodNode methodNode) {
		Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
		List<LocalVariableNode> arguments = range(methodNode.localVariables, 1, argumentTypes.length);

		InsnList insnList = new InsnList();

		insnList.add(new InsnNode(DUP));

		insnList.add(new FieldInsnNode(GETSTATIC, SnapshotManager_name, SNAPSHOT_MANAGER_FIELD_NAME, SnaphotManager_descriptor));

		insnList.add(new InsnNode(SWAP));

		insnList.add(new VarInsnNode(ALOAD, 0));

		insnList.add(new InsnNode(SWAP));

		insnList.add(pushAsArray(arguments, argumentTypes));

		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, THROW_VARIABLES, SnaphotManager_throwVariables_descriptor, false));

		return insnList;
	}

	private InsnList notifyInput(ClassNode classNode, MethodNode methodNode) {
		Type returnType = Type.getReturnType(methodNode.desc);
		Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
		List<LocalVariableNode> arguments = range(methodNode.localVariables, 1, argumentTypes.length);

		InsnList insnList = new InsnList();
		int newLocal = methodNode.maxLocals;

		if (returnType.getSize() > 0) {
			insnList.add(memorizeLocal(returnType, newLocal));
		}

		LabelNode skip = new LabelNode();
		LabelNode done = new LabelNode();

		insnList.add(new FieldInsnNode(GETSTATIC, SnapshotManager_name, SNAPSHOT_MANAGER_FIELD_NAME, SnaphotManager_descriptor));

		insnList.add(new InsnNode(DUP));
		insnList.add(new JumpInsnNode(IFNULL, skip));

		insnList.add(new LdcInsnNode(Type.getObjectType(classNode.name)));
		insnList.add(new LdcInsnNode(methodNode.name));
		if (returnType.getSize() > 0) {
			insnList.add(pushType(returnType));
			insnList.add(recallLocal(newLocal));
		}
		insnList.add(pushTypes(argumentTypes));
		insnList.add(pushAsArray(arguments, argumentTypes));
		if (returnType.getSize() > 0) {
			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, INPUT_VARIABLES, SnaphotManager_inputVariablesResult_descriptor, false));
		} else {
			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, INPUT_VARIABLES, SnaphotManager_inputVariablesNoResult_descriptor, false));
		}
		insnList.add(new JumpInsnNode(Opcodes.GOTO, done));
		insnList.add(skip);
		insnList.add(new InsnNode(POP));
		insnList.add(done);
		return insnList;
	}

	private InsnList notifyOutput(ClassNode classNode, MethodNode methodNode) {
		Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
		List<LocalVariableNode> arguments = range(methodNode.localVariables, 1, argumentTypes.length);

		InsnList insnList = new InsnList();

		LabelNode skip = new LabelNode();
		LabelNode done = new LabelNode();

		insnList.add(new FieldInsnNode(GETSTATIC, SnapshotManager_name, SNAPSHOT_MANAGER_FIELD_NAME, SnaphotManager_descriptor));

		insnList.add(new InsnNode(DUP));
		insnList.add(new JumpInsnNode(IFNULL, skip));

		insnList.add(new LdcInsnNode(Type.getObjectType(classNode.name)));
		insnList.add(new LdcInsnNode(methodNode.name));
		insnList.add(pushTypes(argumentTypes));
		insnList.add(pushAsArray(arguments, argumentTypes));
		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, OUTPUT_VARIABLES, SnaphotManager_outputVariables_descriptor, false));
		insnList.add(new JumpInsnNode(Opcodes.GOTO, done));
		insnList.add(skip);
		insnList.add(new InsnNode(POP));
		insnList.add(done);
		return insnList;
	}

	private String keySignature(ClassNode classNode, MethodNode methodNode) {
		return classNode.name + ":" + methodNode.name + methodNode.desc;
	}

}
