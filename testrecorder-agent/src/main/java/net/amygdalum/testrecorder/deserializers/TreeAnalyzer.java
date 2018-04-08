package net.amygdalum.testrecorder.deserializers;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.WorkSet;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedInput;
import net.amygdalum.testrecorder.values.SerializedOutput;

public class TreeAnalyzer {

	private WorkSet<SerializedValue> seed;
	private WorkSet<SerializedReferenceType> todo;
	private Set<Integer> inputs;
	private Set<Integer> outputs;

	public TreeAnalyzer() {
		seed = new WorkSet<>();
		todo = new WorkSet<>();
		inputs = new HashSet<>();
		outputs = new HashSet<>();
	}

	public void addSeed(SerializedValue value) {
		seed.add(value);
	}

	public void addGlobalSeed(SerializedField global) {
		seed.add(global.getValue());
	}

	public void addInputSeed(SerializedInput in) {
		inputs.add(in.getId());
		in.getAllValues().stream()
			.filter(Objects::nonNull)
			.forEach(seed::add);
	}

	public void addOutputSeed(SerializedOutput out) {
		outputs.add(out.getId());
		out.getAllValues().stream()
			.filter(Objects::nonNull)
			.forEach(seed::add);
	}

	public DefaultDeserializerContext analyze(DefaultDeserializerContext context) {
		while (seed.hasMoreElements()) {
			SerializedValue value = seed.remove();
			analyzeValue(context, value);
		}
		while (todo.hasMoreElements()) {
			SerializedReferenceType value = todo.remove();
			value.referencedValues().stream()
				.forEach(referencedValue -> analyzeReference(context, value, referencedValue));
		}
		return context;
	}

	private void analyzeValue(DefaultDeserializerContext context, SerializedValue value) {
		context.staticRef(value);
		if (value instanceof SerializedReferenceType) {
			SerializedReferenceType object = (SerializedReferenceType) value;
			todo.add(object);
		}
	}

	private void analyzeReference(DefaultDeserializerContext context, SerializedReferenceType value, SerializedValue referencedValue) {
		context.ref(value, referencedValue);
		if (referencedValue instanceof SerializedReferenceType) {
			SerializedReferenceType object = (SerializedReferenceType) referencedValue;
			if (inputs.contains(object.getId())) {
				context.staticRef(object);
			}
			if (outputs.contains(object.getId())) {
				context.staticRef(object);
			}
			todo.add(object);
		}
	}

}
