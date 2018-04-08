package net.amygdalum.testrecorder.evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.function.Function;

import net.amygdalum.testrecorder.types.SerializedValue;

public class SerializedValueEvaluator {

    private List<Expression> parsed;

    public SerializedValueEvaluator(String expression) {
        this.parsed = parse(expression);
    }
    
    private static List<Expression> parse(String expression) {
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
        return expressions;
    }

    public Optional<SerializedValue> applyTo(SerializedValue value) {
        Optional<SerializedValue> result = Optional.ofNullable(value);
        
        for (Expression expression : parsed) {
            result = result
                .map(v -> expression.evaluate(v))
                .filter(v -> v.isPresent())
                .map(v -> v.get());
        }
        
        return result;
    }
}
