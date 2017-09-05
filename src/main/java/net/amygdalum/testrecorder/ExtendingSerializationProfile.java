package net.amygdalum.testrecorder;

import java.util.ArrayList;
import java.util.List;

public class ExtendingSerializationProfile implements SerializationProfile {

    private SerializationProfile profile;
    private SerializationProfile defaultProfile;

    public ExtendingSerializationProfile(SerializationProfile profile, SerializationProfile defaultProfile) {
        this.profile = profile;
        this.defaultProfile = defaultProfile;
    }

    @Override
    public boolean inherit() {
        return false;
    }

    @Override
    public List<Fields> getFieldExclusions() {
        List<Fields> fieldExclusions = new ArrayList<>();

        if (profile.getFieldExclusions() != null) {
            fieldExclusions.addAll(profile.getFieldExclusions());
        }
        if (defaultProfile.getFieldExclusions() != null) {
            fieldExclusions.addAll(defaultProfile.getFieldExclusions());
        }

        return fieldExclusions;
    }

    @Override
    public List<Classes> getClassExclusions() {
        List<Classes> classExclusions = new ArrayList<>();

        if (profile.getClassExclusions() != null) {
            classExclusions.addAll(profile.getClassExclusions());
        }
        if (defaultProfile.getClassExclusions() != null) {
            classExclusions.addAll(defaultProfile.getClassExclusions());
        }

        return classExclusions;
    }

    @Override
    public List<Fields> getGlobalFields() {
        List<Fields> globalFields = new ArrayList<>();

        if (profile.getGlobalFields() != null) {
            globalFields.addAll(profile.getGlobalFields());
        }
        if (defaultProfile.getGlobalFields() != null) {
            globalFields.addAll(defaultProfile.getGlobalFields());
        }

        return globalFields;
    }

    @Override
    public List<Methods> getInputs() {
        List<Methods> inputs = new ArrayList<>();
        
        if (profile.getInputs() != null) {
            inputs.addAll(profile.getInputs());
        }
        if (defaultProfile.getInputs() != null) {
            inputs.addAll(defaultProfile.getInputs());
        }
        
        return inputs;
    }
    
    @Override
    public List<Methods> getOutputs() {
        List<Methods> outputs = new ArrayList<>();
        
        if (profile.getOutputs() != null) {
            outputs.addAll(profile.getOutputs());
        }
        if (defaultProfile.getOutputs() != null) {
            outputs.addAll(defaultProfile.getOutputs());
        }
        
        return outputs;
    }
    
}
