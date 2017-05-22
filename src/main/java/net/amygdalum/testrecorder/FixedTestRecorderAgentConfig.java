package net.amygdalum.testrecorder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FixedTestRecorderAgentConfig implements TestRecorderAgentConfig {

	private List<Predicate<Field>> fieldExclusions;
	private List<Predicate<Class<?>>> classExclusions;
	private List<Field> globalFields;
	private List<Method> inputs;
    private List<Method> outputs;

	private SnapshotConsumer snapshotConsumer;
	private long timeoutInMillis;
	private List<String> packages;

	public FixedTestRecorderAgentConfig(TestRecorderAgentConfig config) {
		this.fieldExclusions = new ArrayList<>(config.getFieldExclusions());
		this.classExclusions = new ArrayList<>(config.getClassExclusions());
		this.globalFields = new ArrayList<>(config.getGlobalFields());
		this.snapshotConsumer = config.getSnapshotConsumer();
		this.timeoutInMillis = config.getTimeoutInMillis();
		this.packages = config.getPackages();
	}

    @Override
    public boolean inherit() {
        return false;
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
	public List<Method> getInputs() {
        return inputs;
    }
	
    @Override
	public List<Method> getOutputs() {
        return outputs;
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

}