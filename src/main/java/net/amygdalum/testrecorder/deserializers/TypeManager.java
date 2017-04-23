package net.amygdalum.testrecorder.deserializers;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.Wrapped;
import net.amygdalum.testrecorder.util.Types;

public class TypeManager {

    private static final String DEFAULT_PKG = "java.lang";
    private static final String WILDCARD = "?";

    private String pkg;
    private Map<String, String> imports;
    private Set<String> staticImports;
    private Set<Type> noImports;

    public TypeManager() {
        this("");
    }

    public TypeManager(String pkg) {
        this.pkg = pkg;
        this.imports = new LinkedHashMap<>();
        this.staticImports = new LinkedHashSet<>();
        this.noImports = new LinkedHashSet<>();
    }

    public String getPackage() {
        return pkg;
    }

    public List<String> getImports() {
        return Stream.concat(imports.values().stream(), staticImports.stream())
            .collect(toList());
    }

    public void staticImport(Class<?> type, String method) {
        staticImports.add("static " + type.getName() + "." + method);
    }

    public void registerTypes(Type... types) {
        for (Type type : types) {
            registerType(type);
        }
    }

    public void registerType(Type type) {
        if (type instanceof Class<?>) {
            registerImport((Class<?>) type);
        } else if (type instanceof GenericArrayType) {
            registerType(((GenericArrayType) type).getGenericComponentType());
        } else if (type instanceof ParameterizedType) {
            registerType(((ParameterizedType) type).getRawType());
            registerTypes(((ParameterizedType) type).getActualTypeArguments());
        }
    }

    public void registerImport(Class<?> clazz) {
        if (cannotBeNotImported(clazz)) {
            return;
        } else if (isHidden(clazz)) {
            registerImport(Wrapped.class);
            staticImport(Wrapped.class, "clazz");
            noImports.add(clazz);
        } else if (isColliding(clazz)) {
            noImports.add(clazz);
        } else if (clazz.isPrimitive()) {
            return;
        } else if (clazz.isArray()) {
            registerImport(clazz.getComponentType());
        } else {
            imports.put(clazz.getSimpleName(), getFullName(clazz));
        }
    }

    private String getFullName(Class<?> clazz) {
        return getFullSignature(clazz).replace('$', '.');
    }

    private String getFullSignature(Class<?> clazz) {
        return clazz.getName();
    }

