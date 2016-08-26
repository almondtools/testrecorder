package net.amygdalum.testrecorder.deserializers;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethodStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;

import java.util.ArrayList;
import java.util.List;

import net.amygdalum.testrecorder.Deserializer;

public class ConstructionPlan {

	private LocalVariable var;
	private ConstructorParams constructorParams;
	private List<SetterParam> setterParams;

	public ConstructionPlan(LocalVariable var, ConstructorParams constructorParams, List<SetterParam> setterParams) {
		this.var = var;
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

	public Computation compute(TypeManager types, Deserializer<Computation> compiler) {
		Class<?> clazz = constructorParams.getType();
		types.registerTypes(clazz);
		
		List<String> statements = new ArrayList<>();

		List<Computation> computedParams = constructorParams.getParams().stream()
			.map(value -> value.compile(types, compiler))
			.collect(toList());

		statements.addAll(computedParams.stream()
			.flatMap(computation -> computation.getStatements().stream())
			.collect(toList()));

		String[] params = computedParams.stream()
			.map(computation -> computation.getValue())
			.toArray(String[]::new);
		
		String bean = newObject(types.getBestName(clazz), params);
		String constructorStatement = assignLocalVariableStatement(types.getSimpleName(clazz), var.getName(), bean);
		statements.add(constructorStatement);
		var.define(clazz);

		for (SetterParam param : setterParams) {
			Computation fieldComputation = param.computeValue().accept(compiler);
			statements.addAll(fieldComputation.getStatements());

			String setStatement = callMethodStatement(var.getName(), param.getName(), fieldComputation.getValue());
			statements.add(setStatement);
		}

		return new Computation(var.getName(), null, true, statements);
	}
}
