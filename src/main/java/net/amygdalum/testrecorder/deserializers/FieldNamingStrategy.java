package net.amygdalum.testrecorder.deserializers;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Function;

import net.amygdalum.testrecorder.values.SerializedField;

public final class FieldNamingStrategy {

    private static final List<Function<SerializedField, String>> NAMING_STRATEGIES = asList(qualifySimple(), qualifyCanonical());

    private FieldNamingStrategy() {
    }

    public static List<SerializedField> ensureUniqueNames(List<SerializedField> fields) {
        if (containsUniqueNames(fields)) {
            return fields;
        }
        List<SerializedField> collect = fields.stream()
            .collect(groupingBy(SerializedField::getName, toList())).values().stream()
            .map(equalfields -> applyStrategys(equalfields))
            .flatMap(List::stream)
            .collect(toList());
        return collect;
    }

    private static  List<SerializedField> applyStrategys(List<SerializedField> equalfields) {
        for (Function<SerializedField, String> naming : NAMING_STRATEGIES) {
            List<SerializedField> qualified = equalfields.stream()
                .map(field -> new SerializedField(field.getDeclaringClass(), naming.apply(field), field.getType(), field.getValue()))
                .collect(toList());
            if (containsUniqueNames(qualified)) {
                return qualified;
            }
        }
        return equalfields;
    }

    private static boolean containsUniqueNames(List<SerializedField> fields) {
        return fields.stream()
            .map(SerializedField::getName)
            .distinct()
            .count() == fields.size();
    }

    private static Function<SerializedField, String> qualifySimple() {
        return field -> field.getDeclaringClass().getSimpleName().replace('.', '$') + "$" + field.getName();
    }

    private static Function<SerializedField, String> qualifyCanonical() {
        return field -> field.getDeclaringClass().getCanonicalName().replace('.', '$') + "$" + field.getName();
    }

}
