package net.amygdalum.testrecorder;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.amygdalum.testrecorder.util.ByteCode;
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
				Class<?> clazz = ByteCode.classFromInternalName(className);
				return Types.getDeclaredField(clazz, fieldName);
			} catch (ReflectiveOperationException e) {
				throw new SerializationException(e);
			}
		}
	}

}
