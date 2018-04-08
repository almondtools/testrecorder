package net.amygdalum.testrecorder;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.List;

import net.amygdalum.testrecorder.profile.Classes;
import net.amygdalum.testrecorder.profile.ExcludeExplicitExcluded;
import net.amygdalum.testrecorder.profile.ExcludeGenerated;
import net.amygdalum.testrecorder.profile.ExcludeStatic;
import net.amygdalum.testrecorder.profile.Fields;
import net.amygdalum.testrecorder.profile.Methods;
import net.amygdalum.testrecorder.profile.SerializationProfile;

public class DefaultSerializationProfile implements SerializationProfile {

    public static final List<Fields> DEFAULT_FIELD_EXCLUDES = asList(
        new ExcludeExplicitExcluded(),
        new ExcludeGenerated(),
        new ExcludeStatic());

    public static final List<Classes> DEFAULT_CLASS_EXCLUSIONS = emptyList();

    public static final List<Fields> DEFAULT_GLOBAL_FIELDS = emptyList();
    public static final List<Methods> INPUT = emptyList();
    public static final List<Methods> OUTPUT = emptyList();

    @Override
    public List<Fields> getFieldExclusions() {
        return DEFAULT_FIELD_EXCLUDES;
    }

    @Override
    public List<Classes> getClassExclusions() {
        return DEFAULT_CLASS_EXCLUSIONS;
    }

    @Override
    public List<Fields> getGlobalFields() {
        return DEFAULT_GLOBAL_FIELDS;
    }
    
    @Override
    public List<Methods> getInputs() {
        return INPUT;
    }
    
    @Override
    public List<Methods> getOutputs() {
        return OUTPUT;
    }

    @Override
    public List<Classes> getClasses() {
        return emptyList();
    }

}
