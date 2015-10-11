package com.almondtools.testrecorder.examples;

import static com.almondtools.testrecorder.SnapshotGenerator.setSnapshotConsumer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.almondtools.testrecorder.GeneratedSnapshot;
import com.almondtools.testrecorder.SnapshotInstrumentor;
import com.almondtools.testrecorder.generator.TestGenerator;

public class BeanFizzBuzzTest {

	private SnapshotInstrumentor instrumentor;
	private List<GeneratedSnapshot> snapshots;
	private TestGenerator testGenerator;

	public static void main(String[] args) throws IOException {
		new BeanFizzBuzzTest().run();
	}
	
	public void run() throws IOException {
		instrumentor = new SnapshotInstrumentor();
		instrumentor.register("com.almondtools.testrecorder.examples.BeanFizzBuzz");
		snapshots = new ArrayList<>();
		testGenerator = new TestGenerator();
		setSnapshotConsumer(testGenerator.andThen(snapshot -> {
			snapshots.add(snapshot);
		}));

		BeanFizzBuzz.main(new String[0]);
		
		testGenerator.writeTests(Paths.get("target/generated"));
	}

}