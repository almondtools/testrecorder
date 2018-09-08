package net.amygdalum.testrecorder.evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.function.Function;

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
        List<Expression> expressions = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(expression, ".[]", true);
        
        Function<String, Expression> tokenConstructor = null;
        
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (".".equals(token)) {
                tokenConstructor = FieldExpression::new;
            } else if ("[".equals(token)){
                tokenConstructor = IndexExpression::new;
            } else if ("]".equals(token)) {
                tokenConstructor = null;
            } else if (tokenConstructor != null){
                expressions.add(tokenConstructor.apply(token));
                tokenConstructor = null;
            }
        }
        return expressions.toArray(new Expression[expressions.size()]);
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
}
