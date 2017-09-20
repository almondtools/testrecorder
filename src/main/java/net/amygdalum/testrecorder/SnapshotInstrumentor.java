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
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SWAP;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
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

import net.amygdalum.testrecorder.SerializationProfile.Global;
import net.amygdalum.testrecorder.SerializationProfile.Input;
import net.amygdalum.testrecorder.SerializationProfile.Output;
import net.amygdalum.testrecorder.util.Types;

public class SnapshotInstrumentor implements ClassFileTransformer {

	private static final String STATIC_INIT_NAME = "<clinit>";

	private static final String GET_DECLARED_METHOD = "getDeclaredMethod";
	private static final String GET_DECLARED_FIELD = "getDeclaredField";

	public static final String SNAPSHOT_MANAGER_FIELD_NAME = "MANAGER";
	private static final String REGISTER = "register";
	private static final String REGISTER_GLOBAL = "registerGlobal";
	private static final String SETUP_VARIABLES = "setupVariables";
	private static final String INPUT_VARIABLES = "inputVariables";
	private static final String OUTPUT_VARIABLES = "outputVariables";
	private static final String THROW_VARIABLES = "throwVariables";
	private static final String EXPECT_VARIABLES = "expectVariables";

	private static final String Class_name = Type.getInternalName(Class.class);
	private static final String Types_name = Type.getInternalName(Types.class);
	private static final String SnapshotManager_name = Type.getInternalName(SnapshotManager.class);

	private static final String SnaphotManager_descriptor = Type.getDescriptor(SnapshotManager.class);
	private static final String Recorded_descriptor = Type.getDescriptor(Recorded.class);
	private static final String Input_descriptor = Type.getDescriptor(Input.class);
	private static final String Output_descriptor = Type.getDescriptor(Output.class);
	private static final String Global_descriptor = Type.getDescriptor(Global.class);

