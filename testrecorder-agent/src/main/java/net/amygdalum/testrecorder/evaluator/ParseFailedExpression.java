package net.amygdalum.testrecorder.evaluator;

import java.util.Optional;

import net.amygdalum.testrecorder.types.SerializedValue;

public class ParseFailedExpression implements Expression {

	private String message;

	public ParseFailedExpression(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

	@Override
	public Optional<SerializedValue> evaluate(SerializedValue base) {
		return evaluate(base, null);
	}

	@Override
	public Optional<SerializedValue> evaluate(SerializedValue base, Class<?> type) {
		return Optional.empty();
	}

}
