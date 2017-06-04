package net.amygdalum.testrecorder.util;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

public class IORecorder extends BlockJUnit4ClassRunner {

    private IORecorderClassLoader loader;
    private volatile SetupInput setupInput;
    private volatile ExpectedOutput expectedOutput;

    public IORecorder(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Object createTest() throws Exception {
        Object test = super.createTest();
        for (Field field : test.getClass().getDeclaredFields()) {
            if (field.getType() == ExpectedOutput.class) {
                field.setAccessible(true);
                RecordOutput outputs = test.getClass().getAnnotation(RecordOutput.class);
                field.set(test, fetchExpectedOutput(outputs.signatures()));
            }
            if (field.getType() == SetupInput.class) {
                field.setAccessible(true);
                RecordInput inputs = test.getClass().getAnnotation(RecordInput.class);
                field.set(test, fetchSetupInput(inputs.signatures()));
            }
        }
        return test;
    }

    @Override
    protected TestClass createTestClass(Class<?> testClass) {
        return super.createTestClass(instrumented(testClass));
    }

    private Class<?> instrumented(Class<?> klass) {
        try {
            RecordOutput outputs = klass.getAnnotation(RecordOutput.class);
            RecordInput inputs = klass.getAnnotation(RecordInput.class);
            loader = createLoader(klass, inputs, outputs);
            return loader.loadClass(klass.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private IORecorderClassLoader createLoader(Class<?> klass, RecordInput input, RecordOutput output) {
        Set<String> classes = new LinkedHashSet<>();
        if (output != null) {
            classes.addAll(asList(output.value()));
        }
        if (input != null) {
            classes.addAll(asList(input.value()));
        }
        SetupInput in = fetchSetupInput(input == null ? new String[0] : input.signatures());
        ExpectedOutput out = fetchExpectedOutput(output == null ? new String[0] : output.signatures());
        return new IORecorderClassLoader(klass, in, out, classes);
    }

    private synchronized ExpectedOutput fetchExpectedOutput(String[] signatures) {
        if (expectedOutput == null) {
            expectedOutput = new ExpectedOutput(signatures);
        }
        return expectedOutput;
    }

    private synchronized SetupInput fetchSetupInput(String[] signatures) {
        if (setupInput == null) {
            setupInput = new SetupInput(signatures);
        }
        return setupInput;
    }

}
