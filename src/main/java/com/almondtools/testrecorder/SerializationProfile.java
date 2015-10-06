package com.almondtools.testrecorder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

public interface SerializationProfile {

	List<SerializerFactory<?>> getSerializerFactories();

	List<Predicate<Field>> getFieldExclusions();

	List<Predicate<Class<?>>> getClassExclusions();
}
