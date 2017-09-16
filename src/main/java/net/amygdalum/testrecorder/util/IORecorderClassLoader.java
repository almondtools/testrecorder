package net.amygdalum.testrecorder.util;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.ByteCode.pushAsArray;
import static net.amygdalum.testrecorder.ByteCode.range;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.INSTANCEOF;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.POP;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import net.amygdalum.testrecorder.ByteCode;

public class IORecorderClassLoader extends AbstractInstrumentedClassLoader {

	private static final List<String> UNSUPPORTED_PACKAGE_PREFIXES = asList("java","org.junit","org.hamcrest","net.amygdalum.testrecorder.util");

	private static final String Class_name = Type.getInternalName(Class.class);
    private static final String IORecorderClassLoader_name = Type.getInternalName(IORecorderClassLoader.class);
    private static final String OutputListener_name = Type.getInternalName(OutputListener.class);

    private static final String Class_getClassLoader_descriptor = ByteCode.methodDescriptor(Class.class, "getClassLoader");

    private static final String IORecorderClassLoader_getOut_descriptor = ByteCode.methodDescriptor(IORecorderClassLoader.class, "getOut");
    
    private static final String OutputListener_notifyOutput_descriptor = ByteCode.methodDescriptor(OutputListener.class, "notifyOutput", Class.class, String.class, Object[].class);

    private OutputListener out;
    private String root;
    private Set<String> classes;

    public IORecorderClassLoader(Class<?> clazz, OutputListener out, Set<String> classes) {
        super(clazz.getClassLoader());
        this.root = clazz.getName();
        this.out = out;
        this.classes = classes;
        adoptInstrumentations(clazz.getClassLoader());
    }

    private final void adoptInstrumentations(ClassLoader loader) {
        if (loader instanceof ClassInstrumenting) {
            Map<String, byte[]> instrumentations = ((ClassInstrumenting) loader).getInstrumentations();
            for (Map.Entry<String, byte[]> instrumentation : instrumentations.entrySet()) {
                String name = instrumentation.getKey();
                byte[] bytes = instrumentation.getValue();
                if (!classes.contains(name) && findLoadedClass(name) == null) {
                    define(name, bytes);
                }
            }
        }
    }

    public OutputListener getOut() {
        return out;
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.startsWith(root)) {
            Class<?> clazz = findLoadedClass(name);
            if (clazz != null) {
                return clazz;
            } else {
                return findClass(name);
            }
        }
        if (isInstrumented(name)) {
            return findLoadedClass(name);
        } else if (instrumentationNotSupported(name)) {
        	return super.loadClass(name);
        } else {
            return defineInstrumented(name);
        }
    }

	private boolean instrumentationNotSupported(String name) {
		if (classes.contains(name)) {
			return false;
		}
		for (String prefix : UNSUPPORTED_PACKAGE_PREFIXES) {
			if (name.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	private Class<?> defineInstrumented(String name) throws ClassNotFoundException {
		try {
            byte[] bytes = instrument(name);
            return define(name, bytes);
        } catch (Throwable t) {
            throw new ClassNotFoundException(t.getMessage(), t);
        }
	}

    public byte[] instrument(String className) throws IOException {
        return instrument(new ClassReader(className));
    }

    public byte[] instrument(ClassReader cr) {
        ClassNode classNode = new ClassNode();

        cr.accept(classNode, 0);

        instrumentOutputMethods(classNode);

        ClassWriter out = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(out);
        return out.toByteArray();
    }

    private void instrumentOutputMethods(ClassNode classNode) {
        for (MethodNode method : getOutputMethods(classNode)) {
            method.instructions.insert(notifyOutput(classNode, method));
        }
    }

    private List<MethodNode> getOutputMethods(ClassNode classNode) {
        return classNode.methods.stream()
            .filter(methodNode -> out.matches(signature(classNode, methodNode)))
            .collect(toList());
    }

    private String signature(ClassNode classNode, MethodNode methodNode) {
        String string = classNode.name.replace('/', '.') + "." + methodNode.name + Arrays.stream(Type.getArgumentTypes(methodNode.desc))
            .map(type -> type.getClassName() != null ? type.getClassName() : type.getInternalName() != null ? type.getInternalName().replace('/', '.') : "void")
            .collect(joining(",", "(", ")"));
        return string;
    }

    private InsnList notifyOutput(ClassNode classNode, MethodNode methodNode) {
        int localVariableIndex = ((methodNode.access & ACC_STATIC) == 0) ? 1 : 0;

        Type[] argumentTypes = Type.getArgumentTypes(methodNode.desc);
        List<LocalVariableNode> arguments = range(methodNode.localVariables, localVariableIndex, argumentTypes.length);

        InsnList insnList = new InsnList();

        LabelNode skip = new LabelNode();
        LabelNode done = new LabelNode();

        insnList.add(new LdcInsnNode(Type.getObjectType(classNode.name)));
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, Class_name, "getClassLoader",
            Class_getClassLoader_descriptor, false));

        insnList.add(new InsnNode(DUP));
        insnList.add(new JumpInsnNode(IFNULL, skip));

        insnList.add(new InsnNode(DUP));
        insnList.add(new TypeInsnNode(INSTANCEOF, IORecorderClassLoader_name));
        insnList.add(new JumpInsnNode(IFEQ, skip));
        insnList.add(new TypeInsnNode(CHECKCAST, IORecorderClassLoader_name));

        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, IORecorderClassLoader_name, "getOut", IORecorderClassLoader_getOut_descriptor, false));

        insnList.add(new InsnNode(DUP));
        insnList.add(new JumpInsnNode(IFNULL, skip));

        insnList.add(new LdcInsnNode(Type.getObjectType(classNode.name)));
        insnList.add(new LdcInsnNode(methodNode.name));
        insnList.add(pushAsArray(arguments, argumentTypes));
        insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, OutputListener_name, "notifyOutput", OutputListener_notifyOutput_descriptor, true));

        insnList.add(new JumpInsnNode(Opcodes.GOTO, done));
        insnList.add(skip);
        insnList.add(new InsnNode(POP));
        insnList.add(done);
        return insnList;
    }

}