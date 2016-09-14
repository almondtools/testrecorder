package net.amygdalum.testrecorder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

public class FixedTestRecorderAgentConfig implements TestRecorderAgentConfig {

	private List<Predicate<Field>> fieldExclusions;
	private List<Predicate<Class<?>>> classExclusions;
	private List<Field> globalFields;

	private SnapshotConsumer snapshotConsumer;
	private long timeoutInMillis;
	private List<String> packages;
	private Class<? extends Runnable> initializer;

	public FixedTestRecorderAgentConfig(TestRecorderAgentConfig config) {
		this.fieldExclusions = config.getFieldExclusions();
		this.classExclusions = config.getClassExclusions();
		this.globalFields = config.getGlobalFields();
		this.snapshotConsumer = config.getSnapshotConsumer();
		this.timeoutInMillis = config.getTimeoutInMillis();
		this.packages = config.getPackages();
		this.initializer = config.getInitializer();
	}

	@Override
	public List<Predicate<Field>> getFieldExclusions() {
		return fieldExclusions;
	}

	@Override
	public List<Predicate<Class<?>>> getClassExclusions() {
		return classExclusions;
	}

	@Override
	public List<Field> getGlobalFields() {
		return globalFields;
	}

	@Override
	public SnapshotConsumer getSnapshotConsumer() {
		return snapshotConsumer;
	}

	@Override
	public long getTimeoutInMillis() {
		return timeoutInMillis;
	}

	@Override
	public List<String> getPackages() {
		return packages;
	}

	@Override
	public Class<? extends Runnable> getInitializer() {
		return initializer;
	}

}