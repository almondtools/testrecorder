package net.amygdalum.testrecorder.deserializers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.amygdalum.testrecorder.types.ContextSnapshot;
import net.amygdalum.testrecorder.types.ReferenceTypeVisitor;
import net.amygdalum.testrecorder.types.SerializedAggregateType;
import net.amygdalum.testrecorder.types.SerializedArgument;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedInput;
import net.amygdalum.testrecorder.types.SerializedOutput;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedResult;
import net.amygdalum.testrecorder.types.SerializedStructuralType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.IdentityWorkSet;

public class TreeAnalyzer implements ReferenceTypeVisitor<Void> {

	private IdentityWorkSet<SerializedReferenceType> todo;
	private Set<Integer> inputs;
	private Set<Integer> outputs;
	private List<TreeAnalysisListener> listeners;

	public TreeAnalyzer() {
		todo = new IdentityWorkSet<>();
		inputs = new HashSet<>();
		outputs = new HashSet<>();
		listeners = new ArrayList<>();
	}

	public TreeAnalyzer addListener(TreeAnalysisListener listener) {
		listeners.add(listener);
		return this;
	}

	private void addThis(SerializedValue value) {
		listeners.forEach(listener -> listener.notifyThis(value));
		analyzeValue(value);
	}

	private void addException(SerializedValue value) {
		listeners.forEach(listener -> listener.notifyThis(value));
		analyzeValue(value);
	}
	
	private void addArgument(SerializedArgument argument) {
		listeners.forEach(listener -> listener.notifyArgument(argument));
		analyzeValue(argument.getValue());
	}

	private void addResult(SerializedResult result) {
		listeners.forEach(listener -> listener.notifyResult(result));
		analyzeValue(result.getValue());
	}

	private void addGlobal(SerializedField field) {
		listeners.forEach(listener -> listener.notifyGlobal(field));
		analyzeValue(field.getValue());
	}

	private void addInput(SerializedInput in) {
		inputs.add(in.getId());
		in.getAllValues().stream()
			.filter(Objects::nonNull)
			.forEach(this::analyzeValue);
	}

	private void addOutput(SerializedOutput out) {
		outputs.add(out.getId());
		out.getAllValues().stream()
			.filter(Objects::nonNull)
			.forEach(this::analyzeValue);
	}

	private void analyzeValue(SerializedValue value) {
		if (value instanceof SerializedReferenceType) {
			SerializedReferenceType object = (SerializedReferenceType) value;
			if (inputs.contains(object.getId())) {
				for (TreeAnalysisListener listener : listeners) {
					listener.notifyInput(value);
				}
			}
			if (outputs.contains(object.getId())) {
				for (TreeAnalysisListener listener : listeners) {
					listener.notifyOutput(value);
				}
			}
			todo.add(object);
		}
	}

	private void analyze() {
		while (todo.hasMoreElements()) {
			SerializedReferenceType object = todo.remove();
			object.accept(this);
		}
	}

	@Override
	public Void visitAggregateType(SerializedAggregateType value) {
		value.elements().stream()
			.peek(ref -> listeners.forEach(listener -> listener.notifyAggregate(value, ref)))
			.forEach(this::analyzeValue);
		return null;
	}

	@Override
	public Void visitStructuralType(SerializedStructuralType value) {
		value.fields().stream()
			.peek(ref -> listeners.forEach(listener -> listener.notifyField(value, ref)))
			.map(ref -> ref.getValue())
			.forEach(this::analyzeValue);
		return null;
	}

	@Override
	public Void visitImmutableType(SerializedImmutableType value) {
		value.referencedValues().stream()
			.peek(ref -> listeners.forEach(listener -> listener.notifyReference(value, ref)))
			.forEach(this::analyzeValue);
		return null;
	}

	public void analyze(ContextSnapshot snapshot) {

		snapshot.onSetupThis().ifPresent(self -> addThis(self));
		snapshot.onExpectThis().ifPresent(self -> addThis(self));

		snapshot.streamSetupArgs()
			.forEach(arg -> addArgument(arg));
		snapshot.streamExpectArgs()
			.forEach(arg -> addArgument(arg));

		snapshot.onExpectResult()
			.ifPresent(result -> addResult(result));

		snapshot.onExpectException()
			.ifPresent(exception -> addException(exception));

		snapshot.streamSetupGlobals()
			.filter(Objects::nonNull)
			.forEach(global -> addGlobal(global));
		snapshot.streamExpectGlobals()
			.filter(Objects::nonNull)
			.forEach(global -> addGlobal(global));

		snapshot.streamInput()
			.forEach(input -> addInput(input));

		snapshot.streamOutput()
			.forEach(output -> addOutput(output));

		analyze();
	}

}
