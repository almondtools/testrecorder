package net.amygdalum.testrecorder;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.DefaultSerializationProfile.DEFAULT_CLASS_EXCLUSIONS;
import static net.amygdalum.testrecorder.DefaultSerializationProfile.DEFAULT_FIELD_EXCLUDES;
import static net.amygdalum.testrecorder.DefaultSerializationProfile.DEFAULT_GLOBAL_FIELDS;
import static net.amygdalum.testrecorder.DefaultSerializationProfile.INPUT;
import static net.amygdalum.testrecorder.DefaultSerializationProfile.OUTPUT;

import java.util.List;

import net.amygdalum.testrecorder.profile.Classes;
import net.amygdalum.testrecorder.profile.Fields;
import net.amygdalum.testrecorder.profile.Methods;
import net.amygdalum.testrecorder.profile.PerformanceProfile;
import net.amygdalum.testrecorder.profile.SerializationProfile;

public class DefaultAgentProfile implements SerializationProfile, PerformanceProfile {

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

    @Override
    public long getTimeoutInMillis() {
        return 100_000;
    }

	@Override
	public long getIdleTime() {
		return 10_000;
	}

}
