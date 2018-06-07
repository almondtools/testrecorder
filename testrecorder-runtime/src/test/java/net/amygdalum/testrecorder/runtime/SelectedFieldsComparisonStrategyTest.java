package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.testrecorder.runtime.SelectedFieldsComparisonStrategy.comparingFields;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Bean;
import net.amygdalum.testrecorder.util.testobjects.Dubble;

public class SelectedFieldsComparisonStrategyTest {

	@Test
	void testExtend() throws Exception {
		Bean left = new Bean();
		left.setAttribute("att");
		Bean right = new Bean();
		right.setAttribute("att");
		assertThat(comparingFields("attribute").extend(new GenericComparison("@",  left, right))).containsExactly(
			GenericComparison.from("@", "attribute", left, right));
	}

	@Test
	void testExtendMultipleFields() throws Exception {
		Dubble left = new Dubble("aatt", "batt");
		Dubble right = new Dubble("aatt", "bdiff");

		assertThat(comparingFields("a").extend(new GenericComparison("@",  left, right))).containsExactly(
			GenericComparison.from("@", "a", left, right));
		assertThat(comparingFields("b").extend(new GenericComparison("@",  left, right))).containsExactly(
			GenericComparison.from("@", "b", left, right));
		assertThat(comparingFields("a", "b").extend(new GenericComparison("@",  left, right))).containsExactlyInAnyOrder(
			GenericComparison.from("@", "a", left, right),
			GenericComparison.from("@", "b", left, right));
	}

}
