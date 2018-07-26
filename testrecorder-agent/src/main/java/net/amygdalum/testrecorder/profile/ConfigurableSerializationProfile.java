package net.amygdalum.testrecorder.profile;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

public class ConfigurableSerializationProfile implements SerializationProfile {

	private List<Fields> fieldExclusions;
	private List<Fields> fieldFacades;
	private List<Classes> classExclusions;
	private List<Classes> classFacades;
	private List<Fields> globalFields;
	private List<Methods> inputs;
	private List<Methods> outputs;
	private List<Methods> recorded;

	private List<Classes> classes;

	public ConfigurableSerializationProfile() {
		this.fieldExclusions = new ArrayList<>();
		this.fieldFacades = new ArrayList<>();
		this.classExclusions = new ArrayList<>();
		this.classFacades = new ArrayList<>();
		this.globalFields = new ArrayList<>();
		this.inputs = new ArrayList<>();
		this.outputs = new ArrayList<>();
		this.recorded = new ArrayList<>();
		this.classes = new ArrayList<>();
	}

	public ConfigurableSerializationProfile(SerializationProfile base) {
		this.fieldExclusions = new ArrayList<>(base.getFieldExclusions());
		this.fieldFacades = new ArrayList<>(base.getFieldFacades());
		this.classExclusions = new ArrayList<>(base.getClassExclusions());
		this.classFacades = new ArrayList<>(base.getClassFacades());
		this.globalFields = new ArrayList<>(base.getGlobalFields());
		this.inputs = new ArrayList<>(base.getInputs());
		this.outputs = new ArrayList<>(base.getOutputs());
		this.recorded = new ArrayList<>(base.getRecorded());
		this.classes = new ArrayList<>(base.getClasses());
	}

	public static Builder builder() {
		return new Builder();
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
	public List<Fields> getFieldFacades() {
		return fieldFacades;
	}

	@Override
	public List<Classes> getClassExclusions() {
		return classExclusions;
	}

	@Override
	public List<Classes> getClassFacades() {
		return classFacades;
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
	public List<Methods> getRecorded() {
		return recorded;
	}

	public static class Builder {

		private ConfigurableSerializationProfile profile;

		public Builder() {
			this.profile = new ConfigurableSerializationProfile();
		}

		public Builder(SerializationProfile base) {
			this.profile = new ConfigurableSerializationProfile(base);
		}

		public Builder withClasses(Classes... classes) {
			return withClasses(asList(classes));
		}

		public Builder withClasses(List<Classes> classes) {
			profile.classes.addAll(classes);
			return this;
		}

		public Builder withRecorded(Methods... methods) {
			return withRecorded(asList(methods));
		}

		public Builder withRecorded(List<Methods> methods) {
			profile.recorded.addAll(methods);
			return this;
		}

		public Builder withFieldExclusions(Fields... fieldExclusions) {
			return withFieldExclusions(asList(fieldExclusions));
		}
		
		public Builder withFieldExclusions(List<Fields> fieldExclusions) {
			profile.fieldExclusions.addAll(fieldExclusions);
			return this;
		}

		public Builder withFieldFacades(Fields... fieldFacades) {
			return withFieldFacades(asList(fieldFacades));
		}
		
		public Builder withFieldFacades(List<Fields> fieldFacades) {
			profile.fieldFacades.addAll(fieldFacades);
			return this;
		}
		
		public Builder withClassExclusions(Classes... classExclusions) {
			return withClassExclusions(asList(classExclusions));
		}
		
		public Builder withClassExclusions(List<Classes> classExclusions) {
			profile.classExclusions.addAll(classExclusions);
			return this;
		}

		public Builder withClassFacades(Classes... classFacades) {
			return withClassFacades(asList(classFacades));
		}
		
		public Builder withClassFacades(List<Classes> classFacades) {
			profile.classFacades.addAll(classFacades);
			return this;
		}
		
		public Builder withGlobalFields(Fields... globalFields) {
			return withGlobalFields(asList(globalFields));
		}
		
		public Builder withGlobalFields(List<Fields> globalFields) {
			profile.globalFields.addAll(globalFields);
			return this;
		}

		public Builder withInputs(Methods... inputs) {
			return withInputs(asList(inputs));
		}
		
		public Builder withInputs(List<Methods> inputs) {
			profile.inputs.addAll(inputs);
			return this;
		}

		public Builder withOutputs(Methods... outputs) {
			return withOutputs(asList(outputs));
		}
		
		public Builder withOutputs(List<Methods> outputs) {
			profile.outputs.addAll(outputs);
			return this;
		}

		public ConfigurableSerializationProfile build() {
			return profile;
		}

	}

}
