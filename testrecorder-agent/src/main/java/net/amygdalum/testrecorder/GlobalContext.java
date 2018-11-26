package net.amygdalum.testrecorder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.asm.ByteCode.classFrom;
import static net.amygdalum.testrecorder.util.Types.getDeclaredField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import net.amygdalum.testrecorder.types.SerializationException;

public class GlobalContext {

	private List<FieldDescriptor> globals;

	private Map<ClassLoader, List<Field>> globalFields;

	public GlobalContext() {
		this.globals = new ArrayList<>();
		this.globalFields = new IdentityHashMap<>();
	}

	public List<Field> globals(ClassLoader loader) {
		return globalFields.computeIfAbsent(loader, this::computeGlobalFields);
	}

	private List<Field> computeGlobalFields(ClassLoader loader) {
		return globals.stream()
			.map(descriptor -> descriptor.field(loader))
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

		public Field field(ClassLoader loader) {
			try {
				Class<?> clazz = classFrom(className, loader);
				return getDeclaredField(clazz, fieldName);
			} catch (RuntimeException | ReflectiveOperationException e) {
				throw new SerializationException(e);
			}
		}
	}

}
