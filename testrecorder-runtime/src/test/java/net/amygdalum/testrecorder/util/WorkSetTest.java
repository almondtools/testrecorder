package net.amygdalum.testrecorder.util;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;



public class WorkSetTest {

	@Test
	public void testConstructor() throws Exception {
		assertThat(new WorkSet<>().hasMoreElements()).isEqualTo(false);
		assertThat(new WorkSet<>(new LinkedList<>(asList("A"))).hasMoreElements()).isEqualTo(true);
	}

	@Test
	public void testEnqueueDequeueSingle() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.add("A");
		assertThat(workSet.hasMoreElements()).isEqualTo(true);
		String element = workSet.remove();
		assertThat(workSet.hasMoreElements()).isEqualTo(false);
		assertThat(element).isEqualTo("A");
	}

	@Test
	public void testEnqueueDequeueOneElementList() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.addAll(asList("A"));
		assertThat(workSet.hasMoreElements()).isEqualTo(true);
		String element = workSet.remove();
		assertThat(workSet.hasMoreElements()).isEqualTo(false);
		assertThat(element).isEqualTo("A");
	}

	@Test
	public void testEnqueueDequeueTwice() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
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
	public void testEnqueueTwiceDequeueTwice() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
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
	public void testNoReenqueuings() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.addAll(asList("A", "B"));
		String a = workSet.remove();
		assertThat(a).isEqualTo("A");
		workSet.add("A");
		String b = workSet.remove();
		assertThat(b).isEqualTo("B");
		assertThat(workSet.hasMoreElements()).isEqualTo(false);
	}

	@Test
	public void testDequeueEmpty() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		assertThatThrownBy(() -> workSet.remove()).isInstanceOf(NoSuchElementException.class);
	}

	@Test
	public void testToStringFresh() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.addAll(asList("A", "B"));
		assertThat(workSet.toString()).isEqualTo("{A, B}");
	}

	@Test
	public void testToStringUsed() throws Exception {
		WorkSet<String> workSet = new WorkSet<>();
		workSet.addAll(asList("A", "B", "C"));
		workSet.remove();
		assertThat(workSet.toString()).isEqualTo("{B, C | A}");
	}

	@Test
	public void testWorkset() throws Exception {
		assertThat(new WorkSet<>().isEmpty()).isTrue();
	}

	@Test
	public void testAdd() throws Exception {
		WorkSet<String> ws = new WorkSet<>();

		boolean changed = ws.add("A");

		assertThat(changed).isTrue();
		assertThat(ws.isEmpty()).isFalse();
		assertThat(ws.getDone()).isEmpty();
		assertThat(ws.peek()).isEqualTo("A");
		assertThat(ws.size()).isEqualTo(1);
	}

	@Test
	public void testAddExisting() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.add("A");

		boolean changed = ws.add("A");

		assertThat(changed).isFalse();
		assertThat(ws.isEmpty()).isFalse();
		assertThat(ws.getDone()).isEmpty();
		assertThat(ws.peek()).isEqualTo("A");
		assertThat(ws.size()).isEqualTo(1);
	}

	@Test
	public void testAddDone() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.add("A");
		ws.remove();

		boolean changed = ws.add("A");

		assertThat(changed).isFalse();
		assertThat(ws.isEmpty()).isTrue();
		assertThat(ws.getDone()).containsExactly("A");
		assertThat(ws.peek()).isNull();
		assertThat(ws.size()).isEqualTo(0);
	}

	@Test
	public void testOffer() throws Exception {
		WorkSet<String> ws = new WorkSet<>();

		boolean changed = ws.offer("A");

		assertThat(changed).isTrue();
		assertThat(ws.isEmpty()).isFalse();
		assertThat(ws.getDone()).isEmpty();
		assertThat(ws.peek()).isEqualTo("A");
		assertThat(ws.size()).isEqualTo(1);
	}

	@Test
	public void testOfferExisting() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.add("A");

		boolean changed = ws.offer("A");

		assertThat(changed).isFalse();
		assertThat(ws.isEmpty()).isFalse();
		assertThat(ws.getDone()).isEmpty();
		assertThat(ws.peek()).isEqualTo("A");
		assertThat(ws.size()).isEqualTo(1);
	}

	@Test
	public void testOfferDone() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.add("A");
		ws.remove();

		boolean changed = ws.offer("A");

		assertThat(changed).isFalse();
		assertThat(ws.isEmpty()).isTrue();
		assertThat(ws.getDone()).containsExactly("A");
		assertThat(ws.peek()).isNull();
		assertThat(ws.size()).isEqualTo(0);
	}

	@Test
	public void testAddAll() throws Exception {
		WorkSet<String> ws = new WorkSet<>();

		boolean changed = ws.addAll(asList("A", "B"));

		assertThat(changed).isTrue();
		assertThat(ws.isEmpty()).isFalse();
		assertThat(ws.getDone()).isEmpty();
		assertThat(ws.peek()).isEqualTo("A");
		assertThat(ws.toArray(new String[0])).containsExactly("A", "B");
		assertThat(ws.size()).isEqualTo(2);
	}

	@Test
	public void testAddAllExisting() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		boolean changed = ws.addAll(asList("A", "B"));

		assertThat(changed).isFalse();
		assertThat(ws.isEmpty()).isFalse();
		assertThat(ws.getDone()).isEmpty();
		assertThat(ws.peek()).isEqualTo("A");
		assertThat(ws.toArray(new String[0])).containsExactly("A", "B");
		assertThat(ws.size()).isEqualTo(2);
	}

	@Test
	public void testAddAllSomeExisting() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		boolean changed = ws.addAll(asList("B", "C"));

		assertThat(changed).isTrue();
		assertThat(ws.isEmpty()).isFalse();
		assertThat(ws.getDone()).isEmpty();
		assertThat(ws.peek()).isEqualTo("A");
		assertThat(ws.toArray(new String[0])).containsExactly("A", "B", "C");
		assertThat(ws.size()).isEqualTo(3);
	}

	@Test
	public void testAddAllSomeDone() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));
		ws.remove();

		boolean changed = ws.addAll(asList("A", "C"));

		assertThat(changed).isTrue();
		assertThat(ws.isEmpty()).isFalse();
		assertThat(ws.getDone()).containsExactly("A");
		assertThat(ws.peek()).isEqualTo("B");
		assertThat(ws.toArray(new String[0])).containsExactly("B", "C");
		assertThat(ws.size()).isEqualTo(2);
	}

	@Test
	public void testRemoveOnEmpty() throws Exception {
		WorkSet<String> ws = new WorkSet<>();

		assertThatThrownBy(() -> ws.remove()).isInstanceOf(NoSuchElementException.class);
	}

	@Test
	public void testRemoveEmptying() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.add("A");

		String r = ws.remove();

		assertThat(r).isEqualTo("A");
		assertThat(ws.isEmpty()).isTrue();
		assertThat(ws.getDone()).containsExactly("A");
		assertThat(ws.peek()).isNull();
	}

	@Test
	public void testRemoveNonEmptying() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		String r = ws.remove();

		assertThat(r).isEqualTo("A");
		assertThat(ws.isEmpty()).isFalse();
		assertThat(ws.getDone()).containsExactly("A");
		assertThat(ws.peek()).isEqualTo("B");
	}

	@Test
	public void testPollOnEmpty() throws Exception {
		WorkSet<String> ws = new WorkSet<>();

		String r = ws.poll();

		assertThat(r).isNull();
		assertThat(ws.isEmpty()).isTrue();
	}

	@Test
	public void testPollEmptying() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.add("A");

		String r = ws.poll();

		assertThat(r).isEqualTo("A");
		assertThat(ws.isEmpty()).isTrue();
		assertThat(ws.getDone()).containsExactly("A");
		assertThat(ws.peek()).isNull();
	}

	@Test
	public void testPollNonEmptying() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		String r = ws.poll();

		assertThat(r).isEqualTo("A");
		assertThat(ws.isEmpty()).isFalse();
		assertThat(ws.getDone()).containsExactly("A");
		assertThat(ws.peek()).isEqualTo("B");
	}

	@Test
	public void testContains() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		assertThat(ws.contains("A")).isTrue();
		assertThat(ws.contains("B")).isTrue();
		assertThat(ws.contains("C")).isFalse();
	}

	@Test
	public void testContainsDone() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		ws.remove();

		assertThat(ws.contains("A")).isTrue();
		assertThat(ws.contains("B")).isTrue();
		assertThat(ws.contains("C")).isFalse();
	}

	@Test
	public void testIterator() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		Iterator<String> iterator = ws.iterator();

		assertThat(iterator.next()).isEqualTo("A");
		assertThat(iterator.next()).isEqualTo("B");
	}

	@Test
	public void testToArray() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		assertThat(ws.toArray()).containsExactly((Object) "A", "B");
		assertThat(ws.toArray(new String[0])).containsExactly("A", "B");
	}

	@Test
	public void testRemoveObject() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		ws.remove("A");

		assertThat(ws.isEmpty()).isFalse();
		assertThat(ws.getDone()).isEmpty();
		assertThat(ws.peek()).isEqualTo("B");
	}

	@Test
	public void testRemoveObjectOnDone() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("0", "A", "B"));
		ws.remove();
		ws.remove();

		ws.remove("A");

		assertThat(ws.isEmpty()).isFalse();
		assertThat(ws.getDone()).containsExactly("0");
		assertThat(ws.peek()).isEqualTo("B");
	}

	@Test
	public void testRemoveAll() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		ws.removeAll(asList("A", "B"));

		assertThat(ws.isEmpty()).isTrue();
		assertThat(ws.getDone()).isEmpty();
		assertThat(ws.peek()).isNull();
	}

	@Test
	public void testRemoveAllOnDone() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("0", "A", "B"));
		ws.remove();
		ws.remove();

		ws.removeAll(asList("A", "B"));

		assertThat(ws.isEmpty()).isTrue();
		assertThat(ws.getDone()).containsExactly("0");
		assertThat(ws.peek()).isNull();
	}

	@Test
	public void testRetainAll() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		ws.retainAll(asList("B"));

		assertThat(ws.isEmpty()).isFalse();
		assertThat(ws.getDone()).isEmpty();
		assertThat(ws.peek()).isEqualTo("B");
	}

	@Test
	public void testContainsAll() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		assertThat(ws.containsAll(asList("B"))).isTrue();
		assertThat(ws.containsAll(asList("A"))).isTrue();
		assertThat(ws.containsAll(asList("A", "B"))).isTrue();
		assertThat(ws.containsAll(asList("B", "A"))).isTrue();
		assertThat(ws.containsAll(asList("C"))).isFalse();
		assertThat(ws.containsAll(asList("A", "B", "C"))).isFalse();
	}

	@Test
	public void testContainsAllDone() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		ws.remove();

		assertThat(ws.containsAll(asList("B"))).isTrue();
		assertThat(ws.containsAll(asList("A"))).isTrue();
		assertThat(ws.containsAll(asList("A", "B"))).isTrue();
		assertThat(ws.containsAll(asList("B", "A"))).isTrue();
		assertThat(ws.containsAll(asList("C"))).isFalse();
		assertThat(ws.containsAll(asList("A", "B", "C"))).isFalse();
	}

	@Test
	public void testClear() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		ws.clear();

		assertThat(ws.isEmpty()).isTrue();
		assertThat(ws.getDone()).isEmpty();
		assertThat(ws.peek()).isNull();
	}

	@Test
	public void testClearOnDone() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));
		ws.remove();

		ws.clear();

		assertThat(ws.isEmpty()).isTrue();
		assertThat(ws.getDone()).isEmpty();
		assertThat(ws.peek()).isNull();
	}

	@Test
	public void testElementOnEmpty() throws Exception {
		WorkSet<String> ws = new WorkSet<>();

		assertThatThrownBy(() -> ws.element()).isInstanceOf(NoSuchElementException.class);
	}

	@Test
	public void testElement() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		assertThat(ws.element()).isEqualTo("A");
	}

	@Test
	public void testPeekOnEmpty() throws Exception {
		WorkSet<String> ws = new WorkSet<>();

		assertThat(ws.peek()).isNull();
	}

	@Test
	public void testPeek() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		assertThat(ws.peek()).isEqualTo("A");
	}

}
