package net.amygdalum.testrecorder.profile;

import java.util.ArrayList;
import java.util.List;

public class ConfigurableSerializationProfile implements SerializationProfile {

	private List<Fields> fieldExclusions;
	private List<Classes> classExclusions;
	private List<Fields> globalFields;
	private List<Methods> inputs;
	private List<Methods> outputs;

	private List<Classes> classes;

	public ConfigurableSerializationProfile(SerializationProfile base) {
		this.fieldExclusions = new ArrayList<>(base.getFieldExclusions());
		this.classExclusions = new ArrayList<>(base.getClassExclusions());
		this.globalFields = new ArrayList<>(base.getGlobalFields());
		this.inputs = new ArrayList<>(base.getInputs());
		this.outputs = new ArrayList<>(base.getOutputs());
		this.classes = new ArrayList<>(base.getClasses());
	}

	public static Builder builder(SerializationProfile base) {
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

	public static class Builder {

		private ConfigurableSerializationProfile agent;

		public Builder(SerializationProfile base) {
			this.agent = new ConfigurableSerializationProfile(base);
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

		public ConfigurableSerializationProfile build() {
			return agent;
		}

	}

}
