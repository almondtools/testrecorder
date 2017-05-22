package net.amygdalum.testrecorder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
    public List<Predicate<Field>> getFieldExclusions() {
        List<Predicate<Field>> fieldExclusions = new ArrayList<>();

        if (profile.getFieldExclusions() != null) {
            fieldExclusions.addAll(profile.getFieldExclusions());
        }
        if (defaultProfile.getFieldExclusions() != null) {
            fieldExclusions.addAll(defaultProfile.getFieldExclusions());
        }

        return fieldExclusions;
    }

    @Override
    public List<Predicate<Class<?>>> getClassExclusions() {
        List<Predicate<Class<?>>> classExclusions = new ArrayList<>();

        if (profile.getClassExclusions() != null) {
            classExclusions.addAll(profile.getClassExclusions());
        }
        if (defaultProfile.getClassExclusions() != null) {
            classExclusions.addAll(defaultProfile.getClassExclusions());
        }

        return classExclusions;
    }

    @Override
    public List<Field> getGlobalFields() {
        List<Field> globalFields = new ArrayList<>();

        if (profile.getGlobalFields() != null) {
            globalFields.addAll(profile.getGlobalFields());
        }
        if (defaultProfile.getGlobalFields() != null) {
            globalFields.addAll(defaultProfile.getGlobalFields());
        }

        return globalFields;
    }

    @Override
    public List<Method> getInputs() {
        List<Method> inputs = new ArrayList<>();
        
        if (profile.getInputs() != null) {
            inputs.addAll(profile.getInputs());
        }
        if (defaultProfile.getInputs() != null) {
            inputs.addAll(defaultProfile.getInputs());
        }
        
        return inputs;
    }
    
    @Override
    public List<Method> getOutputs() {
        List<Method> outputs = new ArrayList<>();
        
        if (profile.getOutputs() != null) {
            outputs.addAll(profile.getOutputs());
        }
        if (defaultProfile.getOutputs() != null) {
            outputs.addAll(defaultProfile.getOutputs());
        }
        
        return outputs;
    }
    
}
