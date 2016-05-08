package net.amygdalum.testrecorder.profile;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

import net.amygdalum.testrecorder.SerializationProfile;
import net.amygdalum.testrecorder.SerializerFactory;
import net.amygdalum.testrecorder.serializers.ArraysListSerializer;
import net.amygdalum.testrecorder.serializers.BigDecimalSerializer;
import net.amygdalum.testrecorder.serializers.BigIntegerSerializer;
import net.amygdalum.testrecorder.serializers.ClassSerializer;
import net.amygdalum.testrecorder.serializers.CollectionsListSerializer;
import net.amygdalum.testrecorder.serializers.CollectionsMapSerializer;
import net.amygdalum.testrecorder.serializers.CollectionsSetSerializer;
import net.amygdalum.testrecorder.serializers.DefaultListSerializer;
import net.amygdalum.testrecorder.serializers.DefaultMapSerializer;
import net.amygdalum.testrecorder.serializers.DefaultSetSerializer;

public class DefaultSerializationProfile implements SerializationProfile {

	public static final List<SerializerFactory<?>> DEFAULT_SERIALIZERS = asList(
		(SerializerFactory<?>) new ArraysListSerializer.Factory(),
		(SerializerFactory<?>) new CollectionsListSerializer.Factory(),
		(SerializerFactory<?>) new CollectionsSetSerializer.Factory(),
		(SerializerFactory<?>) new CollectionsMapSerializer.Factory(),
		(SerializerFactory<?>) new DefaultListSerializer.Factory(),
		(SerializerFactory<?>) new DefaultSetSerializer.Factory(),
		(SerializerFactory<?>) new DefaultMapSerializer.Factory(),
		(SerializerFactory<?>) new ClassSerializer.Factory(),
		(SerializerFactory<?>) new BigIntegerSerializer.Factory(),
		(SerializerFactory<?>) new BigDecimalSerializer.Factory());
	
	public static final List<Predicate<Field>> DEFAULT_FIELD_EXCLUDES = asList(
		new ExcludeExplicitExcluded(),
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
