package net.amygdalum.testrecorder.types;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public interface TypeManager {

	String getPackage();

	List<String> getImports();

	void staticImport(Class<?> type, String method);

	void registerTypes(Type... types);

	void registerType(Type type);

	void registerImport(Class<?> clazz);

	String getVariableTypeName(Type type);

	String getConstructorTypeName(Type type);

	String getRawTypeName(Type type);

	String getRawClass(String type);

	String getRawClass(Type type);

	boolean isHidden(Constructor<?> constructor);

	boolean isHidden(Type type);

	boolean isGenericVariable(Type type);

	boolean isErasureHidden(Type type);

	boolean isColliding(Class<?> clazz);

	boolean isNotImported(Class<?> clazz);

	Type wrapHidden(Type type);

	String getWrappedName(Type type);

	Type bestType(Type preferred, Class<?> bound);

	Type bestType(Type preferred, Type secondary, Class<?> bound);

	Optional<Type> mostSpecialOf(Type... types);

}