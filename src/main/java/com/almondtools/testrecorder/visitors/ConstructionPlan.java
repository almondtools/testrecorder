package com.almondtools.testrecorder.visitors;

import static com.almondtools.testrecorder.TypeHelper.getBestName;
import static com.almondtools.testrecorder.TypeHelper.getSimpleName;
import static com.almondtools.testrecorder.visitors.Templates.assignStatement;
import static com.almondtools.testrecorder.visitors.Templates.callMethodStatement;
import static com.almondtools.testrecorder.visitors.Templates.newObject;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.almondtools.testrecorder.SerializedValueVisitor;

public class ConstructionPlan {

	private String name;
	private ConstructorParams constructorParams;
	private List<SetterParam> setterParams;

	public ConstructionPlan(String name, ConstructorParams constructorParams, List<SetterParam> setterParams) {
		this.name = name;
		this.constructorParams = constructorParams;
		this.setterParams = setterParams;
	}

	public Object execute() {
		try {
			Object base = constructorParams.apply();
			for (SetterParam param : setterParams) {
				param.apply(base);
			}
			return base;
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}

	public Computation compute(SerializedValueVisitor<Computation> compiler) {
		Class<?> clazz = constructorParams.getType();
		List<String> statements = new ArrayList<>();

		List<Computation> computedParams = constructorParams.getValues().stream()
			.map(value -> value.accept(compiler))
			.collect(toList());

		statements.addAll(computedParams.stream()
			.flatMap(computation -> computation.getStatements().stream())
			.collect(toList()));

		String[] params = computedParams.stream()
			.map(computation -> computation.getValue())
			.toArray(String[]::new);

		String bean = newObject(getBestName(clazz), params);
		String constructorStatement = assignStatement(getSimpleName(clazz), name, bean);
		statements.add(constructorStatement);

		for (SetterParam param : setterParams) {
			Computation fieldComputation = param.computeValue().accept(compiler);
			statements.addAll(fieldComputation.getStatements());

			String setStatement = callMethodStatement(name, param.getName(), fieldComputation.getValue());
			statements.add(setStatement);
		}

		return new Computation(name, true, statements);
	}
}
