package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import java.io.PrintWriter;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

public class CaptureCallTest {

	private MethodContext context;

	@BeforeEach
	void before() {
		context = new MethodContext(AClass.classNode(), AClass.staticMethodNode());
	}

	@Nested
	class testCaptureCall {
		@Test
		void onVirtualMethodCall() throws Exception {
			Class<PrintWriter> clazz = PrintWriter.class;
			Method method = clazz.getMethod("write", String.class, int.class, int.class);
			MethodInsnNode call = new MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(clazz), "write", Type.getMethodDescriptor(method), false);
			InsnList insns = new CaptureCall(call, "base", "args")
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					// backup stack
					"ISTORE 3",
					"ISTORE 2",
					"ASTORE 1",
					// this
					"DUP",
					"ASTORE 0",
					// arguments
					"LDC 3",
					"ANEWARRAY java/lang/Object",
					"DUP",
					// first argument (String)
					"LDC 0",
					"ALOAD 1",
					"AASTORE",
					// second argument (int)
					"DUP",
					"LDC 1",
					"ILOAD 2",
					"INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;",
					"AASTORE",
					// third argument (int)
					"DUP",
					"LDC 2",
					"ILOAD 3",
					"INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;",
					"AASTORE",
					// save arguments
					"ASTORE 4",
					// restore stack
					"ALOAD 1",
					"ILOAD 2",
					"ILOAD 3");
			assertThat(context.local("base").index).isEqualTo(0);
			assertThat(context.local("args").index).isEqualTo(4);
		}

		@Test
		void onStaticMethodCall() throws Exception {
			Class<System> clazz = System.class;
			Method method = clazz.getMethod("arraycopy", Object.class, int.class, Object.class, int.class, int.class);
			MethodInsnNode call = new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(clazz), "write", Type.getMethodDescriptor(method), false);
			InsnList insns = new CaptureCall(call, "base", "args")
				.build(context);

			assertThat(ByteCode.toString(insns))
				.containsExactly(
					// backup stack
					"ISTORE 5",
					"ISTORE 4",
					"ASTORE 3",
					"ISTORE 2",
					"ASTORE 1",
					// store static base class
					"LDC Ljava/lang/System;.class",
					"ASTORE 0",
					// arguments
					"LDC 5",
					"ANEWARRAY java/lang/Object",
					// first argument (array/object)
					"DUP",
					"LDC 0",
					"ALOAD 1",
					"AASTORE",
					// second argument (int)
					"DUP",
					"LDC 1",
					"ILOAD 2",
					"INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;",
					"AASTORE",
					// third argument (array/object)
					"DUP",
					"LDC 2",
					"ALOAD 3",
					"AASTORE",
					// second argument (int)
					"DUP",
					"LDC 3",
					"ILOAD 4",
					"INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;",
					"AASTORE",
					// second argument (int)
					"DUP",
					"LDC 4",
					"ILOAD 5",
					"INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;",
					"AASTORE",
					// save arguments
					"ASTORE 6",
					// restore stack
					"ALOAD 1",
					"ILOAD 2",
					"ALOAD 3",
					"ILOAD 4",
					"ILOAD 5");
			assertThat(context.local("base").index).isEqualTo(0);
			assertThat(context.local("args").index).isEqualTo(6);
		}
	}
}
