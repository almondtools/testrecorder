package com.almondtools.invivoderived.profile;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

import com.almondtools.invivoderived.SerializationProfile;
import com.almondtools.invivoderived.SerializerFactory;
import com.almondtools.invivoderived.serializers.ArrayListSerializer;
import com.almondtools.invivoderived.serializers.BigDecimalSerializer;
import com.almondtools.invivoderived.serializers.BigIntegerSerializer;
import com.almondtools.invivoderived.serializers.LinkedHashMapSerializer;
import com.almondtools.invivoderived.serializers.LinkedHashSetSerializer;

public class DefaultSerializationProfile implements SerializationProfile {

	public static final List<SerializerFactory<?>> DEFAULT_SERIALIZERS = asList(
		(SerializerFactory<?>) new ArrayListSerializer.Factory(),
		(SerializerFactory<?>) new LinkedHashSetSerializer.Factory(),
		(SerializerFactory<?>) new LinkedHashMapSerializer.Factory(),
		(SerializerFactory<?>) new BigIntegerSerializer.Factory(),
		(SerializerFactory<?>) new BigDecimalSerializer.Factory());
	
	public static final List<Predicate<Field>> DEFAULT_FIELD_EXCLUDES = asList(
		new ExcludeGenerated(),
		new ExcludeStatic());
	
	private static final List<Predicate<Class<?>>> DEFAULT_CLASS_EXCLUSIONS = emptyList();
	
	@Override
	public List<SerializerFactory<?>> getSerializerFactories() {
		return DEFAULT_SERIALIZERS;
	}

	@Override
	public List<Predicate<Field>> getFieldExclusions() {
		return DEFAULT_FIELD_EXCLUDES;
	}

	@Override
	public List<Predicate<Class<?>>> getClassExclusions() {
		return DEFAULT_CLASS_EXCLUSIONS;
	}

}
