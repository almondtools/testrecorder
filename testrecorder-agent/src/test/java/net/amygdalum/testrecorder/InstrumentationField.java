package net.amygdalum.testrecorder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class InstrumentationField {
	public ClassNode classNode;
	public FieldNode fieldNode;

	public InstrumentationField(ClassNode classNode, FieldNode fieldNode) {
		this.classNode = classNode;
		this.fieldNode = fieldNode;
	}

	public static InstrumentationField instrumentField(Class<?> clazz, String fieldName) throws IOException, NoSuchMethodException {
		Field field = Arrays.stream(clazz.getDeclaredFields())
			.filter(m -> m.getName().equals(fieldName))
			.findFirst()
			.orElse(null);
		String className = Type.getInternalName(clazz);
		String fieldDesc = Type.getDescriptor(field.getType());
	
		ClassReader cr = new ClassReader(className);
		ClassNode classNode = new ClassNode();
	
		cr.accept(classNode, 0);
	
		FieldNode fieldNode = classNode.fields.stream()
			.filter(f -> f.name.equals(fieldName) && f.desc.equals(fieldDesc))
			.findFirst()
			.orElseThrow(() -> new NoSuchMethodException(fieldName + fieldDesc));
	
		return new InstrumentationField(classNode, fieldNode);
	}

}