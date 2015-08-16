package com.almondtools.iit.exampletests;

import static com.almondtools.iit.analyzer.SnapshotGenerator.setSnapshotConsumer;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.almondtools.iit.GeneratedSnapshot;
import com.almondtools.iit.analyzer.SnapshotInstrumentor;
import com.almondtools.iit.examples.OtherFizzBuzz;
import com.almondtools.iit.generator.TestGenerator;

public class OtherFizzBuzzTest {

	private SnapshotInstrumentor instrumentor;
	private List<GeneratedSnapshot> snapshots;
	private TestGenerator testGenerator;

	@Before
	public void before() throws Exception {
		instrumentor = new SnapshotInstrumentor();
		instrumentor.register("com.almondtools.iit.examples.OtherFizzBuzz");
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