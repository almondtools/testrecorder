package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.testrecorder.runtime.DefaultComparisonStrategy.all;
import static net.amygdalum.testrecorder.runtime.ListEnabledComparisonStrategy.extendByLists;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Bean;
import net.amygdalum.testrecorder.util.testobjects.Dubble;
import net.amygdalum.testrecorder.util.testobjects.PublicList;

public class ListEnabledComparisonStrategyTest {

	@Test
	void testExtend() throws Exception {
		Bean left = new Bean();
		left.setAttribute("att");
		Bean right = new Bean();
		right.setAttribute("att");
		assertThat(extendByLists(all()).extend(new GenericComparison("@", left, right))).containsExactly(
			GenericComparison.from("@", "attribute", left, right));
	}

	@Test
	void testExtendMultipleFields() throws Exception {
		Dubble left = new Dubble("aatt", "batt");
		Dubble right = new Dubble("aatt", "bdiff");

		assertThat(extendByLists(all()).extend(new GenericComparison("@", left, right))).containsExactly(
			GenericComparison.from("@", "a", left, right),
			GenericComparison.from("@", "b", left, right));
	}

	@Test
	void testExtendLists() throws Exception {
		PublicList<String> left = new PublicList<>();
		left.add("aatt");
		left.add("batt");
		PublicList<String> right = new PublicList<>();
		right.add("aatt");
		right.add("bdiff");

		assertThat(extendByLists(all()).extend(new GenericComparison("@", left, right))).containsExactly(
			new GenericComparison("@[0]", "aatt","aatt"),
			new GenericComparison("@[1]", "batt","bdiff"));
	}

	@Test
	void testExtendListsDifferentSize() throws Exception {
		PublicList<String> left = new PublicList<>();
		left.add("aatt");
		left.add("batt");
		PublicList<String> right = new PublicList<>();
		left.add("aatt");
		left.add("bdiff");
		left.add("csurplus");
		
		assertThatThrownBy(() -> extendByLists(all()).extend(new GenericComparison("@", left, right))).isInstanceOf(ComparisonException.class);
		assertThatThrownBy(() -> extendByLists(all()).extend(new GenericComparison("@", right, left))).isInstanceOf(ComparisonException.class);
	}
	
}
