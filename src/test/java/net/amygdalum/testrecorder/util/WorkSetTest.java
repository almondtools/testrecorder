package net.amygdalum.testrecorder.util;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.NoSuchElementException;

import org.junit.Test;


public class WorkSetTest {

	@Test
	public void testConstructor() throws Exception {
		assertThat(new WorkSet<>().hasMoreElements(), equalTo(false));
		assertThat(new WorkSet<>(asList("A")).hasMoreElements(), equalTo(true));
		assertThat(new WorkSet<>("A").hasMoreElements(), equalTo(true));
	}

	@Test
	public void testEnqueueDequeueSingle() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.enqueue("A");
		assertThat(workSet.hasMoreElements(), equalTo(true));
		String element = workSet.dequeue();
		assertThat(workSet.hasMoreElements(), equalTo(false));
		assertThat(element, equalTo("A"));
	}

	@Test
	public void testEnqueueDequeueOneElementList() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.enqueue(asList("A"));
		assertThat(workSet.hasMoreElements(), equalTo(true));
		String element = workSet.dequeue();
		assertThat(workSet.hasMoreElements(), equalTo(false));
		assertThat(element, equalTo("A"));
	}

	@Test
	public void testEnqueueDequeueTwice() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.enqueue(asList("A", "B"));
		assertThat(workSet.hasMoreElements(), equalTo(true));
		String a = workSet.dequeue();
		assertThat(workSet.hasMoreElements(), equalTo(true));
		assertThat(a, equalTo("A"));
		String b = workSet.dequeue();
		assertThat(workSet.hasMoreElements(), equalTo(false));
		assertThat(b, equalTo("B"));
	}

	@Test
	public void testEnqueueTwiceDequeueTwice() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.enqueue(asList("A"));
		workSet.enqueue(asList("B"));
		assertThat(workSet.hasMoreElements(), equalTo(true));
		String a = workSet.dequeue();
		assertThat(workSet.hasMoreElements(), equalTo(true));
		assertThat(a, equalTo("A"));
		String b = workSet.dequeue();
		assertThat(workSet.hasMoreElements(), equalTo(false));
		assertThat(b, equalTo("B"));
	}
	
	@Test
	public void testNoReenqueuings() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.enqueue(asList("A", "B"));
		String a = workSet.dequeue();
		assertThat(a, equalTo("A"));
		workSet.enqueue("A");
		String b = workSet.dequeue();
		assertThat(b, equalTo("B"));
		assertThat(workSet.hasMoreElements(), equalTo(false));
	}

	@Test(expected=NoSuchElementException.class)
	public void testDequeueEmpty() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.dequeue();
	}

	@Test
	public void testToStringFresh() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.enqueue(asList("A","B"));
		assertThat(workSet.toString(), equalTo("{A, B}"));
	}

	@Test
	public void testToStringUsed() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.enqueue(asList("A","B","C"));
		workSet.dequeue();
		assertThat(workSet.toString(), equalTo("{B, C | A}"));
	}

}
