package net.amygdalum.testrecorder;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import net.amygdalum.testrecorder.profile.ExcludeExplicitExcluded;
import net.amygdalum.testrecorder.profile.ExcludeGenerated;
import net.amygdalum.testrecorder.profile.ExcludeStatic;

public class DefaultTestRecorderAgentConfig implements TestRecorderAgentConfig {

	public static final List<Predicate<Field>> DEFAULT_FIELD_EXCLUDES = asList(
		new ExcludeExplicitExcluded(),
		new ExcludeGenerated(),
		new ExcludeStatic());
	
	public static final List<Predicate<Class<?>>> DEFAULT_CLASS_EXCLUSIONS = emptyList();

	public static final List<Field> DEFAULT_GLOBAL_FIELDS = emptyList();

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

	@Override
	public SnapshotConsumer getSnapshotConsumer() {
		return new TestGenerator(getInitializer());
	}
	
	@Override
	public long getTimeoutInMillis() {
		return 100_000;
	}

	@Override
	public List<String> getPackages() {
		return Collections.emptyList();
	}
	
	@Override
	public Class<? extends Runnable> getInitializer() {
		return null;
	}
}
