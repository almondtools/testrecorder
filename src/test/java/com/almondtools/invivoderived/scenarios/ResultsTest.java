package com.almondtools.invivoderived.scenarios;

import static com.almondtools.invivoderived.analyzer.SnapshotGenerator.setSnapshotConsumer;

import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.almondtools.invivoderived.analyzer.SnapshotInstrumentor;
import com.almondtools.invivoderived.generator.TestGenerator;

public class ResultsTest {

	private SnapshotInstrumentor instrumentor;
	private TestGenerator testGenerator;

	@Before
	public void before() throws Exception {
		instrumentor = new SnapshotInstrumentor();
		instrumentor.register("com.almondtools.invivoderived.scenarios.Results");
		testGenerator = new TestGenerator();
		setSnapshotConsumer(testGenerator);
	}

	@After
	public void after() throws Exception {
		testGenerator.writeTests(Paths.get("target/generated"), Results.class);
	}
	

	@Test
	public void testSnapshotSize() throws Exception {
		Results.main(new String[0]);
	}

}