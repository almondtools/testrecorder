package com.almondtools.invivoderived.examples;

import static com.almondtools.invivoderived.SnapshotGenerator.setSnapshotConsumer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.almondtools.invivoderived.GeneratedSnapshot;
import com.almondtools.invivoderived.SnapshotInstrumentor;
import com.almondtools.invivoderived.generator.TestGenerator;

public class FizzBuzzTest {

	private SnapshotInstrumentor instrumentor;
	private List<GeneratedSnapshot> snapshots;
	private TestGenerator testGenerator;

	public static void main(String[] args) throws IOException {
		new FizzBuzzTest().run();
	}
	
	public void run() throws IOException {
		instrumentor = new SnapshotInstrumentor();
		instrumentor.register("com.almondtools.invivoderived.examples.FizzBuzz");
		snapshots = new ArrayList<>();
		testGenerator = new TestGenerator();
		setSnapshotConsumer(testGenerator.andThen(snapshot -> {
			snapshots.add(snapshot);
		}));

		FizzBuzz.main(new String[0]);
		
		testGenerator.writeTests(Paths.get("target/generated"));
	}

}