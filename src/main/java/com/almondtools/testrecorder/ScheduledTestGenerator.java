package com.almondtools.testrecorder;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ScheduledTestGenerator extends TestGenerator {

	private Path path;
	private int counter;
	private int counterInterval;
	private long start;
	private long timeInterval;
	private String classNameTemplate;
	
	public ScheduledTestGenerator() {
		this.counter = 0;
		this.start = System.currentTimeMillis();
		this.path = Paths.get(".");
	}

	public ScheduledTestGenerator withDumpTo(Path path) {
		this.path = path;
		return this;
	}

	public ScheduledTestGenerator withClassName(String template) {
		this.classNameTemplate = template;
		return this;
	}

	public ScheduledTestGenerator withDumpOnTimeInterval(long timeInterval) {
		this.timeInterval = timeInterval;
		return this;
	}

	public ScheduledTestGenerator withDumpOnCounterInterval(int counterInterval) {
		this.counterInterval = counterInterval;
		return this;
	}

	public ScheduledTestGenerator withDumpOnShutDown(boolean shutDown) {
		if (shutDown) {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

				@Override
				public void run() {
					dumpResults();
				}

			}));
		}
		return this;
	}
	
	@Override
	public void accept(MethodSnapshot snapshot) {
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
