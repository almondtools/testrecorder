package net.amygdalum.testrecorder.arch;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.ImportOptions;

public class ArchitectureTest {

	
	private static JavaClasses classes;

	@BeforeAll
	public static void beforeAll() throws Exception {
		classes = new ClassFileImporter()
			.importClasspath(new ImportOptions()
				.with(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
				.with(location -> location.contains("/net/amygdalum/testrecorder/")));
	}

	@Test
	void testArchitecturalLayers() {
		layeredArchitecture()
			.layer("codeserializer").definedBy("net.amygdalum.testrecorder.codeserializer")
			.layer("callsiterecorder").definedBy("net.amygdalum.testrecorder.callsiterecorder")
			.layer("dynamiccompile").definedBy("net.amygdalum.testrecorder.dynamiccompile")
			.layer("data").definedBy("net.amygdalum.testrecorder.data")
			.layer("main").definedBy("net.amygdalum.testrecorder")
			.layer("configurator").definedBy("net.amygdalum.testrecorder.configurator")
			.layer("runtime").definedBy("net.amygdalum.testrecorder.runtime")
			.layer("profile").definedBy("net.amygdalum.testrecorder.profile")
			.layer("extensionpoint").definedBy("net.amygdalum.testrecorder.extensionpoint")
			.layer("serializers").definedBy("net.amygdalum.testrecorder.serializers")
			.layer("generator").definedBy("net.amygdalum.testrecorder.generator")
			.layer("deserializers").definedBy("net.amygdalum.testrecorder.deserializers")
			.layer("builder").definedBy("net.amygdalum.testrecorder.deserializers.builder")
			.layer("matcher").definedBy("net.amygdalum.testrecorder.deserializers.matcher")
			.layer("evaluator").definedBy("net.amygdalum.testrecorder.evaluator")
			.layer("values").definedBy("net.amygdalum.testrecorder.values")
			.layer("types").definedBy("net.amygdalum.testrecorder.types")
			.layer("fakeio").definedBy("net.amygdalum.testrecorder.fakeio")
			.layer("util").definedBy("net.amygdalum.testrecorder.util")
			.layer("asm").definedBy("net.amygdalum.testrecorder.asm")

			.whereLayer("codeserializer").mayNotBeAccessedByAnyLayer() // only from outside module
			.whereLayer("callsiterecorder").mayNotBeAccessedByAnyLayer() // only from outside module
			.whereLayer("dynamiccompile").mayNotBeAccessedByAnyLayer() // only from outside module
			.whereLayer("data").mayNotBeAccessedByAnyLayer() // reserved for future use
			.whereLayer("main").mayOnlyBeAccessedByLayers(
				"codeserializer",
				"callsiterecorder",
				"configurator",
				"generator")
			.whereLayer("configurator").mayOnlyBeAccessedByLayers(
				"main",
				"callsiterecorder")
			.whereLayer("profile").mayOnlyBeAccessedByLayers(
				"codeserializer",
				"callsiterecorder",
				"configurator",
				"main",
				"generator",
				"builder",
				"matcher")
			.whereLayer("extensionpoint").mayOnlyBeAccessedByLayers(
				"main",
				"profile",
				"types",
				"serializers",
				"generator",
				"builder",
				"matcher")
			.whereLayer("serializers").mayOnlyBeAccessedByLayers(
				"main",
				"codeserializer",
				"callsiterecorder",
				"configurator")
			.whereLayer("generator").mayOnlyBeAccessedByLayers(
				"main",
				"codeserializer",
				"callsiterecorder",
				"configurator")
			.whereLayer("deserializers").mayOnlyBeAccessedByLayers(
				"main",
				"codeserializer",
				"callsiterecorder",
				"configurator",
				"builder",
				"matcher",
				"generator")
			.whereLayer("builder").mayOnlyBeAccessedByLayers(
				"main",
				"codeserializer",
				"callsiterecorder",
				"configurator",
				"generator")
			.whereLayer("matcher").mayOnlyBeAccessedByLayers(
				"main",
				"codeserializer",
				"callsiterecorder",
				"configurator",
				"generator")
			.whereLayer("evaluator").mayOnlyBeAccessedByLayers(
				"main",
				"codeserializer",
				"callsiterecorder",
				"configurator",
				"generator")
			.whereLayer("values").mayOnlyBeAccessedByLayers(
				"main",
				"codeserializer",
				"callsiterecorder",
				"configurator",
				"generator",
				"serializers",
				"deserializers",
				"matcher",
				"builder",
				"evaluator")
			.whereLayer("types").mayOnlyBeAccessedByLayers(
				"main",
				"codeserializer",
				"callsiterecorder",
				"configurator",
				"profile",
				"generator",
				"serializers",
				"deserializers",
				"matcher",
				"builder",
				"evaluator",
				"values")
			.whereLayer("fakeio").mayOnlyBeAccessedByLayers(
				"main",
				"codeserializer",
				"callsiterecorder",
				"configurator",
				"generator",
				"serializers",
				"deserializers",
				"matcher",
				"builder")
			.whereLayer("asm").mayOnlyBeAccessedByLayers(
				"main",
				"codeserializer",
				"callsiterecorder",
				"configurator",
				"profile",
				"serializers",
				"fakeio",
				"types",
				"values")
			.whereLayer("util").mayOnlyBeAccessedByLayers(
				"main",
				"runtime",
				"codeserializer",
				"callsiterecorder",
				"configurator",
				"profile",
				"generator",
				"serializers",
				"deserializers",
				"matcher",
				"builder",
				"evaluator",
				"dynamiccompile",
				"fakeio",
				"data",
				"values",
				"types",
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
