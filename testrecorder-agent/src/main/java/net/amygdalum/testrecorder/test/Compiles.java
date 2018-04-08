package net.amygdalum.testrecorder.test;

import java.util.function.Consumer;

import org.assertj.core.api.SoftAssertions;

import net.amygdalum.testrecorder.dynamiccompile.DynamicClassCompiler;
import net.amygdalum.testrecorder.dynamiccompile.DynamicClassCompilerException;
import net.amygdalum.testrecorder.dynamiccompile.RenderedTest;

public class Compiles implements Consumer<RenderedTest> {

	private SoftAssertions softly;
	private DynamicClassCompiler compiler;

	public Compiles() {
		softly = new SoftAssertions();
		compiler = new DynamicClassCompiler();
	}

	public static Compiles compiles() {
		return new Compiles();
	}

	@Override
	public void accept(RenderedTest test) {
		try {
			Class<?> clazz = compiler.compile(test.getTestCode(), test.getTestClassLoader());
			softly.assertThat(clazz).withFailMessage("compile succesful, but class cannot be loaded:%n %s", test.getTestCode()).isNotNull();
		} catch (DynamicClassCompilerException e) {
			softly.fail(e.getMessage());
			for (String msg : e.getDetailMessages()) {
				softly.fail(msg);
			}
		}
		softly.assertAll();
	}

}
