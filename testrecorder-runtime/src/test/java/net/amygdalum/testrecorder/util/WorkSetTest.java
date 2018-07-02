package net.amygdalum.testrecorder.util;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WorkSetTest {

	private WorkSet<String> workSet;

	@BeforeEach
	public void before() throws Exception {
		workSet = new WorkSet<>();
	}

	@Test
	void testConstructor() throws Exception {
		assertThat(workSet.hasMoreElements()).isEqualTo(false);
	}

	@Test
	void testConstructorWithBase() throws Exception {
		workSet = new WorkSet<>(new LinkedList<>(asList("A")));
		assertThat(workSet.hasMoreElements()).isEqualTo(true);
	}

	@Test
	void testEnqueueDequeueSingle() throws Exception {
		workSet.add("A");
		assertThat(workSet.hasMoreElements()).isEqualTo(true);
		String element = workSet.remove();
		assertThat(workSet.hasMoreElements()).isEqualTo(false);
		assertThat(element).isEqualTo("A");
	}

	@Test
	void testEnqueueDequeueOneElementList() throws Exception {
		workSet.addAll(asList("A"));
		assertThat(workSet.hasMoreElements()).isEqualTo(true);
		String element = workSet.remove();
		assertThat(workSet.hasMoreElements()).isEqualTo(false);
		assertThat(element).isEqualTo("A");
	}

	@Test
	void testEnqueueDequeueTwice() throws Exception {
		workSet.addAll(asList("A", "B"));
		assertThat(workSet.hasMoreElements()).isEqualTo(true);
		String a = workSet.remove();
		assertThat(workSet.hasMoreElements()).isEqualTo(true);
		assertThat(a).isEqualTo("A");
		String b = workSet.remove();
		assertThat(workSet.hasMoreElements()).isEqualTo(false);
		assertThat(b).isEqualTo("B");
	}

	@Test
	void testEnqueueTwiceDequeueTwice() throws Exception {
		workSet.addAll(asList("A"));
		workSet.addAll(asList("B"));
		assertThat(workSet.hasMoreElements()).isEqualTo(true);
		String a = workSet.remove();
		assertThat(workSet.hasMoreElements()).isEqualTo(true);
		assertThat(a).isEqualTo("A");
		String b = workSet.remove();
		assertThat(workSet.hasMoreElements()).isEqualTo(false);
		assertThat(b).isEqualTo("B");
	}

	@Test
	void testNoReenqueuings() throws Exception {
		workSet.addAll(asList("A", "B"));
		String a = workSet.remove();
		assertThat(a).isEqualTo("A");
		workSet.add("A");
		String b = workSet.remove();
		assertThat(b).isEqualTo("B");
		assertThat(workSet.hasMoreElements()).isEqualTo(false);
	}

	@Test
	void testDequeueEmpty() throws Exception {
		assertThatThrownBy(() -> workSet.remove()).isInstanceOf(NoSuchElementException.class);
	}

	@Test
	void testToStringFresh() throws Exception {
		workSet.addAll(asList("A", "B"));
		assertThat(workSet.toString()).isEqualTo("{A, B}");
	}

	@Test
	void testToStringUsed() throws Exception {
		workSet.addAll(asList("A", "B", "C"));
		workSet.remove();
		assertThat(workSet.toString()).isEqualTo("{B, C | A}");
	}

	@Test
	void testWorkset() throws Exception {
		assertThat(workSet.isEmpty()).isTrue();
	}

	@Test
	void testAdd() throws Exception {
		boolean changed = workSet.add("A");

		assertThat(changed).isTrue();
		assertThat(workSet.isEmpty()).isFalse();
		assertThat(workSet.getDone()).isEmpty();
		assertThat(workSet.peek()).isEqualTo("A");
		assertThat(workSet.size()).isEqualTo(1);
	}

	@Test
	void testAddExisting() throws Exception {
		workSet.add("A");

		boolean changed = workSet.add("A");

		assertThat(changed).isFalse();
		assertThat(workSet.isEmpty()).isFalse();
		assertThat(workSet.getDone()).isEmpty();
		assertThat(workSet.peek()).isEqualTo("A");
		assertThat(workSet.size()).isEqualTo(1);
	}

	@Test
	void testAddDone() throws Exception {
		workSet.add("A");
		workSet.remove();

		boolean changed = workSet.add("A");

		assertThat(changed).isFalse();
		assertThat(workSet.isEmpty()).isTrue();
		assertThat(workSet.getDone()).containsExactly("A");
		assertThat(workSet.peek()).isNull();
		assertThat(workSet.size()).isEqualTo(0);
	}

	@Test
	void testOffer() throws Exception {
		boolean changed = workSet.offer("A");

		assertThat(changed).isTrue();
		assertThat(workSet.isEmpty()).isFalse();
		assertThat(workSet.getDone()).isEmpty();
		assertThat(workSet.peek()).isEqualTo("A");
		assertThat(workSet.size()).isEqualTo(1);
	}

	@Test
	void testOfferExisting() throws Exception {
		workSet.add("A");

		boolean changed = workSet.offer("A");

		assertThat(changed).isFalse();
		assertThat(workSet.isEmpty()).isFalse();
		assertThat(workSet.getDone()).isEmpty();
		assertThat(workSet.peek()).isEqualTo("A");
		assertThat(workSet.size()).isEqualTo(1);
	}

	@Test
	void testOfferDone() throws Exception {
		workSet.add("A");
		workSet.remove();

		boolean changed = workSet.offer("A");

		assertThat(changed).isFalse();
		assertThat(workSet.isEmpty()).isTrue();
		assertThat(workSet.getDone()).containsExactly("A");
		assertThat(workSet.peek()).isNull();
		assertThat(workSet.size()).isEqualTo(0);
	}

	@Test
	void testAddAll() throws Exception {
		boolean changed = workSet.addAll(asList("A", "B"));

		assertThat(changed).isTrue();
		assertThat(workSet.isEmpty()).isFalse();
		assertThat(workSet.getDone()).isEmpty();
		assertThat(workSet.peek()).isEqualTo("A");
		assertThat(workSet.toArray(new String[0])).containsExactly("A", "B");
		assertThat(workSet.size()).isEqualTo(2);
	}

	@Test
	void testAddAllExisting() throws Exception {
		workSet.addAll(asList("A", "B"));

		boolean changed = workSet.addAll(asList("A", "B"));

		assertThat(changed).isFalse();
		assertThat(workSet.isEmpty()).isFalse();
		assertThat(workSet.getDone()).isEmpty();
		assertThat(workSet.peek()).isEqualTo("A");
		assertThat(workSet.toArray(new String[0])).containsExactly("A", "B");
		assertThat(workSet.size()).isEqualTo(2);
	}

	@Test
	void testAddAllSomeExisting() throws Exception {
		workSet.addAll(asList("A", "B"));

		boolean changed = workSet.addAll(asList("B", "C"));

		assertThat(changed).isTrue();
		assertThat(workSet.isEmpty()).isFalse();
		assertThat(workSet.getDone()).isEmpty();
		assertThat(workSet.peek()).isEqualTo("A");
		assertThat(workSet.toArray(new String[0])).containsExactly("A", "B", "C");
		assertThat(workSet.size()).isEqualTo(3);
	}

	@Test
	void testAddAllSomeDone() throws Exception {
		workSet.addAll(asList("A", "B"));
		workSet.remove();

		boolean changed = workSet.addAll(asList("A", "C"));

		assertThat(changed).isTrue();
		assertThat(workSet.isEmpty()).isFalse();
		assertThat(workSet.getDone()).containsExactly("A");
		assertThat(workSet.peek()).isEqualTo("B");
		assertThat(workSet.toArray(new String[0])).containsExactly("B", "C");
		assertThat(workSet.size()).isEqualTo(2);
	}

	@Test
	void testRemoveOnEmpty() throws Exception {
		assertThatThrownBy(() -> workSet.remove()).isInstanceOf(NoSuchElementException.class);
	}

	@Test
	void testRemoveEmptying() throws Exception {
		workSet.add("A");

		String r = workSet.remove();

		assertThat(r).isEqualTo("A");
		assertThat(workSet.isEmpty()).isTrue();
		assertThat(workSet.getDone()).containsExactly("A");
		assertThat(workSet.peek()).isNull();
	}

	@Test
	void testRemoveNonEmptying() throws Exception {
		workSet.addAll(asList("A", "B"));

		String r = workSet.remove();

		assertThat(r).isEqualTo("A");
		assertThat(workSet.isEmpty()).isFalse();
		assertThat(workSet.getDone()).containsExactly("A");
		assertThat(workSet.peek()).isEqualTo("B");
	}

	@Test
	void testPollOnEmpty() throws Exception {
		String r = workSet.poll();

		assertThat(r).isNull();
		assertThat(workSet.isEmpty()).isTrue();
	}

	@Test
	void testPollEmptying() throws Exception {
		workSet.add("A");

		String r = workSet.poll();

		assertThat(r).isEqualTo("A");
		assertThat(workSet.isEmpty()).isTrue();
		assertThat(workSet.getDone()).containsExactly("A");
		assertThat(workSet.peek()).isNull();
	}

	@Test
	void testPollNonEmptying() throws Exception {
		workSet.addAll(asList("A", "B"));

		String r = workSet.poll();

		assertThat(r).isEqualTo("A");
		assertThat(workSet.isEmpty()).isFalse();
		assertThat(workSet.getDone()).containsExactly("A");
		assertThat(workSet.peek()).isEqualTo("B");
	}

	@Test
	void testContains() throws Exception {
		workSet.addAll(asList("A", "B"));

		assertThat(workSet.contains("A")).isTrue();
		assertThat(workSet.contains("B")).isTrue();
		assertThat(workSet.contains("C")).isFalse();
	}

	@Test
	void testContainsDone() throws Exception {
		workSet.addAll(asList("A", "B"));

		workSet.remove();

		assertThat(workSet.contains("A")).isTrue();
		assertThat(workSet.contains("B")).isTrue();
		assertThat(workSet.contains("C")).isFalse();
	}

	@Test
	void testIterator() throws Exception {
		workSet.addAll(asList("A", "B"));

		Iterator<String> iterator = workSet.iterator();

		assertThat(iterator.next()).isEqualTo("A");
		assertThat(iterator.next()).isEqualTo("B");
	}

	@Test
	void testToArray() throws Exception {
		workSet.addAll(asList("A", "B"));

		assertThat(workSet.toArray()).containsExactly((Object) "A", "B");
		assertThat(workSet.toArray(new String[0])).containsExactly("A", "B");
	}

	@Test
	void testRemoveObject() throws Exception {
		workSet.addAll(asList("A", "B"));

		workSet.remove("A");

		assertThat(workSet.isEmpty()).isFalse();
		assertThat(workSet.getDone()).isEmpty();
		assertThat(workSet.peek()).isEqualTo("B");
	}

	@Test
	void testRemoveObjectOnDone() throws Exception {
		workSet.addAll(asList("0", "A", "B"));
		workSet.remove();
		workSet.remove();

		workSet.remove("A");

		assertThat(workSet.isEmpty()).isFalse();
		assertThat(workSet.getDone()).containsExactly("0");
		assertThat(workSet.peek()).isEqualTo("B");
	}

	@Test
	void testRemoveAll() throws Exception {
		workSet.addAll(asList("A", "B"));

		workSet.removeAll(asList("A", "B"));

		assertThat(workSet.isEmpty()).isTrue();
		assertThat(workSet.getDone()).isEmpty();
		assertThat(workSet.peek()).isNull();
	}

	@Test
	void testRemoveAllOnDone() throws Exception {
		workSet.addAll(asList("0", "A", "B"));
		workSet.remove();
		workSet.remove();

		workSet.removeAll(asList("A", "B"));

		assertThat(workSet.isEmpty()).isTrue();
		assertThat(workSet.getDone()).containsExactly("0");
		assertThat(workSet.peek()).isNull();
	}

	@Test
	void testRetainAll() throws Exception {
		workSet.addAll(asList("A", "B"));

		workSet.retainAll(asList("B"));

		assertThat(workSet.isEmpty()).isFalse();
		assertThat(workSet.getDone()).isEmpty();
		assertThat(workSet.peek()).isEqualTo("B");
	}

	@Test
	void testContainsAll() throws Exception {
		workSet.addAll(asList("A", "B"));

		assertThat(workSet.containsAll(asList("B"))).isTrue();
		assertThat(workSet.containsAll(asList("A"))).isTrue();
		assertThat(workSet.containsAll(asList("A", "B"))).isTrue();
		assertThat(workSet.containsAll(asList("B", "A"))).isTrue();
		assertThat(workSet.containsAll(asList("C"))).isFalse();
		assertThat(workSet.containsAll(asList("A", "B", "C"))).isFalse();
	}

	@Test
	void testContainsAllDone() throws Exception {
		workSet.addAll(asList("A", "B"));

		workSet.remove();

		assertThat(workSet.containsAll(asList("B"))).isTrue();
		assertThat(workSet.containsAll(asList("A"))).isTrue();
		assertThat(workSet.containsAll(asList("A", "B"))).isTrue();
		assertThat(workSet.containsAll(asList("B", "A"))).isTrue();
		assertThat(workSet.containsAll(asList("C"))).isFalse();
		assertThat(workSet.containsAll(asList("A", "B", "C"))).isFalse();
	}

	@Test
	void testClear() throws Exception {
		workSet.addAll(asList("A", "B"));

		workSet.clear();

		assertThat(workSet.isEmpty()).isTrue();
		assertThat(workSet.getDone()).isEmpty();
		assertThat(workSet.peek()).isNull();
	}

	@Test
	void testClearOnDone() throws Exception {
		workSet.addAll(asList("A", "B"));
		workSet.remove();

		workSet.clear();

		assertThat(workSet.isEmpty()).isTrue();
		assertThat(workSet.getDone()).isEmpty();
		assertThat(workSet.peek()).isNull();
	}

	@Test
	void testElementOnEmpty() throws Exception {

		assertThatThrownBy(() -> workSet.element()).isInstanceOf(NoSuchElementException.class);
	}

	@Test
	void testElement() throws Exception {
		workSet.addAll(asList("A", "B"));

		assertThat(workSet.element()).isEqualTo("A");
	}

	@Test
	void testPeekOnEmpty() throws Exception {

		assertThat(workSet.peek()).isNull();
	}

	@Test
	void testPeek() throws Exception {
		workSet.addAll(asList("A", "B"));

		assertThat(workSet.peek()).isEqualTo("A");
	}

}
