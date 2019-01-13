package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.testrecorder.runtime.DefaultComparisonStrategy.all;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Bean;
import net.amygdalum.testrecorder.util.testobjects.Dubble;

public class DefaultComparisonStrategyTest {

	@Nested
	class testExtend {
		@Test
		void common() throws Exception {
			Bean left = new Bean();
			left.setAttribute("att");
			Bean right = new Bean();
			right.setAttribute("att");
			assertThat(all().extend(new GenericComparison("@", left, right))).containsExactly(
				GenericComparison.from("@", "attribute", left, right));
		}

		@Test
		void multipleFields() throws Exception {
			Dubble left = new Dubble("aatt", "batt");
			Dubble right = new Dubble("aatt", "bdiff");

			assertThat(all().extend(new GenericComparison("@", left, right))).containsExactly(
				GenericComparison.from("@", "a", left, right),
				GenericComparison.from("@", "b", left, right));
		}
	}
}
