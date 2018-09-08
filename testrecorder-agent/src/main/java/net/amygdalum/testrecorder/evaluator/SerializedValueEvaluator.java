package net.amygdalum.testrecorder.evaluator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import net.amygdalum.testrecorder.types.SerializedValue;

public class SerializedValueEvaluator {

	private Expression[] parsed;
	private Class<?> type;

	public SerializedValueEvaluator(String expression) {
		this.parsed = parse(expression);
	}

	public SerializedValueEvaluator(String expression, Class<?> type) {
		this.parsed = parse(expression);
		this.type = type;
	}

	private static Expression[] parse(String expression) {
		try {
			List<Expression> expressions = new ArrayList<>();
			StringTokenizer tokenizer = new StringTokenizer(expression, ".[]", true);

			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (".".equals(token)) {
					expressions.add(new FieldExpression(nextString(tokenizer, "<field>")));
				} else if ("[".equals(token)) {
					expressions.add(new IndexExpression(nextInt(tokenizer, "<index>")));
					String nextToken = nextString(tokenizer, "']'");
					if (!nextToken.equals("]")) {
						throw new ParseException("expecting ']', but found: '" + token + "'", 0);
					}
				} else {
					throw new ParseException("expecting '.' or '[', but found: '" + token + "'", 0);
				}
			}
			return expressions.toArray(new Expression[expressions.size()]);
		} catch (ParseException e) {
			return new Expression[] { new ParseFailedExpression(e.getMessage()) };
		}
	}

	private static String nextString(StringTokenizer tokenizer, String expression) throws ParseException {
		if (tokenizer.hasMoreTokens()) {
			return tokenizer.nextToken();
		}
		throw new ParseException("expecting " + expression + ", but nothing found", 0);
	}

	private static int nextInt(StringTokenizer tokenizer, String expression) throws ParseException {
		if (tokenizer.hasMoreTokens()) {
			String nextToken = tokenizer.nextToken();
			try {
				return Integer.parseInt(nextToken);
			} catch (NumberFormatException e) {
				throw new ParseException("expecting " + expression + ", found:'" + nextToken + "'", 0);
			}
		}
		throw new ParseException("expecting " + expression + ", but nothing found", 0);
	}

	public Optional<SerializedValue> applyTo(SerializedValue value) {
		Optional<SerializedValue> result = Optional.ofNullable(value);
		if (parsed.length == 0) {
			if (type != null) {
				result = result.filter(v -> type.isAssignableFrom(v.getType()));
			}
			return result;
		}

		int last = parsed.length - 1;
		for (int i = 0; i < last; i++) {
			Expression expression = parsed[i];
			result = result.flatMap(expression::evaluate);
		}
		Expression expression = parsed[last];
		if (type == null) {
			result = result.flatMap(expression::evaluate);
		} else {
			result = result.flatMap(v -> expression.evaluate(v, type));
		}

		return result;
	}

	public Optional<ParseFailedExpression> error() {
		if (parsed.length > 0 && parsed[0] instanceof ParseFailedExpression) {
			return Optional.of((ParseFailedExpression) parsed[0]);
		}
		return Optional.empty();
	}
}
