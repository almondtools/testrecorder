package net.amygdalum.testrecorder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class ScheduledTestGenerator extends TestGenerator {

	private static volatile Set<ScheduledTestGenerator> dumpOnShutDown;

	private Path path;
	private int counter;
	private int counterInterval;
	private int counterMaximum;
	private long start;
	private long timeInterval;
	private String classNameTemplate;

	public ScheduledTestGenerator(Class<? extends Runnable> initializer) {
		super(initializer);
		this.counter = 0;
		this.start = System.currentTimeMillis();
		this.path = Paths.get(".");
	}

	public ScheduledTestGenerator withDumpTo(Path path) {
		this.path = path;
		return this;
	}

	public ScheduledTestGenerator withDumpMaximum(int maximum) {
		this.counterMaximum = maximum;
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
			addDumpOnShutdown();
		}
		return this;
	}

	private synchronized void addDumpOnShutdown() {
		if (dumpOnShutDown == null) {
			dumpOnShutDown = new HashSet<>();
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

				@Override
				public void run() {
					for (ScheduledTestGenerator gen : dumpOnShutDown) {
						gen.dumpResults();
					}
				}

			}));
		}
		dumpOnShutDown.add(this);
	}

	@Override
	public void accept(ContextSnapshot snapshot) {
		super.accept(snapshot);
		checkCounterInterval();
		checkTimeInterval();
	}

	private void checkCounterInterval() {
		counter++;
		if (counterInterval > 0 && counter <= counterMaximum && counter % counterInterval == 0) {
			dumpResults();
		}
	}

	private void checkTimeInterval() {
		long oldStart = start;
		start = System.currentTimeMillis();
		if (timeInterval > 0 && start - oldStart >= timeInterval && counter <= counterMaximum) {
			dumpResults();
		}
	}

	public void dumpResults() {
		andThen(() -> {
			writeResults(path);
			clearResults();
		});
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
