package net.amygdalum.testrecorder;

import java.util.List;

public class DefaultingSerializationProfile implements SerializationProfile {

    private SerializationProfile profile;
    private SerializationProfile defaultProfile;

    public DefaultingSerializationProfile(SerializationProfile profile, SerializationProfile defaultProfile) {
        this.profile = profile;
        this.defaultProfile = defaultProfile;
    }

    @Override
    public List<Fields> getFieldExclusions() {
        List<Fields> fieldExclusions = profile.getFieldExclusions();
        if (fieldExclusions == null) {
            return defaultProfile.getFieldExclusions();
        } else {
            return fieldExclusions;
        }
    }

    @Override
    public List<Classes> getClassExclusions() {
        List<Classes> classExclusions = profile.getClassExclusions();
        if (classExclusions == null) {
            return defaultProfile.getClassExclusions();
        } else {
            return classExclusions;
        }
    }

    @Override
    public List<Fields> getGlobalFields() {
        List<Fields> globalFields = profile.getGlobalFields();
        if (globalFields == null) {
            return defaultProfile.getGlobalFields();
        } else {
            return globalFields;
        }
    }
    
    @Override
    public List<Methods> getInputs() {
        List<Methods> inputs = profile.getInputs();
        if (inputs == null) {
            return defaultProfile.getInputs();
        } else {
            return inputs;
        }
    }

    @Override
    public List<Methods> getOutputs() {
        List<Methods> outputs = profile.getOutputs();
        if (outputs == null) {
            return defaultProfile.getOutputs();
        } else {
            return outputs;
        }
    }
    
}
