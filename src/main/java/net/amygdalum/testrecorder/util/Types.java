package net.amygdalum.testrecorder.util;

import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

public final class Types {

    private static final String SYNTHETIC_INDICATOR = "$";
    private static final String[] HANDLED_SYNTHETIC_PREFIXES = { "this$" };

    private Types() {
    }

    public static Type inferType(List<Type> types) {
        Optional<Class<?>> reduce = types.stream()
            .map(type -> superTypes(baseType(type)))
            .reduce((s1, s2) -> intersectClasses(s1, s2))
            .map(s -> bestClass(s));
        return reduce.orElse(Object.class);
    }

    private static Set<Class<?>> superTypes(Class<?> clazz) {
        WorkSet<Class<?>> todo = new WorkSet<>();
        todo.add(clazz);
        while (!todo.isEmpty()) {
            Class<?> next = todo.remove();
            if (next != Object.class) {
                Class<?> superclass = next.getSuperclass();
                if (superclass != null) {
                    todo.add(superclass);
                }
                for (Class<?> nextInterface : next.getInterfaces()) {
                    todo.add(nextInterface);
                }
            }
        }
        return todo.getDone();
    }

    private static Set<Class<?>> intersectClasses(Set<Class<?>> s1, Set<Class<?>> s2) {
        SortedSet<Class<?>> result = new TreeSet<>(new Comparator<Class<?>>() {

            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                if (o1.isAssignableFrom(o2)) {
                    return -1;
                } else if (o2.isAssignableFrom(o1)) {
                    return 1;
                }
                int m1 = o1.getMethods().length;
                int m2 = o2.getMethods().length;

                if (m2 > m1) {
                    return 1;
                } else if (m1 > m2) {
                    return -1;
                } else {
                    return System.identityHashCode(o1) - System.identityHashCode(o2);
                }
            }
        });
        result.addAll(s1);
        result.retainAll(s2);
        return result;
    }

    private static Class<?> bestClass(Set<Class<?>> classes) {
        Class<?> bestInterface = null;
        Class<?> bestClass = Object.class;
        for (Class<?> clazz : classes) {
            if (clazz.isInterface()) {
                if (bestInterface == null) {
                    bestInterface = clazz;
                } else if (bestInterface.isAssignableFrom(clazz)) {
                    bestInterface = clazz;
                }
            } else if (bestClass.isAssignableFrom(clazz)) {
                bestClass = clazz;
            }
        }
        if (isBoxedPrimitive(bestClass)
            || bestClass == String.class) {
            return bestClass;
        } else if (bestInterface != null) {
            return bestInterface;
        } else {
            return bestClass;
        }
    }

    public static Class<?> baseType(Type type) {
        if (type instanceof Class<?>) {
            return ((Class<?>) type);
        } else if (type instanceof GenericArrayType) {
            return Array.newInstance(baseType(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
        } else if (type instanceof ParameterizedType) {
            return baseType(((ParameterizedType) type).getRawType());
        } else {
            return Object.class;
        }
    }

    public static Class<?> boxedType(Type type) {
        if (!(type instanceof Class<?>)) {
            return baseType(type);
        }
        Class<?> clazz = (Class<?>) type;
        if (clazz == boolean.class) {
            return Boolean.class;
        } else if (clazz == char.class) {
            return Character.class;
        } else if (clazz == byte.class) {
            return Byte.class;
        } else if (clazz == short.class) {
            return Short.class;
        } else if (clazz == int.class) {
            return Integer.class;
        } else if (clazz == float.class) {
            return Float.class;
        } else if (clazz == long.class) {
            return Long.class;
        } else if (clazz == double.class) {
            return Double.class;
        } else {
            return clazz;
        }
    }

    public static Type component(Type arrayType) {
        if (arrayType instanceof Class<?> && ((Class<?>) arrayType).isArray()) {
            return ((Class<?>) arrayType).getComponentType();
        } else if (arrayType instanceof GenericArrayType) {
            return ((GenericArrayType) arrayType).getGenericComponentType();
        } else {
            return Object.class;
        }
    }

    public static boolean assignableTypes(Type toType, Type fromType) {
        Class<?> toClass = baseType(toType);
        Class<?> fromClass = baseType(fromType);
        if (!toClass.isAssignableFrom(fromClass)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean equalTypes(Type type1, Type type2) {
        return baseType(type1).equals(baseType(type2));
    }

    public static boolean boxingEquivalentTypes(Type type1, Type type2) {
        return boxedType(type1).equals(boxedType(type2));
    }

    public static Optional<Type> typeArgument(Type type, int i) {
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (actualTypeArguments == null || actualTypeArguments.length <= i) {
                return Optional.empty();
            }
            return Optional.of(actualTypeArguments[i]);
        } else {
            return Optional.empty();
        }
    }

    public static Class<?> innerType(Class<?> clazz, String name) {
        for (Class<?> inner : clazz.getDeclaredClasses()) {
            if (inner.getSimpleName().equals(name)) {
                return inner;
            }
        }
        throw new TypeNotPresentException(clazz.getName() + "$" + name, new ClassNotFoundException(clazz.getName() + "$" + name));
    }

    public static boolean isHidden(Type type, String pkg) {
        Class<?> clazz = baseType(type);
        while (true) {
            int modifiers = clazz.getModifiers();
            if (clazz.isAnonymousClass()) {
                return true;
            } else if (isPublic(modifiers)) {
                return false;
            } else if (isPrivate(modifiers)) {
                return true;
            } else if (pkg == null || !pkg.equals(clazz.getPackage().getName())) {
                return true;
            } else if (clazz.getEnclosingClass() != null) {
                clazz = clazz.getEnclosingClass();
            } else {
                return false;
            }
        }
    }

    public static boolean isHidden(Constructor<?> constructor, String pkg) {
        int modifiers = constructor.getModifiers();
        if (isPrivate(modifiers)) {
            return true;
        } else {
            return isHidden(constructor.getDeclaringClass(), pkg);
        }
    }

    public static boolean isBoxedPrimitive(Type type) {
        if (!(type instanceof Class<?>)) {
            return false;
        }
        Class<?> clazz = (Class<?>) type;
        if (clazz == Boolean.class) {
            return true;
        } else if (clazz == Character.class) {
            return true;
        } else if (clazz == Byte.class) {
            return true;
        } else if (clazz == Short.class) {
            return true;
        } else if (clazz == Integer.class) {
            return true;
        } else if (clazz == Float.class) {
            return true;
        } else if (clazz == Long.class) {
            return true;
        } else if (clazz == Double.class) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isPrimitive(Type type) {
        return type instanceof Class<?>
            && ((Class<?>) type).isPrimitive();
    }

    public static boolean isLiteral(Type type) {
        return isPrimitive(type)
            || isBoxedPrimitive(type)
            || type == String.class;
    }

    public static Type array(Type componentType) {
        if (componentType instanceof Class<?>) {
            return Array.newInstance((Class<?>) componentType, 0).getClass();
        } else {
            return new GenericArrayTypeImplementation(componentType);
        }
    }

    public static ParameterizedType parameterized(Type raw, Type owner, Type... typeArgs) {
        return new ParameterizedTypeImplementation(raw, owner, typeArgs);
    }

    public static WildcardType wildcard() {
        return new WildcardTypeImplementation();
    }

    public static WildcardType wildcardExtends(Type... bounds) {
        return new WildcardTypeImplementation().extending(bounds);
    }

    public static WildcardType wildcardSuper(Type... bounds) {
        return new WildcardTypeImplementation().limiting(bounds);
    }

    public static List<Field> allFields(Class<?> clazz) {
        Class<?> current = clazz;
        List<Field> fields = new ArrayList<>();
        while (current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (!field.isSynthetic()) {
                    fields.add(field);
                }
            }
            current = current.getSuperclass();
        }
        return fields;

    }

    public static List<Class<?>> innerClasses(Class<?> of) {
        return asList(of.getDeclaredClasses());
    }

    public static List<Method> allMethods(Class<?> clazz) {
        Class<?> current = clazz;
        List<Method> methods = new ArrayList<>();
        while (current != Object.class) {
            for (Method method : current.getDeclaredMethods()) {
                if (!method.isSynthetic()) {
                    methods.add(method);
                }
            }
            current = current.getSuperclass();
        }
        return methods;
    }

    public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Class<?> current = clazz;
        while (current != Object.class) {
            try {
                return current.getDeclaredMethod(name, parameterTypes);
            } catch (NoSuchMethodException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchMethodException(name);
    }

    public static Field getDeclaredField(Class<?> clazz, String name) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != Object.class) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    public static boolean needsCast(Type variableType, Type expressionType) {
        return !baseType(variableType).isAssignableFrom(baseType(expressionType))
            && !boxingEquivalentTypes(variableType, expressionType);
    }

    public static boolean isFinal(Field field) {
        return (field.getModifiers() & Modifier.FINAL) == Modifier.FINAL;
    }

    public static boolean isStatic(Field field) {
        return (field.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
    }

    public static boolean isUnhandledSynthetic(Field field) {
        String name = field.getName();
        if (!field.isSynthetic() && !name.contains(SYNTHETIC_INDICATOR)) {
            return false;
        }
        for (String prefix : HANDLED_SYNTHETIC_PREFIXES) {
            if (name.startsWith(prefix)) {
                return false;
            }
        }
        return true;
    }

    private static final class GenericArrayTypeImplementation implements GenericArrayType {

        private Type componentType;

        public GenericArrayTypeImplementation(Type componentType) {
            this.componentType = componentType;
        }

        @Override
        public Type getGenericComponentType() {
            return componentType;
        }

        @Override
        public String getTypeName() {
            StringBuilder buffer = new StringBuilder();
            buffer.append(componentType.getTypeName());
            buffer.append("[]");
            return buffer.toString();
        }

        @Override
        public int hashCode() {
            return componentType.hashCode() + 19;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            GenericArrayTypeImplementation that = (GenericArrayTypeImplementation) obj;
            return this.componentType.equals(that.componentType);
        }

        @Override
        public String toString() {
            return getTypeName();
        }

    }

    private static final class ParameterizedTypeImplementation implements ParameterizedType {

        private Type raw;
        private Type owner;
        private Type[] typeArgs;

        public ParameterizedTypeImplementation(Type raw, Type owner, Type... typeArgs) {
            this.raw = raw;
            this.owner = owner;
            this.typeArgs = typeArgs;
        }

        @Override
        public Type getRawType() {
            return raw;
        }

        @Override
        public Type getOwnerType() {
            return owner;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return typeArgs;
        }

        @Override
        public String getTypeName() {
            StringBuilder buffer = new StringBuilder();
            buffer.append(raw.getTypeName());
            buffer.append('<');
            if (typeArgs != null && typeArgs.length > 0) {
                buffer.append(Stream.of(typeArgs)
                    .map(type -> type.getTypeName())
                    .collect(joining(", ")));
            }
            buffer.append('>');
            return buffer.toString();
        }

        @Override
        public int hashCode() {
            return raw.hashCode() * 3 + (owner == null ? 0 : owner.hashCode() * 5) + Arrays.hashCode(typeArgs) * 7 + 13;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ParameterizedTypeImplementation that = (ParameterizedTypeImplementation) obj;
            return this.raw.equals(that.raw)
                && Objects.equals(this.owner, that.owner)
                && Arrays.equals(this.typeArgs, that.typeArgs);
        }

        @Override
        public String toString() {
            return getTypeName();
        }

    }

    private static final class WildcardTypeImplementation implements WildcardType {

        private Type[] upperBounds;
        private Type[] lowerBounds;

        public WildcardTypeImplementation() {
            upperBounds = new Type[0];
            lowerBounds = new Type[0];
        }

        public WildcardTypeImplementation extending(Type... bounds) {
            this.upperBounds = bounds;
            return this;
        }

        public WildcardTypeImplementation limiting(Type... bounds) {
            this.lowerBounds = bounds;
            return this;
        }

        @Override
        public Type[] getUpperBounds() {
            return upperBounds;
        }

        @Override
        public Type[] getLowerBounds() {
            return lowerBounds;
        }

        @Override
        public String getTypeName() {
            StringBuilder buffer = new StringBuilder();
            buffer.append("?");
            if (lowerBounds.length > 0) {
                buffer.append(" super ").append(Stream.of(lowerBounds)
                    .map(type -> type.getTypeName())
                    .collect(joining(", ")));
            }
            if (upperBounds.length > 0) {
                buffer.append(" extends ").append(Stream.of(upperBounds)
                    .map(type -> type.getTypeName())
                    .collect(joining(", ")));
            }
            return buffer.toString();
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(upperBounds) * 5 + Arrays.hashCode(lowerBounds) * 7 + 23;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            WildcardTypeImplementation that = (WildcardTypeImplementation) obj;
            return Arrays.equals(this.upperBounds, that.upperBounds)
                && Arrays.equals(this.lowerBounds, that.lowerBounds);
        }

        @Override
        public String toString() {
            return getTypeName();
        }

    }

}
