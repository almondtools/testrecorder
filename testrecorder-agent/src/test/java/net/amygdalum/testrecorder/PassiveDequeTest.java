package net.amygdalum.testrecorder;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PassiveDequeTest {

    private PassiveDeque<String> deque;

    @BeforeEach
    public void before() throws Exception {
        deque = new PassiveDeque<>("42");
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertThat(deque.isEmpty()).isFalse();
    }

    @Test
    public void testToArray() throws Exception {
        assertThat(deque.toArray()).containsExactly("42");
        assertThat(deque.toArray(new String[0])).containsExactly("42");
        assertThat(deque.toArray(null)).containsExactly("42");
    }

    @Test
    public void testContainsAll() throws Exception {
        assertThat(deque.containsAll(emptyList())).isFalse();
        assertThat(deque.containsAll(asList("41"))).isFalse();
        assertThat(deque.containsAll(asList("42"))).isFalse();
    }

    @Test
    public void testAddAll() throws Exception {
        assertThat(deque.addAll(emptyList())).isFalse();
        assertThat(deque.addAll(asList("41"))).isFalse();
    }

    @Test
    public void testRemoveAll() throws Exception {
        assertThat(deque.removeAll(emptyList())).isFalse();
        assertThat(deque.removeAll(asList("41"))).isFalse();
        assertThat(deque.removeAll(asList("42"))).isFalse();
    }

    @Test
    public void testRetainAll() throws Exception {
        assertThat(deque.retainAll(emptyList())).isFalse();
        assertThat(deque.retainAll(asList("41"))).isFalse();
        assertThat(deque.retainAll(asList("42"))).isFalse();
    }

    @Test
    public void testClear() throws Exception {
        deque.clear();
        
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testAddFirst() throws Exception {
        deque.addFirst("41");

        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testAddLast() throws Exception {
        deque.addLast("41");

        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testOfferFirst() throws Exception {
        boolean state = deque.offerFirst("41");

        assertThat(state).isFalse();
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testOfferLast() throws Exception {
        boolean state = deque.offerLast("41");

        assertThat(state).isFalse();
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testRemoveFirst() throws Exception {
        String data = deque.removeFirst();

        assertThat(data).isEqualTo("42");
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testRemoveLast() throws Exception {
        String data = deque.removeLast();

        assertThat(data).isEqualTo("42");
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testPollFirst() throws Exception {
        String data = deque.pollFirst();

        assertThat(data).isEqualTo("42");
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testPollLast() throws Exception {
        String data = deque.pollLast();

        assertThat(data).isEqualTo("42");
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testPeekFirst() throws Exception {
        String data = deque.peekFirst();

        assertThat(data).isEqualTo("42");
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testPeekLast() throws Exception {
        String data = deque.peekLast();

        assertThat(data).isEqualTo("42");
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testRemoveFirstOccurrence() throws Exception {
        boolean state = deque.removeFirstOccurrence("42");

        assertThat(state).isFalse();
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testRemoveLastOccurrence() throws Exception {
        boolean state = deque.removeLastOccurrence("42");

        assertThat(state).isFalse();
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testAdd() throws Exception {
        boolean state = deque.add("41");

        assertThat(state).isFalse();
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testOffer() throws Exception {
        boolean state = deque.offer("41");

        assertThat(state).isFalse();
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testRemove() throws Exception {
        deque.remove();

        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testPoll() throws Exception {
        String data = deque.poll();

        assertThat(data).isEqualTo("42");
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testElement() throws Exception {
        String data = deque.element();

        assertThat(data).isEqualTo("42");
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testPeek() throws Exception {
        String data = deque.peek();

        assertThat(data).isEqualTo("42");
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testPush() throws Exception {
        deque.push("41");

        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testPop() throws Exception {
        String data = deque.pop();

        assertThat(data).isEqualTo("42");
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testRemoveObject() throws Exception {
        boolean state = deque.remove("42");

        assertThat(state).isFalse();
        assertThat(deque.getLast()).isEqualTo("42");
        assertThat(deque.getFirst()).isEqualTo("42");
    }

    @Test
    public void testContains() throws Exception {
        assertThat(deque.contains("41")).isFalse();
        assertThat(deque.contains("42")).isFalse();
    }

    @Test
    public void testSize() throws Exception {
        assertThat(deque.size()).isEqualTo(1);
    }

    @Test
    public void testIterator() throws Exception {
        assertThat(deque.iterator().next()).isEqualTo("42");
        assertThat(deque.descendingIterator().next()).isEqualTo("42");
    }

}
