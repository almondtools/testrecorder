package net.amygdalum.testrecorder.util;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.junit.Test;


public class WorkSetTest {

	@Test
	public void testConstructor() throws Exception {
		assertThat(new WorkSet<>().hasMoreElements(), equalTo(false));
		assertThat(new WorkSet<>(new LinkedList<>(asList("A"))).hasMoreElements(), equalTo(true));
	}

	@Test
	public void testEnqueueDequeueSingle() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.add("A");
		assertThat(workSet.hasMoreElements(), equalTo(true));
		String element = workSet.remove();
		assertThat(workSet.hasMoreElements(), equalTo(false));
		assertThat(element, equalTo("A"));
	}

	@Test
	public void testEnqueueDequeueOneElementList() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.addAll(asList("A"));
		assertThat(workSet.hasMoreElements(), equalTo(true));
		String element = workSet.remove();
		assertThat(workSet.hasMoreElements(), equalTo(false));
		assertThat(element, equalTo("A"));
	}

	@Test
	public void testEnqueueDequeueTwice() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.addAll(asList("A", "B"));
		assertThat(workSet.hasMoreElements(), equalTo(true));
		String a = workSet.remove();
		assertThat(workSet.hasMoreElements(), equalTo(true));
		assertThat(a, equalTo("A"));
		String b = workSet.remove();
		assertThat(workSet.hasMoreElements(), equalTo(false));
		assertThat(b, equalTo("B"));
	}

	@Test
	public void testEnqueueTwiceDequeueTwice() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.addAll(asList("A"));
		workSet.addAll(asList("B"));
		assertThat(workSet.hasMoreElements(), equalTo(true));
		String a = workSet.remove();
		assertThat(workSet.hasMoreElements(), equalTo(true));
		assertThat(a, equalTo("A"));
		String b = workSet.remove();
		assertThat(workSet.hasMoreElements(), equalTo(false));
		assertThat(b, equalTo("B"));
	}
	
	@Test
	public void testNoReenqueuings() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.addAll(asList("A", "B"));
		String a = workSet.remove();
		assertThat(a, equalTo("A"));
		workSet.add("A");
		String b = workSet.remove();
		assertThat(b, equalTo("B"));
		assertThat(workSet.hasMoreElements(), equalTo(false));
	}

	@Test(expected=NoSuchElementException.class)
	public void testDequeueEmpty() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.remove();
	}

	@Test
	public void testToStringFresh() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.addAll(asList("A","B"));
		assertThat(workSet.toString(), equalTo("{A, B}"));
	}

	@Test
	public void testToStringUsed() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.addAll(asList("A","B","C"));
		workSet.remove();
		assertThat(workSet.toString(), equalTo("{B, C | A}"));
	}

}
