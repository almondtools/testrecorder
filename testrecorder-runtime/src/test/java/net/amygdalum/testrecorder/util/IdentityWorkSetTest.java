package net.amygdalum.testrecorder.util;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IdentityWorkSetTest {

	private IdentityWorkSet<String> workSet;

	@BeforeEach
	public void before() throws Exception {
		workSet = new IdentityWorkSet<>();
	}

	@Test
	void testConstructor() throws Exception {
		assertThat(workSet.hasMoreElements()).isEqualTo(false);
	}

	@Test
	void testConstructorWithBase() throws Exception {
		workSet = new IdentityWorkSet<>(new LinkedList<>(asList("str","str")));

		assertThat(workSet).containsExactly("str");
		assertThat(workSet.getDone()).isEmpty();
	}

	@Test
	void testAdd2() throws Exception {
		workSet.add("str");
		workSet.add("str");

		assertThat(workSet).containsExactly("str");
		assertThat(workSet.getDone()).isEmpty();
	}

	@Test
	void testAddRemoveAdd() throws Exception {
		workSet.add("str");
		workSet.remove();
		workSet.add("str");

		assertThat(workSet).isEmpty();
		assertThat(workSet.getDone()).containsExactly("str");
	}

	@Test
	void testAdd2OnEqual() throws Exception {
		workSet.add("str");
		workSet.add(new String("str"));

		assertThat(workSet).containsExactly("str", "str");
		assertThat(workSet.getDone()).isEmpty();
	}

	@Test
	void testAddRemoveAddOnEqual() throws Exception {
		workSet.add("str");
		workSet.remove();
		workSet.add(new String("str"));

		assertThat(workSet).containsExactly("str");
		assertThat(workSet.getDone()).containsExactly("str");
	}

	@Test
	void testAdd2Remove2OnEqual() throws Exception {
		workSet.add("str");
		workSet.add(new String("str"));
		workSet.remove();
		workSet.remove();

		assertThat(workSet).isEmpty();
		assertThat(workSet.getDone()).containsExactly("str", "str");
	}

}
