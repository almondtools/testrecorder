package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.deserializers.Templates.assignLocalVariableStatement;
import static net.amygdalum.testrecorder.deserializers.Templates.callLocalMethod;
import static net.amygdalum.testrecorder.types.Computation.variable;
import static net.amygdalum.testrecorder.util.TypeFilters.startingWith;
import static net.amygdalum.testrecorder.util.Types.equalTypes;
import static net.amygdalum.testrecorder.util.Types.innerClasses;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedList;

public class CollectionsListAdaptor implements SetupGenerator<SerializedList> {

    private DefaultListAdaptor adaptor;

    public CollectionsListAdaptor() {
        this.adaptor = new DefaultListAdaptor();
    }

    @Override
    public Class<SerializedList> getAdaptedClass() {
        return SerializedList.class;
    }

    @Override
    public Class<? extends SetupGenerator<SerializedList>> parent() {
        return DefaultListAdaptor.class;
    }

    @Override
    public boolean matches(Type type) {
        return innerClasses(Collections.class).stream()
            .filter(startingWith("Unmodifiable", "Synchronized", "Checked", "Empty", "Singleton"))
            .filter(element -> List.class.isAssignableFrom(element))
            .anyMatch(element -> equalTypes(element, type));
    }

    @Override
    public Computation tryDeserialize(SerializedList value, SetupGenerators generator, DeserializerContext context) {
        TypeManager types = context.getTypes();
        types.registerImport(List.class);
        types.registerType(value.getComponentType());

        String name = types.getRawTypeName(value.getType());
        if (name.contains("Empty")) {
            return tryDeserializeEmpty(value, generator, context);
        } else if (name.contains("Singleton")) {
            return tryDeserializeSingleton(value, generator, context);
        } else if (name.contains("Unmodifiable")) {
            return tryDeserializeUnmodifiable(value, generator, context);
        } else if (name.contains("Synchronized")) {
            return tryDeserializeSynchronized(value, generator, context);
        } else if (name.contains("Checked")) {
            return tryDeserializeChecked(value, generator, context);
        } else {
            throw new DeserializationException("failed deserializing: " + value);
        }
    }

    private Computation createOrdinaryList(SerializedList value, SetupGenerators generator, DeserializerContext context) {
        SerializedList baseValue = new SerializedList(parameterized(ArrayList.class, null, value.getComponentType()));
        baseValue.addAll(value);
        return adaptor.tryDeserialize(baseValue, generator, context);
    }

    private Computation tryDeserializeEmpty(SerializedList value, SetupGenerators generator, DeserializerContext context) {
        Type componentType = value.getComponentType();
        String factoryMethod = "emptyList";
        TypeManager types = context.getTypes();
        types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(componentType)) {
            componentType = wildcard();
        }
        Type resultType = parameterized(List.class, null, componentType);
        return context.forVariable(value, local -> {

            String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod));

            return variable(local.getName(), resultType, asList(decoratingStatement));
        });
    }

    private Computation tryDeserializeSingleton(SerializedList value, SetupGenerators generator, DeserializerContext context) {
        Type componentType = value.getComponentType();
        String factoryMethod = "singletonList";
        TypeManager types = context.getTypes();
        types.registerImport(List.class);
        types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(componentType)) {
            componentType = wildcard();
        }
        Type resultType = parameterized(List.class, null, componentType);
        return context.forVariable(value, local -> {

            Computation computation = value.get(0).accept(generator, context);
            List<String> statements = new LinkedList<>(computation.getStatements());
            String resultBase = computation.getValue();

            String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase));
            statements.add(decoratingStatement);

            return variable(local.getName(), resultType, statements);
        });
    }

    private Computation tryDeserializeUnmodifiable(SerializedList value, SetupGenerators generator, DeserializerContext context) {
        Type componentType = value.getComponentType();
        String factoryMethod = "unmodifiableList";
        TypeManager types = context.getTypes();
        types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(componentType)) {
            componentType = wildcard();
        }
        Type resultType = parameterized(List.class, null, componentType);
        return context.forVariable(value, local -> {

            Computation computation = createOrdinaryList(value, generator, context);
            List<String> statements = new LinkedList<>(computation.getStatements());
            String resultBase = computation.getValue();

            String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase));
            statements.add(decoratingStatement);

            return variable(local.getName(), resultType, statements);
        });
    }

    private Computation tryDeserializeSynchronized(SerializedList value, SetupGenerators generator, DeserializerContext context) {
        Type componentType = value.getComponentType();
        String factoryMethod = "synchronizedList";
        TypeManager types = context.getTypes();
        types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(componentType)) {
            componentType = wildcard();
        }
        Type resultType = parameterized(List.class, null, componentType);
        return context.forVariable(value, local -> {

            Computation computation = createOrdinaryList(value, generator, context);
            List<String> statements = new LinkedList<>(computation.getStatements());
            String resultBase = computation.getValue();

            String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase));
            statements.add(decoratingStatement);

            return variable(local.getName(), resultType, statements);
        });

    }

    private Computation tryDeserializeChecked(SerializedList value, SetupGenerators generator, DeserializerContext context) {
        Type componentType = value.getComponentType();
        String factoryMethod = "checkedList";
        TypeManager types = context.getTypes();
        types.staticImport(Collections.class, factoryMethod);

        if (types.isHidden(componentType)) {
            throw new DeserializationException("cannot deserialize checked list with hidden element type: " + types.getVariableTypeName(componentType));
        }
        Type resultType = parameterized(List.class, null, componentType);
        return context.forVariable(value, local -> {

            Computation computation = createOrdinaryList(value, generator, context);
            List<String> statements = new LinkedList<>(computation.getStatements());
            String resultBase = computation.getValue();
            String checkedType = types.getRawClass(componentType);

            String decoratingStatement = assignLocalVariableStatement(types.getVariableTypeName(resultType), local.getName(), callLocalMethod(factoryMethod, resultBase, checkedType));
            statements.add(decoratingStatement);

            return variable(local.getName(), resultType, statements);
        });
    }

}
