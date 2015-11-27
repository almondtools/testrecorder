package com.almondtools.testrecorder.profile;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

import com.almondtools.testrecorder.SerializationProfile;
import com.almondtools.testrecorder.SerializerFactory;
import com.almondtools.testrecorder.serializers.DefaultListSerializer;
import com.almondtools.testrecorder.serializers.BigDecimalSerializer;
import com.almondtools.testrecorder.serializers.BigIntegerSerializer;
import com.almondtools.testrecorder.serializers.DefaultHashMapSerializer;
import com.almondtools.testrecorder.serializers.DefaultSetSerializer;

public class DefaultSerializationProfile implements SerializationProfile {

	public static final List<SerializerFactory<?>> DEFAULT_SERIALIZERS = asList(
		(SerializerFactory<?>) new DefaultListSerializer.Factory(),
		(SerializerFactory<?>) new DefaultSetSerializer.Factory(),
		(SerializerFactory<?>) new DefaultHashMapSerializer.Factory(),
		(SerializerFactory<?>) new BigIntegerSerializer.Factory(),
		(SerializerFactory<?>) new BigDecimalSerializer.Factory());
	
	public static final List<Predicate<Field>> DEFAULT_FIELD_EXCLUDES = asList(
		new ExcludeGenerated(),
		new ExcludeStatic());
	
	public static final List<Predicate<Class<?>>> DEFAULT_CLASS_EXCLUSIONS = emptyList();

	public static final List<Field> DEFAULT_GLOBAL_FIELDS = emptyList();
	
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
	
	@Override
	public List<Field> getGlobalFields() {
		return DEFAULT_GLOBAL_FIELDS;
	}
	

}
