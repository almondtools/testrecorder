package net.amygdalum.testrecorder.util;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class WorkQueueTest {

	@Test
	void testSize() throws Exception {
		assertThat(new WorkQueue<>().size()).isEqualTo(0);
		assertThat(new WorkQueue<>().isEmpty()).isTrue();
		assertThat(new WorkQueue<>(asList("a", "b")).size()).isEqualTo(2);
		assertThat(new WorkQueue<>(asList("a", "b")).isEmpty()).isFalse();
	}

	@Test
	void testContains() throws Exception {
		assertThat(new WorkQueue<>(asList("a", "b")).contains("a")).isTrue();
		assertThat(new WorkQueue<>(asList("a", "b")).contains("b")).isTrue();
		assertThat(new WorkQueue<>(asList("a", "b")).contains("c")).isFalse();
		assertThat(new WorkQueue<>(asList("a", "b")).contains(new String("a"))).isFalse();
		assertThat(new WorkQueue<>(asList("a", "b")).contains(new String("b"))).isFalse();

	}

	@Test
	void testIterator() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>(asList("a", "b"));
		Iterator<String> iterator = queue.iterator();

		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo("a");
		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo("b");
		assertThat(iterator.hasNext()).isFalse();
		assertThatThrownBy(() -> iterator.next()).isInstanceOf(NoSuchElementException.class);
	}

	@Test
	void testIteratorRemove() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>(asList("a", "b"));
		Iterator<String> iterator = queue.iterator();
		iterator.next();

		iterator.remove();

		assertThat(queue).containsExactly("b");

		iterator.next();

		iterator.remove();

		assertThat(queue).isEmpty();
	}

	@Test
	void testToArray() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>(asList("a", "b"));

		assertThat(queue.toArray()).containsExactly("a", "b");
		assertThat(queue.toArray(new String[2])).containsExactly("a", "b");
		assertThat(queue.toArray(new String[0])).containsExactly("a", "b");
	}

	@Test
	void testAddObject() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>();
		queue.add("a");

		assertThat(queue).containsExactly("a");
	}

	@Test
	void testRemoveObject() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>();
		queue.add("a");
		queue.remove("a");

		assertThat(queue).isEmpty();
	}

	@Test
	void testRemoveObjectNotInQueue() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>();
		queue.add("a");
		queue.remove("b");

		assertThat(queue).contains("a");
	}

	@Test
	void testContainsAll() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>();
		queue.add("a");
		queue.add("b");

		assertThat(queue.containsAll(asList("a", "b"))).isTrue();
		assertThat(queue.containsAll(asList("b", "a"))).isTrue();
		assertThat(queue.containsAll(asList("a"))).isTrue();
		assertThat(queue.containsAll(asList("b"))).isTrue();
		assertThat(queue.containsAll(asList("c"))).isFalse();
	}

	@Test
	void testAddAll() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>();
		queue.addAll(asList("a", "b"));

		assertThat(queue.contains("a")).isTrue();
		assertThat(queue.contains("b")).isTrue();
		assertThat(queue.contains("c")).isFalse();
	}

	@Test
	void testRemoveAll() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>();
		queue.addAll(asList("a", "b", "c"));
		queue.removeAll(asList("a", "b", "d"));

		assertThat(queue.contains("a")).isFalse();
		assertThat(queue.contains("b")).isFalse();
		assertThat(queue.contains("d")).isFalse();
		assertThat(queue.contains("c")).isTrue();
	}

	@Test
	void testRetainAll() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>();
		queue.addAll(asList("a", "b", "c"));
		queue.retainAll(asList("a", "b", "d"));

		assertThat(queue.contains("a")).isTrue();
		assertThat(queue.contains("b")).isTrue();
		assertThat(queue.contains("d")).isFalse();
		assertThat(queue.contains("c")).isFalse();
	}

	@Test
	void testClear() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>(asList("a", "b"));
		queue.clear();

		assertThat(queue).isEmpty();
	}

	@Test
	void testOffer() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>(asList("a", "b"));
		queue.offer("c");

		assertThat(queue).containsExactly("a", "b", "c");
	}

	@Test
	void testRemove() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>(asList("a", "b"));
		String s = queue.remove();

		assertThat(s).isEqualTo("a");
		assertThat(queue).containsExactly("b");
	}

	@Test
	void testRemoveOnEmpty() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>();

		assertThatThrownBy(() -> queue.remove()).isInstanceOf(NoSuchElementException.class);
	}

	@Test
	void testPoll() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>(asList("a", "b"));
		String s = queue.poll();

		assertThat(s).isEqualTo("a");
		assertThat(queue).containsExactly("b");
	}

	@Test
	void testPollOnEmpty() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>();
		String s = queue.poll();

		assertThat(s).isNull();
	}

	@Test
	void testElement() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>(asList("a", "b"));
		String s = queue.element();

		assertThat(s).isEqualTo("a");
		assertThat(queue).containsExactly("a", "b");
	}

	@Test
	void testElementOnEmpty() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>();

		assertThatThrownBy(() -> queue.element()).isInstanceOf(NoSuchElementException.class);
	}

	@Test
	void testPeek() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>(asList("a", "b"));
		String s = queue.peek();

		assertThat(s).isEqualTo("a");
		assertThat(queue).containsExactly("a", "b");
	}

	@Test
	void testPeekOnEmpty() throws Exception {
		WorkQueue<String> queue = new WorkQueue<>();
		String s = queue.peek();

		assertThat(s).isNull();
	}

}
