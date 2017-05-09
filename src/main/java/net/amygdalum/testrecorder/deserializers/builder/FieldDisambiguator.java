package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Function;

import net.amygdalum.testrecorder.values.SerializedField;

public final class FieldDisambiguator {

    private FieldDisambiguator() {
    }

    public static List<SerializedField> disambiguate(List<SerializedField> fields) {
        if (containsUniqueNames(fields)) {
            return fields;
        }
        List<SerializedField> collect = fields.stream()
            .collect(groupingBy(SerializedField::getName, toList())).values().stream()
            .map(equalfields -> disambiguateEqualNamed(equalfields))
            .flatMap(List::stream)
            .collect(toList());
        return collect;
    }

    private static  List<SerializedField> disambiguateEqualNamed(List<SerializedField> equalfields) {
        for (Function<SerializedField, String> naming : asList(qualifySimple(), qualifyCanonical())) {
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
        return field -> field.getDeclaringClass().getSimpleName().replace('.', '$') + "$" + field.getName();
    }

}
