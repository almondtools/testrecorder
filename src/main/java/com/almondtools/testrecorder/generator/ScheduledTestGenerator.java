package com.almondtools.testrecorder.generator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import com.almondtools.testrecorder.GeneratedSnapshot;

public class ScheduledTestGenerator extends TestGenerator {

	private Path path;
	private int counter;
	private int counterInterval;
	private long start;
	private long timeInterval;
	private String classNameTemplate;
	
	public ScheduledTestGenerator(Properties properties) {
		this.counter = 0;
		this.start = System.currentTimeMillis();
		configure(properties);
	}

	private void configure(Properties properties) {
		configureDumpTo(Optional.ofNullable(properties.getProperty("consumer.dump.to"))
			.map(path -> Paths.get(path))
			.orElse(Paths.get("target/generated")));
		
		configureClassName(Optional.ofNullable(properties.getProperty("consumer.dump.class")));
		
		configureDumpOnShutDown(Optional.ofNullable(properties.getProperty("consumer.dump.shutdown"))
			.map(value -> Boolean.valueOf(value)));
		
		configureDumpOnTimeInterval(Optional.ofNullable(properties.getProperty("consumer.dump.time.interval"))
			.map(value -> parseLongOrNull(value)));
		configureDumpOnCounterInterval(Optional.ofNullable(properties.getProperty("consumer.dump.counter.interval"))
			.map(value -> parseIntOrNull(value)));

	}

	private Integer parseIntOrNull(String value) {
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private Long parseLongOrNull(String value) {
		try {
			return Long.valueOf(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private void configureDumpTo(Path path) {
		this.path = path;
	}

	private void configureClassName(Optional<String> template) {
		this.classNameTemplate = template.orElse(null);
	}

	private void configureDumpOnTimeInterval(Optional<Long> timeInterval) {
		this.timeInterval = timeInterval.orElse(0L);
	}

	private void configureDumpOnCounterInterval(Optional<Integer> counterInterval) {
		this.counterInterval = counterInterval.orElse(0);
	}

	private void configureDumpOnShutDown(Optional<Boolean> value) {
		if (value.orElse(false)) {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

				@Override
				public void run() {
					dumpResults();
				}

			}));
		}
	}
	
	@Override
	public void accept(GeneratedSnapshot snapshot) {
		super.accept(snapshot);
		checkCounterInterval();
		checkTimeInterval();
	}

	private void checkCounterInterval() {
		counter++;
		if (counterInterval > 0 && counter % counterInterval == 0) {
			dumpResults();
		}
	}

	private void checkTimeInterval() {
		long oldStart = start;
		start = System.currentTimeMillis();
		if (timeInterval > 0 && start - oldStart >= timeInterval) {
			dumpResults();
		}
	}

	public void dumpResults() {
		writeResults(path);
		clearResults();
	}

	@Override
	public String computeClassName(Class<?> clazz) {
		if (classNameTemplate == null) {
			return super.computeClassName(clazz);
		}
		return classNameTemplate
			.replace("${class}", clazz.getSimpleName())
			.replace("${counter}", String.valueOf(counter))
			.replace("${millis}", String.valueOf(start));
	}
}
