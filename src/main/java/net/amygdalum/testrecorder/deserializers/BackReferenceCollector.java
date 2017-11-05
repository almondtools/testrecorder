package net.amygdalum.testrecorder.deserializers;

import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.util.WorkSet;
import net.amygdalum.testrecorder.values.SerializedField;

public class BackReferenceCollector {

	private WorkSet<SerializedField> globalTodo;
	private WorkSet<SerializedReferenceType> todo;

	public BackReferenceCollector() {
		globalTodo = new WorkSet<>();
		todo = new WorkSet<>();
	}
	
	public void addSeed(SerializedValue value) {
		if (value instanceof SerializedReferenceType) {
			todo.add((SerializedReferenceType) value);
		}
	}

	public void addSeed(SerializedField global) {
		globalTodo.add(global);
	}

	public DeserializerContext walkTree(DeserializerContext context) {
		while (globalTodo.hasMoreElements()) {
			SerializedField next = globalTodo.remove();
			context.staticRef(next.getDeclaringClass(), next);
			SerializedValue value = next.getValue();
			if (value instanceof SerializedReferenceType) {
				todo.add((SerializedReferenceType) value);
			}
		}
		while (todo.hasMoreElements()) {
			SerializedReferenceType next = todo.remove();
			next.referencedValues().stream()
			.filter(referencedValue -> referencedValue instanceof SerializedReferenceType)
			.map(referencedValue -> (SerializedReferenceType) referencedValue)
			.forEach(referencedValue -> {
				context.ref(next, referencedValue);
				todo.add(referencedValue);
			});
		}
		return context;
	}

}