	private static final String SnaphotManager_registerMethod_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, REGISTER, String.class, Method.class);
	private static final String SnaphotManager_registerGlobal_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, REGISTER_GLOBAL, String.class, Field.class);
	private static final String SnaphotManager_setupVariables_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, SETUP_VARIABLES, Object.class, String.class, Object[].class);
	private static final String SnaphotManager_expectVariablesResult_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, EXPECT_VARIABLES, Object.class, Object.class, Object[].class);
	private static final String SnaphotManager_expectVariablesNoResult_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, EXPECT_VARIABLES, Object.class, Object[].class);
	private static final String SnaphotManager_throwVariables_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, THROW_VARIABLES, Object.class, Throwable.class, Object[].class);
	private static final String SnaphotManager_outputVariablesResult_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, OUTPUT_VARIABLES, Object.class, String.class,
		java.lang.reflect.Type.class, Object.class, java.lang.reflect.Type[].class, Object[].class);
	private static final String SnaphotManager_outputVariablesNoResult_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, OUTPUT_VARIABLES, Object.class, String.class,
		java.lang.reflect.Type[].class, Object[].class);
	private static final String SnaphotManager_inputVariablesResult_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, INPUT_VARIABLES, Object.class, String.class,
		java.lang.reflect.Type.class, Object.class, java.lang.reflect.Type[].class, Object[].class);
	private static final String SnaphotManager_inputVariablesNoResult_descriptor = ByteCode.methodDescriptor(SnapshotManager.class, INPUT_VARIABLES, Object.class, String.class,
		java.lang.reflect.Type[].class, Object[].class);

	private static final String Types_getDeclaredMethod_descriptor = ByteCode.methodDescriptor(Types.class, GET_DECLARED_METHOD, Class.class, String.class, Class[].class);
	private static final String Types_getDeclaredField_descriptor = ByteCode.methodDescriptor(Types.class, GET_DECLARED_FIELD, Class.class, String.class);

	private TestRecorderAgentConfig config;
	private Map<String, ClassNode> classCache;

	public SnapshotInstrumentor(TestRecorderAgentConfig config) {
		this.config = config;
		this.classCache = new HashMap<>();
		SnapshotManager.init(config);
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if (className == null) {
			return null;
		}
		for (Packages pkg : config.getPackages()) {
			int lastDot = className.lastIndexOf('/');
			String pkgName = className.substring(0, lastDot).replace('/', '.');
			if (pkg.matches(pkgName)) {
				System.out.println("recording snapshots of " + className);
				return instrument(classfileBuffer);
			}
		}
		return null;
	}

	private ClassNode fetchClassNode(String className) throws IOException {
		ClassNode classNode = classCache.get(className);
		if (classNode == null) {
			ClassReader cr = new ClassReader(className);
			classNode = new ClassNode();

			cr.accept(classNode, 0);
			classCache.put(className, classNode);
		}
		return classNode;
	}

	private ClassNode fetchClassNode(byte[] buffer) {
		ClassReader cr = new ClassReader(buffer);
		ClassNode classNode = new ClassNode();

		cr.accept(classNode, 0);
		classCache.put(classNode.name, classNode);
		return classNode;
	}

	private MethodNode fetchMethodNode(String className, String methodName, String methodDesc) throws IOException, NoSuchMethodException {
		ClassNode classNode = fetchClassNode(className);
		return classNode.methods.stream()
			.filter(method -> method.name.equals(methodName) && method.desc.equals(methodDesc))
			.findFirst()
			.orElseThrow(() -> new NoSuchMethodException(methodName + methodDesc));
	}

	public byte[] instrument(String className) throws IOException {
		return instrument(fetchClassNode(className));
	}

	public byte[] instrument(byte[] buffer) {
		return instrument(fetchClassNode(buffer));
	}

	public byte[] instrument(ClassNode classNode) {
		if (isClass(classNode)) {

			logSkippedSnapshotMethods(classNode);

			instrumentStaticInitializer(classNode);

			instrumentSnapshotMethods(classNode);

			instrumentInputCalls(classNode);

			instrumentOutputCalls(classNode);
		}

		ClassWriter out = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(out);
		return out.toByteArray();
	}

	private boolean isClass(ClassNode classNode) {
		return (classNode.access & (ACC_INTERFACE | ACC_ANNOTATION)) == 0;
	}

	private void logSkippedSnapshotMethods(ClassNode classNode) {
		for (MethodNode methodNode : getSkippedSnapshotMethods(classNode)) {
			System.out.println("method " + Type.getMethodType(methodNode.desc).getDescriptor() + " in " + Type.getType(classNode.name) + " is not accessible, skipping");
		}
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
		for (FieldNode fieldNode : getGlobalFields(classNode)) {
			insnList.add(new InsnNode(DUP));
			insnList.add(new LdcInsnNode(fieldNode.name));

			insnList.add(pushField(classNode, fieldNode));

			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, REGISTER_GLOBAL, SnaphotManager_registerGlobal_descriptor, false));
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

	private void instrumentInputCalls(ClassNode classNode) {
		for (MethodNode method : classNode.methods) {
			if (!isInputMethod(classNode.name, method)) {
				List<MethodInsnNode> inputCalls = stream(method.instructions.iterator())
					.filter(node -> node instanceof MethodInsnNode)
					.map(node -> (MethodInsnNode) node)
					.filter(node -> isInputCall(node))
					.collect(toList());
				for (MethodInsnNode inputCall : inputCalls) {
					AbstractInsnNode location = inputCall.getPrevious();
					method.instructions.remove(inputCall);
					InsnList inputCallWrapped = wrapInputCall(method, inputCall);
					if (location == null) {
						method.instructions.insert(inputCallWrapped);
					} else {
						method.instructions.insert(location, inputCallWrapped);
					}
				}
			}
		}
	}

	private InsnList wrapInputCall(MethodNode method, MethodInsnNode inputCall) {
		Type ownerType = Type.getObjectType(inputCall.owner);
		Type methodType = Type.getMethodType(inputCall.desc);
		Type[] argumentTypes = methodType.getArgumentTypes();
		Type[] returnType = methodType.getReturnType().getSize() == 0 ? new Type[0] : new Type[] {methodType.getReturnType()};

		InsnList insnList = new InsnList();

		int thisVar = method.maxLocals++;
		if (inputCall.getOpcode() == INVOKESTATIC) {
			insnList.add(new LdcInsnNode(ownerType));
			insnList.add(new VarInsnNode(ASTORE, thisVar));
		} else {
			insnList.add(new InsnNode(DUP));
			insnList.add(new VarInsnNode(ASTORE, thisVar));
		}
		
		int[] argumentVars = new int[argumentTypes.length];
		int[] returnVars = new int[returnType.length];
		
		for (int i = 0; i < argumentVars.length; i++) {
			Type type = argumentTypes[i];
			int newLocal = method.maxLocals++;
			argumentVars[i] = newLocal;
			int storecode = type.getOpcode(ISTORE);
			insnList.insert(new VarInsnNode(storecode, newLocal));
			int loadcode = type.getOpcode(ILOAD);
			insnList.add(new VarInsnNode(loadcode, newLocal));
		}
		insnList.add(inputCall);

		if (returnVars.length >= 1) {
			Type type = returnType[0];
			int newLocal = method.maxLocals++;
			returnVars[0] = newLocal;
			insnList.add(memorizeLocal(type, newLocal));
		}
		
		insnList.add(new FieldInsnNode(GETSTATIC, SnapshotManager_name, SNAPSHOT_MANAGER_FIELD_NAME, SnaphotManager_descriptor));

		insnList.add(new VarInsnNode(ALOAD, thisVar));
		insnList.add(new LdcInsnNode(inputCall.name));
		for (int i = 0; i < returnType.length; i++) {
			Type type = returnType[i];
			int result = returnVars[i];
			insnList.add(pushType(type));
			insnList.add(recallLocal(result));
		}
		insnList.add(pushTypes(argumentTypes));
		insnList.add(pushAsArray(argumentVars, argumentTypes));
		if (returnType.length > 0) {
			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, INPUT_VARIABLES, SnaphotManager_inputVariablesResult_descriptor, false));
		} else {
			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, INPUT_VARIABLES, SnaphotManager_inputVariablesNoResult_descriptor, false));
		}

		return insnList;
	}

	private void instrumentOutputCalls(ClassNode classNode) {
		for (MethodNode method : classNode.methods) {
			if (!isOutputMethod(classNode.name, method)) {
				List<MethodInsnNode> outputCalls = stream(method.instructions.iterator())
					.filter(node -> node instanceof MethodInsnNode)
					.map(node -> (MethodInsnNode) node)
					.filter(node -> isOutputCall(node))
					.collect(toList());
				for (MethodInsnNode outputCall : outputCalls) {
					AbstractInsnNode location = outputCall.getPrevious();
					method.instructions.remove(outputCall);
					InsnList outputCallWrapped = wrapOutputCall(method, outputCall);
					if (location == null) {
						method.instructions.insert(outputCallWrapped);
					} else {
						method.instructions.insert(location, outputCallWrapped);
					}
				}
			}
		}
	}

	private InsnList wrapOutputCall(MethodNode method, MethodInsnNode inputCall) {
		Type ownerType = Type.getObjectType(inputCall.owner);
		Type methodType = Type.getMethodType(inputCall.desc);
		Type[] argumentTypes = methodType.getArgumentTypes();
		Type[] returnType = methodType.getReturnType().getSize() == 0 ? new Type[0] : new Type[] {methodType.getReturnType()};

		InsnList insnList = new InsnList();

		int thisVar = method.maxLocals++;
		if (inputCall.getOpcode() == INVOKESTATIC) {
			insnList.add(new LdcInsnNode(ownerType));
			insnList.add(new VarInsnNode(ASTORE, thisVar));
		} else {
			insnList.add(new InsnNode(DUP));
			insnList.add(new VarInsnNode(ASTORE, thisVar));
		}
		
		int[] argumentVars = new int[argumentTypes.length];
		int[] returnVars = new int[returnType.length];
		
		for (int i = 0; i < argumentVars.length; i++) {
			Type type = argumentTypes[i];
			int newLocal = method.maxLocals++;
			argumentVars[i] = newLocal;
			int storecode = type.getOpcode(ISTORE);
			insnList.insert(new VarInsnNode(storecode, newLocal));
			int loadcode = type.getOpcode(ILOAD);
			insnList.add(new VarInsnNode(loadcode, newLocal));
		}
		insnList.add(inputCall);

		if (returnVars.length >= 1) {
			Type type = returnType[0];
			int newLocal = method.maxLocals++;
			returnVars[0] = newLocal;
			insnList.add(memorizeLocal(type, newLocal));
		}
		
		insnList.add(new FieldInsnNode(GETSTATIC, SnapshotManager_name, SNAPSHOT_MANAGER_FIELD_NAME, SnaphotManager_descriptor));

		insnList.add(new VarInsnNode(ALOAD, thisVar));
		insnList.add(new LdcInsnNode(inputCall.name));
		for (int i = 0; i < returnType.length; i++) {
			Type type = returnType[i];
			int result = returnVars[i];
			insnList.add(pushType(type));
			insnList.add(recallLocal(result));
		}
		insnList.add(pushTypes(argumentTypes));
		insnList.add(pushAsArray(argumentVars, argumentTypes));
		if (returnType.length > 0) {
			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, OUTPUT_VARIABLES, SnaphotManager_outputVariablesResult_descriptor, false));
		} else {
			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, OUTPUT_VARIABLES, SnaphotManager_outputVariablesNoResult_descriptor, false));
		}

		return insnList;
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

	private InsnList pushField(ClassNode classNode, FieldNode field) {
		InsnList insnList = new InsnList();

		insnList.add(new LdcInsnNode(Type.getObjectType(classNode.name)));

		insnList.add(new LdcInsnNode(field.name));

		insnList.add(new MethodInsnNode(INVOKESTATIC, Types_name, GET_DECLARED_FIELD, Types_getDeclaredField_descriptor, false));
		return insnList;
	}

	private List<MethodNode> getSnapshotMethods(ClassNode classNode) {
		if (!isVisible(classNode)) {
			return Collections.emptyList();
		}
		return classNode.methods.stream()
			.filter(method -> isSnapshotMethod(method))
			.filter(method -> isVisible(method))
			.collect(toList());
	}

	private List<FieldNode> getGlobalFields(ClassNode classNode) {
		if (!isVisible(classNode)) {
			return Collections.emptyList();
		}
		return classNode.fields.stream()
			.filter(field -> isGlobalField(classNode.name, field))
			.filter(field -> isVisible(field))
			.collect(toList());
	}

	private List<MethodNode> getSkippedSnapshotMethods(ClassNode classNode) {
		return classNode.methods.stream()
			.filter(method -> isSnapshotMethod(method))
			.filter(method -> !isVisible(classNode) || !isVisible(method))
			.collect(toList());
	}

	private boolean isVisible(ClassNode classNode) {
		if ((classNode.access & ACC_PRIVATE) != 0) {
			return false;
		}
		return classNode.innerClasses.stream()
			.filter(innerClassNode -> innerClassNode.name.equals(classNode.name))
			.map(innerClassNode -> (innerClassNode.access & ACC_PRIVATE) == 0)
			.findFirst()
			.orElse(true);
	}

	private boolean isSnapshotMethod(MethodNode methodNode) {
		if (methodNode.visibleAnnotations == null) {
			return false;
		}
		return methodNode.visibleAnnotations.stream()
			.anyMatch(annotation -> annotation.desc.equals(Recorded_descriptor));
	}

	protected boolean isGlobalField(String className, FieldNode fieldNode) {
		return fieldNode.visibleAnnotations != null && fieldNode.visibleAnnotations.stream()
			.anyMatch(annotation -> annotation.desc.equals(Global_descriptor))
			|| config.getGlobalFields().stream()
				.anyMatch(field -> matches(field, className, fieldNode.name, fieldNode.desc));
	}

	private boolean isVisible(MethodNode methodNode) {
		return (methodNode.access & ACC_PRIVATE) == 0;
	}

	private boolean isVisible(FieldNode fieldNode) {
		return (fieldNode.access & ACC_PRIVATE) == 0;
	}

	protected boolean isInputCall(MethodInsnNode node) {
		for (Methods methods : config.getInputs()) {
			if (methods.matches(node.owner, node.name, node.desc)) {
				return true;
			}
		}
		try {
			MethodNode methodNode = fetchMethodNode(node.owner, node.name, node.desc);
			return isInputMethod(node.owner, methodNode);
		} catch (IOException | NoSuchMethodException e) {
			return false;
		}
	}

	protected boolean isInputMethod(String className, MethodNode methodNode) {
		return methodNode.visibleAnnotations != null && methodNode.visibleAnnotations.stream()
			.anyMatch(annotation -> annotation.desc.equals(Input_descriptor))
			|| config.getInputs().stream()
				.anyMatch(method -> matches(method, className, methodNode.name, methodNode.desc));
	}

	protected boolean isOutputCall(MethodInsnNode node) {
		for (Methods methods : config.getOutputs()) {
			if (methods.matches(node.owner, node.name, node.desc)) {
				return true;
			}
		}
		try {
			MethodNode methodNode = fetchMethodNode(node.owner, node.name, node.desc);
			return isOutputMethod(node.owner, methodNode);
		} catch (IOException | NoSuchMethodException e) {
			return false;
		}
	}

	protected boolean isOutputMethod(String className, MethodNode methodNode) {
		return methodNode.visibleAnnotations != null && methodNode.visibleAnnotations.stream()
			.anyMatch(annotation -> annotation.desc.equals(Output_descriptor))
			|| config.getOutputs().stream()
				.anyMatch(method -> matches(method, className, methodNode.name, methodNode.desc));
	}

	private boolean matches(Fields field, String className, String fieldName, String fieldDescriptor) {
		return field.matches(className, fieldName, fieldDescriptor);
	}

	private boolean matches(Methods method, String className, String methodName, String methodDescriptor) {
		return method.matches(className, methodName, methodDescriptor);
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
		int localVariableIndex = isStatic(methodNode) ? 0 : 1;

		Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
		List<LocalVariableNode> arguments = range(methodNode.localVariables, localVariableIndex, argumentTypes.length);

		InsnList insnList = new InsnList();

		insnList.add(new FieldInsnNode(GETSTATIC, SnapshotManager_name, SNAPSHOT_MANAGER_FIELD_NAME, SnaphotManager_descriptor));

		if (isStatic(methodNode)) {
			insnList.add(new InsnNode(ACONST_NULL));
		} else {
			insnList.add(new VarInsnNode(ALOAD, 0));
		}

		insnList.add(new LdcInsnNode(keySignature(classNode, methodNode)));

		insnList.add(pushAsArray(arguments));

		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, SETUP_VARIABLES, SnaphotManager_setupVariables_descriptor, false));

		return insnList;
	}

	private boolean isStatic(MethodNode methodNode) {
		return (methodNode.access & ACC_STATIC) != 0;
	}

	private InsnList expectVariables(ClassNode classNode, MethodNode methodNode) {
		int localVariableIndex = isStatic(methodNode) ? 0 : 1;

		Type returnType = Type.getReturnType(methodNode.desc);
		Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
		List<LocalVariableNode> arguments = range(methodNode.localVariables, localVariableIndex, argumentTypes.length);

		InsnList insnList = new InsnList();
		int newLocal = methodNode.maxLocals;

		if (returnType.getSize() > 0) {
			insnList.add(memorizeLocal(returnType, newLocal));
		}

		insnList.add(new FieldInsnNode(GETSTATIC, SnapshotManager_name, SNAPSHOT_MANAGER_FIELD_NAME, SnaphotManager_descriptor));

		if (isStatic(methodNode)) {
			insnList.add(new InsnNode(ACONST_NULL));
		} else {
			insnList.add(new VarInsnNode(ALOAD, 0));
		}

		if (returnType.getSize() > 0) {
			insnList.add(recallLocal(newLocal));
		}
		insnList.add(pushAsArray(arguments));

		if (returnType.getSize() > 0) {
			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, EXPECT_VARIABLES, SnaphotManager_expectVariablesResult_descriptor, false));
		} else {
			insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, EXPECT_VARIABLES, SnaphotManager_expectVariablesNoResult_descriptor, false));
		}

		return insnList;
	}

	private InsnList throwVariables(ClassNode classNode, MethodNode methodNode) {
		int localVariableIndex = isStatic(methodNode) ? 0 : 1;

		Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
		List<LocalVariableNode> arguments = range(methodNode.localVariables, localVariableIndex, argumentTypes.length);

		InsnList insnList = new InsnList();

		insnList.add(new InsnNode(DUP));

		insnList.add(new FieldInsnNode(GETSTATIC, SnapshotManager_name, SNAPSHOT_MANAGER_FIELD_NAME, SnaphotManager_descriptor));

		insnList.add(new InsnNode(SWAP));

		if (isStatic(methodNode)) {
			insnList.add(new InsnNode(ACONST_NULL));
		} else {
			insnList.add(new VarInsnNode(ALOAD, 0));
		}

		insnList.add(new InsnNode(SWAP));

		insnList.add(pushAsArray(arguments));

		insnList.add(new MethodInsnNode(INVOKEVIRTUAL, SnapshotManager_name, THROW_VARIABLES, SnaphotManager_throwVariables_descriptor, false));

		return insnList;
	}

	private String keySignature(ClassNode classNode, MethodNode methodNode) {
		return classNode.name + ":" + methodNode.name + methodNode.desc;
	}

}
