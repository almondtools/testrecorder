package com.almondtools.invivoderived.examples;

import static com.almondtools.invivoderived.analyzer.SnapshotGenerator.setSnapshotConsumer;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.almondtools.invivoderived.GeneratedSnapshot;
import com.almondtools.invivoderived.analyzer.SnapshotInstrumentor;
import com.almondtools.invivoderived.generator.TestGenerator;

public class FizzBuzzTest {

	private SnapshotInstrumentor instrumentor;
	private List<GeneratedSnapshot> snapshots;
	private TestGenerator testGenerator;

	@Before
	public void before() throws Exception {
		instrumentor = new SnapshotInstrumentor();
		instrumentor.register("com.almondtools.invivoderived.examples.FizzBuzz");
		snapshots = new ArrayList<>();
		testGenerator = new TestGenerator();
		setSnapshotConsumer(testGenerator.andThen(snapshot -> {
			snapshots.add(snapshot);
		}));
	}

	@After
	public void after() throws Exception {
		testGenerator.writeTests(Paths.get("target/generated"), FizzBuzz.class);
	}
	
	@Test
	public void testSnapshotSize() throws Exception {
		FizzBuzz.main(new String[0]);
		assertThat(snapshots, hasSize(100));
	}

}