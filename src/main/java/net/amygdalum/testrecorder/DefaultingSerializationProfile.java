package net.amygdalum.testrecorder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

public class DefaultingSerializationProfile implements SerializationProfile {

	private SerializationProfile profile;
	private SerializationProfile defaultProfile;

	public DefaultingSerializationProfile(SerializationProfile profile, SerializationProfile defaultProfile) {
		this.profile = profile;
		this.defaultProfile = defaultProfile;
	}

	@Override
	public List<SerializerFactory<?>> getSerializerFactories() {
		List<SerializerFactory<?>> serializerFactories = profile.getSerializerFactories();
		if (serializerFactories == null) {
			return defaultProfile.getSerializerFactories();
		} else {
			return serializerFactories;
		}
	}

	@Override
	public List<Predicate<Field>> getFieldExclusions() {
		List<Predicate<Field>> fieldExclusions = profile.getFieldExclusions();
		if (fieldExclusions == null) {
			return defaultProfile.getFieldExclusions();
		} else {
			return fieldExclusions;
		}
	}

	@Override
	public List<Predicate<Class<?>>> getClassExclusions() {
		List<Predicate<Class<?>>> classExclusions = profile.getClassExclusions();
		if (classExclusions == null) {
			return defaultProfile.getClassExclusions();
		} else {
			return classExclusions;
		}
	}

	@Override
	public List<Field> getGlobalFields() {
		List<Field> globalFields = profile.getGlobalFields();
		if (globalFields == null) {
			return defaultProfile.getGlobalFields();
		} else {
			return globalFields;
		}
	}

}
