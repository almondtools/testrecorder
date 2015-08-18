package com.almondtools.invitroderivatives.scenarios;

import static com.almondtools.invitroderivatives.analyzer.SnapshotGenerator.setSnapshotConsumer;

import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.almondtools.invitroderivatives.analyzer.SnapshotInstrumentor;
import com.almondtools.invitroderivatives.generator.TestGenerator;

public class SideEffectsTest {

	private SnapshotInstrumentor instrumentor;
	private TestGenerator testGenerator;

	@Before
	public void before() throws Exception {
		instrumentor = new SnapshotInstrumentor();
		instrumentor.register("com.almondtools.invitroderivatives.scenarios.SideEffects");
		testGenerator = new TestGenerator();
		setSnapshotConsumer(testGenerator);
	}

	@After
	public void after() throws Exception {
		testGenerator.writeTests(Paths.get("target/generated"), SideEffects.class);
	}
	
	@Test
	public void testSnapshotSize() throws Exception {
		SideEffects.main(new String[0]);
	}

}