    public String getBestName(Type type) {
        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            String array = "";
            while (clazz.isArray()) {
                array += "[]";
                clazz = clazz.getComponentType();
            }
            String base = isNotImported(clazz) ? getFullName(clazz) : getSimpleName(clazz);
            String generics = clazz.getTypeParameters().length > 0 ? "<>" : "";
            return base + generics + array;
        } else if (type instanceof GenericArrayType) {
            return getBestName(((GenericArrayType) type).getGenericComponentType()) + "[]";
        } else if (type instanceof ParameterizedType) {
            return getSimpleName(((ParameterizedType) type).getRawType())
                + Stream.of(((ParameterizedType) type).getActualTypeArguments())
                    .map(argtype -> argtype instanceof TypeVariable<?> ? wildcard() : argtype)
                    .map(argtype -> getBestName(argtype))
                    .collect(joining(", ", "<", ">"));
        } else if (type instanceof WildcardType) {
            return WILDCARD;
        } else {
            return getBestName(Object.class);
        }
    }

    public String getBestSignature(Type type) {
        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            String array = "";
            while (clazz.isArray()) {
                array += "[]";
                clazz = clazz.getComponentType();
            }
            String base = isNotImported(clazz) ? getFullSignature(clazz) : getSimpleSignature(clazz);
            String generics = clazz.getTypeParameters().length > 0 ? "<>" : "";
            return base + generics + array;
        } else if (type instanceof GenericArrayType) {
            return getBestName(((GenericArrayType) type).getGenericComponentType()) + "[]";
        } else if (type instanceof ParameterizedType) {
            return getSimpleName(((ParameterizedType) type).getRawType())
                + Stream.of(((ParameterizedType) type).getActualTypeArguments())
                    .map(argtype -> argtype instanceof TypeVariable<?> ? wildcard() : argtype)
                    .map(argtype -> getBestName(argtype))
                    .collect(joining(", ", "<", ">"));
        } else if (type instanceof WildcardType) {
            return WILDCARD;
        } else {
            return getBestSignature(Object.class);
        }
    }

    public String getRelaxedName(Type type) {
        return getSimpleName(type);
    }

    private String getSimpleName(Type type) {
        return getSimpleSignature(type).replace('$', '.');
    }

    private String getSimpleSignature(Type type) {
        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            String array = "";
            while (clazz.isArray()) {
                array += "[]";
                clazz = clazz.getComponentType();
            }
            if (cannotBeNotImported(clazz) || isNotImported(clazz)) {
                return clazz.getName() + array;
            } else {
                return clazz.getSimpleName() + array;
            }
        } else if (type instanceof GenericArrayType) {
            return getSimpleSignature(((GenericArrayType) type).getGenericComponentType()) + "[]";
        } else if (type instanceof ParameterizedType) {
            return getSimpleSignature(((ParameterizedType) type).getRawType())
                + Stream.of(((ParameterizedType) type).getActualTypeArguments())
                    .map(argtype -> argtype instanceof TypeVariable<?> ? wildcard() : argtype)
                    .map(argtype -> getSimpleName(argtype))
                    .collect(joining(", ", "<", ">"));
        } else if (type instanceof WildcardType) {
            return WILDCARD;
        } else {
            return getSimpleSignature(Object.class);
        }
    }

    public String getRawName(Type type) {
        if (type instanceof Class<?>) {
            return getSimpleName(type);
        } else if (type instanceof GenericArrayType) {
            return getRawName(((GenericArrayType) type).getGenericComponentType()) + "[]";
        } else if (type instanceof ParameterizedType) {
            return getRawName(((ParameterizedType) type).getRawType());
        } else {
            return getRawName(Object.class);
        }
    }

    public String getRawTypeName(Type type) {
        if (isHidden(type)) {
            return getWrappedName(type);
        } else {
            return getRawName(type) + ".class";
        }
    }

    public boolean isHidden(Constructor<?> constructor) {
        return Types.isHidden(constructor, pkg);
    }

    public boolean isHidden(Type type) {
        return Types.isHidden(type, pkg);
    }

    public boolean isErasureHidden(Type type) {
        return Types.isErasureHidden(type, pkg);
    }

    public boolean isColliding(Class<?> clazz) {
        return imports.containsKey(clazz.getSimpleName())
            && !imports.get(clazz.getSimpleName()).equals(getFullName(clazz));
    }

    private boolean cannotBeNotImported(Class<?> clazz) {
        return noImports.contains(clazz);
    }

    public boolean isNotImported(Class<?> clazz) {
        if (clazz.getPackage() != null && clazz.getPackage().getName().equals(DEFAULT_PKG)) {
            return noImports.contains(clazz);
        }
        return !imports.containsKey(clazz.getSimpleName())
            || !imports.get(clazz.getSimpleName()).equals(getFullName(clazz));
    }

    public Type wrapHidden(Type type) {
        if (isHidden(type)) {
            return Wrapped.class;
        } else {
            return type;
        }
    }

    public Type bestVisible(Type type) {
        if (!isHidden(type)) {
            return type;
        }
        Class<?> clazz = baseType(type);
        while (clazz != Object.class && isHidden(clazz)) {
            clazz = clazz.getSuperclass();
        }
        return Object.class;
    }

    public String getWrappedName(Type type) {
        return "clazz(\"" + baseType(type).getName() + "\")";
    }

    public Type bestType(Type preferred, Class<?> bound) {
        if (isHidden(preferred)) {
            return bound;
        }
        if (isErasureHidden(preferred)) {
            return baseType(preferred);
        }
        return preferred;
    }

    public Type bestType(Type preferred, Type secondary, Class<?> bound) {
        if (!isHidden(preferred) && !isErasureHidden(preferred) && bound.isAssignableFrom(baseType(preferred))) {
            return preferred;
        }
        if (bound.isAssignableFrom(baseType(secondary))) {
            return secondary;
        }
        return bound;
    }

}
