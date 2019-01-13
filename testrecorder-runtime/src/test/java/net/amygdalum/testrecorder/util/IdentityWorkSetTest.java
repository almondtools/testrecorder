package net.amygdalum.testrecorder.util;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class IdentityWorkSetTest {

	private IdentityWorkSet<String> workSet;

	@BeforeEach
	public void before() throws Exception {
		workSet = new IdentityWorkSet<>();
	}

	@Nested
	class testHasMoreElements {
		@Test
		void onFresh() throws Exception {
			assertThat(workSet.hasMoreElements()).isEqualTo(false);
		}

		@Test
		void onInitiallyFilled() throws Exception {
			workSet = new IdentityWorkSet<>(new LinkedList<>(asList("str", "str")));

			assertThat(workSet).containsExactly("str");
			assertThat(workSet.getDone()).isEmpty();
		}

	}

	@Nested
	class testAdd {
		@Test
		void multipleAdds() throws Exception {
			workSet.add("str");
			workSet.add("str");

			assertThat(workSet).containsExactly("str");
			assertThat(workSet.getDone()).isEmpty();
		}

		@Test
		void addRemoveAdd() throws Exception {
			workSet.add("str");
			workSet.remove();
			workSet.add("str");

			assertThat(workSet).isEmpty();
			assertThat(workSet.getDone()).containsExactly("str");
		}

		@Test
		void addOnEqual() throws Exception {
			workSet.add("str");
			workSet.add(new String("str"));

			assertThat(workSet).containsExactly("str", "str");
			assertThat(workSet.getDone()).isEmpty();
		}

		@Test
		void addRemoveAddOnEqual() throws Exception {
			workSet.add("str");
			workSet.remove();
			workSet.add(new String("str"));

			assertThat(workSet).containsExactly("str");
			assertThat(workSet.getDone()).containsExactly("str");
		}

		@Test
		void addMultipleRemoveMultipleOnEqual() throws Exception {
			workSet.add("str");
			workSet.add(new String("str"));
			workSet.remove();
			workSet.remove();

			assertThat(workSet).isEmpty();
			assertThat(workSet.getDone()).containsExactly("str", "str");
		}
	}
}
