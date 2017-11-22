package net.amygdalum.testrecorder;

import java.util.ArrayList;
import java.util.List;

public class ConfigurableTestRecorderAgentConfig implements TestRecorderAgentConfig {

	private List<Fields> fieldExclusions;
	private List<Classes> classExclusions;
	private List<Fields> globalFields;
	private List<Methods> inputs;
	private List<Methods> outputs;

	private SnapshotConsumer snapshotConsumer;
	private long timeoutInMillis;
	private List<Classes> classes;

	public ConfigurableTestRecorderAgentConfig(TestRecorderAgentConfig base) {
		this.fieldExclusions = new ArrayList<>(base.getFieldExclusions());
		this.classExclusions = new ArrayList<>(base.getClassExclusions());
		this.globalFields = new ArrayList<>(base.getGlobalFields());
		this.inputs = new ArrayList<>(base.getInputs());
		this.outputs = new ArrayList<>(base.getOutputs());
		this.snapshotConsumer = base.getSnapshotConsumer();
		this.timeoutInMillis = base.getTimeoutInMillis();
		this.classes = new ArrayList<>(base.getClasses());
	}

	public static Builder builder(TestRecorderAgentConfig base) {
		return new Builder(base);
	}

	@Override
	public List<Classes> getClasses() {
		return classes;
	}

	@Override
	public List<Fields> getFieldExclusions() {
		return fieldExclusions;
	}

	@Override
	public List<Classes> getClassExclusions() {
		return classExclusions;
	}

	@Override
	public List<Fields> getGlobalFields() {
		return globalFields;
	}

	@Override
	public List<Methods> getInputs() {
		return inputs;
	}

	@Override
	public List<Methods> getOutputs() {
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

	public static class Builder {

		private ConfigurableTestRecorderAgentConfig agent;

		public Builder(TestRecorderAgentConfig base) {
			this.agent = new ConfigurableTestRecorderAgentConfig(base);
		}

		public Builder withClasses(List<Classes> classes) {
			agent.classes = classes;
			return this;
		}

		public Builder withFieldExclusions(List<Fields> fieldExclusions) {
			agent.fieldExclusions = fieldExclusions;
			return this;
		}

		public Builder withClassExclusions(List<Classes> classExclusions) {
			agent.classExclusions = classExclusions;
			return this;
		}

		public Builder withGlobalFields(List<Fields> globalFields) {
			agent.globalFields = globalFields;
			return this;
		}

		public Builder withInputs(List<Methods> inputs) {
			agent.inputs = inputs;
			return this;
		}

		public Builder withOutputs(List<Methods> outputs) {
			agent.outputs = outputs;
			return this;
		}

		public Builder withSnapshotConsumer(SnapshotConsumer snapshotConsumer) {
			agent.snapshotConsumer = snapshotConsumer;
			return this;
		}

		public Builder withTimeoutInMillis(long timeoutInMillis) {
			agent.timeoutInMillis = timeoutInMillis;
			return this;
		}

		public ConfigurableTestRecorderAgentConfig build() {
			return agent;
		}

	}

}
