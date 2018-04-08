package net.amygdalum.testrecorder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.asm.ByteCode.classFrom;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.amygdalum.testrecorder.types.SerializationException;
import net.amygdalum.testrecorder.util.Types;

public class GlobalContext {

	private List<FieldDescriptor> globals;

	private List<Field> globalFields;

	public GlobalContext() {
		this.globals = new ArrayList<>();
	}

	public List<Field> globals() {
		if (globalFields == null) {
			globalFields = computeGlobalFields();
		}
		return globalFields;
	}

	private List<Field> computeGlobalFields() {
		return globals.stream()
			.map(FieldDescriptor::field)
			.distinct()
			.collect(toList());
	}

	public void add(String className, String fieldName) {
		globals.add(new FieldDescriptor(className, fieldName));
	}

	public static class FieldDescriptor {

		public String className;
		public String fieldName;

		public FieldDescriptor(String className, String fieldName) {
			this.className = className;
			this.fieldName = fieldName;
		}

		public Field field() {
			try {
				Class<?> clazz = classFrom(className);
				return Types.getDeclaredField(clazz, fieldName);
			} catch (RuntimeException | ReflectiveOperationException e) {
				throw new SerializationException(e);
			}
		}
	}

}
