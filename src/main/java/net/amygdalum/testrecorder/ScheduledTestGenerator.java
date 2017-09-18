package net.amygdalum.testrecorder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * A configurable SnapshotConsumer client that writes tests to the file system
 */
public class ScheduledTestGenerator extends TestGenerator {

	private static volatile Set<ScheduledTestGenerator> dumpOnShutDown;

	private Path path;
	private int counter;
	private int counterInterval;
	private int counterMaximum;
	private long start;
	private long timeInterval;
	private String classNameTemplate;

	public ScheduledTestGenerator() {
		this.counterMaximum = -1;
		this.counter = 0;
		this.start = System.currentTimeMillis();
		this.path = Paths.get(".");
	}

	/**
	 * specifies the path where test files are dumped
	 * @param path the path where test files are dumped
	 * @return this
	 */
	public ScheduledTestGenerator withDumpTo(Path path) {
		this.path = path;
		return this;
	}

	/**
	 * specifies how many tests will be dumped
	 * @param maximum the maximum of tests to be dumped
	 * @return this
	 */
	public ScheduledTestGenerator withDumpMaximum(int maximum) {
		this.counterMaximum = maximum;
		return this;
	}

	/**
	 * specifies the generic class name of a dumped test file.
	 * The argument {@code template} is a template string using characters and following template expressions:
	 *     ${class} - the name of the class under test
     *     ${counter} - the number of the generated test
     *     ${millis} - the time stamp corresponding to the generated test
	 * 
	 * @param template a template string build of characters and variable references
	 * @return this
	 */
	public ScheduledTestGenerator withClassName(String template) {
		this.classNameTemplate = template;
		return this;
	}

	/**
	 * specifies a time interval of latency between two test renderings. Any snapshot arriving at least this 
	 * time interval after the last dump will trigger a new dump. 
	 * @param timeInterval the interval of latency
	 * @return this
	 */
	public ScheduledTestGenerator withDumpOnTimeInterval(long timeInterval) {
		this.timeInterval = timeInterval;
		return this;
	}

	/**
	 * specifies a number of tests that should trigger a new dump. Every time the list of recorded snapshots
	 * exceeds this number a new dump will be triggered.
	 * @param counterInterval the number that will trigger a new dump
	 * @return this
	 */
	public ScheduledTestGenerator withDumpOnCounterInterval(int counterInterval) {
		this.counterInterval = counterInterval;
		return this;
	}

	/**
	 * specifies that all pending tests should be dumped at shutdown time
	 * @param shutDown true if pending tests should be dumped at shutdown, false otherwise 
	 * @return this
	 */
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
				    if (dumpOnShutDown != null) {
				        for (ScheduledTestGenerator gen : dumpOnShutDown) {
				            gen.dumpResults();
				        }
				    }
				}

			}, "$generate-shutdown"));
		}
		dumpOnShutDown.add(this);
	}

	@Override
	public void accept(ContextSnapshot snapshot) {
        if (counterMaximum > 0 && counter >= counterMaximum) {
            return;
        }
        counter++;
		super.accept(snapshot);
		checkCounterInterval();
		checkTimeInterval();
	}

	private void checkCounterInterval() {
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
		andThen(() -> {
			writeResults(path);
			clearResults();
		});
	}

	@Override
	public String computeClassName(ClassDescriptor clazz) {
		if (classNameTemplate == null) {
			return super.computeClassName(clazz);
		}
		return classNameTemplate
			.replace("${class}", clazz.getSimpleName())
			.replace("${counter}", String.valueOf(counter))
			.replace("${millis}", String.valueOf(start));
	}

}
