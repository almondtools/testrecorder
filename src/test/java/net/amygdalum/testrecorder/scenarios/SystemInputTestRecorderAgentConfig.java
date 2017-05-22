package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.util.List;

import net.amygdalum.testrecorder.DefaultTestRecorderAgentConfig;

public class SystemInputTestRecorderAgentConfig extends DefaultTestRecorderAgentConfig {

    @Override
    public List<Method> getInputs() {
        try {
            return asList(SystemInput.class.getDeclaredMethod("currentTimeMillis"));
        } catch (NoSuchMethodException | SecurityException e) {
           throw new RuntimeException(e);
        }
    }
}