package com.almondtools.invitroderivatives.examples;

import static com.almondtools.invitroderivatives.analyzer.SnapshotGenerator.setSnapshotConsumer;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.almondtools.invitroderivatives.GeneratedSnapshot;
import com.almondtools.invitroderivatives.analyzer.SnapshotInstrumentor;
import com.almondtools.invitroderivatives.generator.TestGenerator;

public class OtherFizzBuzzTest {

	private SnapshotInstrumentor instrumentor;
	private List<GeneratedSnapshot> snapshots;
	private TestGenerator testGenerator;

	@Before
	public void before() throws Exception {
		instrumentor = new SnapshotInstrumentor();
		instrumentor.register("com.almondtools.invitroderivatives.examples.OtherFizzBuzz");
		snapshots = new ArrayList<>();
		testGenerator = new TestGenerator();
		setSnapshotConsumer(testGenerator.andThen(snapshot -> {
			snapshots.add(snapshot);
		}));
	}

	@After
	public void after() throws Exception {
		testGenerator.writeTests(Paths.get("target/generated"), OtherFizzBuzz.class);
	}
	
	@Test
	public void testSnapshotSize() throws Exception {
		OtherFizzBuzz.main(new String[0]);
		assertThat(snapshots, hasSize(100));
	}

}