package net.amygdalum.testrecorder.arch;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

public class ArchitectureTest {

	private static JavaClasses classes;

	@BeforeAll
	public static void beforeAll() throws Exception {
		classes = new ClassFileImporter()
			.importPath("target/classes");
	}

	@Test
	void testArchitecturalLayers() {
		layeredArchitecture()
			.layer("runtime").definedBy("net.amygdalum.testrecorder.runtime")
			.layer("fakeio").definedBy("net.amygdalum.testrecorder.fakeio")
			.layer("asm").definedBy("net.amygdalum.testrecorder.asm")
			.layer("util").definedBy("net.amygdalum.testrecorder.util")

			.whereLayer("fakeio").mayNotBeAccessedByAnyLayer()
			.whereLayer("runtime").mayOnlyBeAccessedByLayers(
				"fakeio") // and from outside module
			.whereLayer("asm").mayOnlyBeAccessedByLayers(
				"runtime",
				"fakeio")
			.whereLayer("util").mayOnlyBeAccessedByLayers(
				"runtime",
				"fakeio",
				"asm")
			.check(classes);
	}

	@Test
	void testNoCycles() {
		slices()
			.matching("net.amygdalum.(**)")
			.should().beFreeOfCycles()
			.check(classes);
	}
}
