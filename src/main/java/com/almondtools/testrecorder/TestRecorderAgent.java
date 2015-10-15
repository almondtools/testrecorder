package com.almondtools.testrecorder;

import static java.util.stream.Collectors.toList;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.almondtools.testrecorder.generator.TestGenerator;

public class TestRecorderAgent {

	public static void premain(String agentArgs, Instrumentation inst) {
		Properties properties = loadProperties(agentArgs);
		List<String> packages = Stream.of(properties.getProperty("packages", "").split(","))
			.map(pkg -> pkg.replace('.', '/'))
			.collect(toList());
		inst.addTransformer(new SnapshotInstrumentor(packages));

		Consumer<GeneratedSnapshot> consumer = Optional.ofNullable(properties.getProperty("consumer"))
			.map(name -> loadConsumer(name, properties))
			.orElse(new TestGenerator());
		System.out.println("using consumer: " + consumer.getClass());

		SnapshotGenerator.setSnapshotConsumer(consumer);
	}

	@SuppressWarnings("unchecked")
	private static Consumer<GeneratedSnapshot> loadConsumer(String name, Properties properties) {
		try {
			Class<?> clazz = Class.forName(name);
			try {
				Constructor<?> constructor = clazz.getConstructor(Properties.class);
				return (Consumer<GeneratedSnapshot>) constructor.newInstance(properties);
			} catch (NoSuchMethodException e) {
				return (Consumer<GeneratedSnapshot>) clazz.newInstance();
			}
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}

	private static Properties loadProperties(String agentArgs) {
		Properties properties = new Properties();
		if (agentArgs != null) {
			try {
				properties.load(new FileInputStream(agentArgs));
				return properties;
			} catch (RuntimeException | IOException e) {
				System.err.println("loading properties from " + agentArgs + " failed.");
			}
		}
		try {
			properties.load(new FileInputStream("testrecorder.properties"));
			return properties;
		} catch (IOException e) {
			System.err.println("loading properties from testrecorder.properties failed.");
		}
		return properties;
	}

}
