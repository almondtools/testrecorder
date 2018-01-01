package net.amygdalum.testrecorder.runtime;

import static java.util.Arrays.asList;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.runtime.GenericComparatorResult.MATCH;
import static net.amygdalum.testrecorder.runtime.GenericComparatorResult.MISMATCH;
import static net.amygdalum.testrecorder.runtime.GenericComparatorResult.NOT_APPLYING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.amygdalum.extensions.assertj.conventions.DefaultEquality;
import net.amygdalum.testrecorder.util.WorkSet;

public class GenericComparisonTest {

	@Test
	public void testGenericComparison() throws Exception {
		Node node1 = new Node("name1");
		Node node2 = new Node("name2");
		GenericComparison comparison = new GenericComparison("root", node1, node2);
		assertThat(comparison.getRoot()).isEqualTo("root");
		assertThat(comparison.getLeft()).isEqualTo(node1);
		assertThat(comparison.getRight()).isEqualTo(node2);
		assertThat(comparison.isMismatch()).isFalse();
	}

	@Test
	public void testFrom() throws Exception {
		Node[] subnodes = new Node[] { new Node("subnode") };
		Node node1 = new Node("node", subnodes);
		Node node2 = new Node("node", subnodes);
		GenericComparison comparison = GenericComparison.from("root", "children", node1, node2);
		assertThat(comparison.getRoot()).isEqualTo("root.children");
		assertThat(comparison.getLeft()).isEqualTo(subnodes);
		assertThat(comparison.getRight()).isEqualTo(subnodes);
		assertThat(comparison.isMismatch()).isFalse();
	}

	@Test
	public void testFromNull() throws Exception {
		Node[] subnodes = new Node[] { new Node("subnode") };
		Node node1 = new Node("node", subnodes);
		Node node2 = new Node("node", subnodes);
		GenericComparison comparison = GenericComparison.from(null, "children", node1, node2);
		assertThat(comparison.getRoot()).isEqualTo("children");
		assertThat(comparison.getLeft()).isEqualTo(subnodes);
		assertThat(comparison.getRight()).isEqualTo(subnodes);
		assertThat(comparison.isMismatch()).isFalse();
	}

	@Test
	public void testFromUnknownField() throws Exception {
		Node[] subnodes = new Node[] { new Node("subnode") };
		Node node1 = new Node("node", subnodes);
		Node node2 = new Node("node", subnodes);
		GenericComparison comparison = GenericComparison.from(null, "child", node1, node2);
		assertThat(comparison.getRoot()).isEqualTo("<error>");
		assertThat(comparison.getLeft()).isNull();
		assertThat(comparison.getRight()).isNull();
		assertThat(comparison.isMismatch()).isTrue();
	}

	@Test
	public void testFromArray() throws Exception {
		Node[] nodes1 = new Node[] { new Node("node1") };
		Node[] nodes2 = new Node[] { new Node("node2") };

		GenericComparison comparison = GenericComparison.from("root", 0, nodes1, nodes2);
		assertThat(comparison.getRoot()).isEqualTo("root[0]");
		assertThat(comparison.getLeft()).isEqualTo(nodes1[0]);
		assertThat(comparison.getRight()).isEqualTo(nodes2[0]);
		assertThat(comparison.isMismatch()).isFalse();
	}

	@Test
	public void testFromArrayNull() throws Exception {
		Node[] nodes1 = new Node[] { new Node("node1") };
		Node[] nodes2 = new Node[] { new Node("node2") };

		GenericComparison comparison = GenericComparison.from(null, 0, nodes1, nodes2);
		assertThat(comparison.getRoot()).isEqualTo("[0]");
		assertThat(comparison.getLeft()).isEqualTo(nodes1[0]);
		assertThat(comparison.getRight()).isEqualTo(nodes2[0]);
		assertThat(comparison.isMismatch()).isFalse();
	}

	@Test
	public void testFromArrayOutOfBounds() throws Exception {
		Node[] nodes1 = new Node[] { new Node("node1") };
		Node[] nodes2 = new Node[] { new Node("node2") };

		GenericComparison comparison = GenericComparison.from(null, 1, nodes1, nodes2);
		assertThat(comparison.getRoot()).isEqualTo("<error>");
		assertThat(comparison.getLeft()).isNull();
		assertThat(comparison.getRight()).isNull();
		assertThat(comparison.isMismatch()).isTrue();
	}

	@Test
	public void testSetMismatchTrue() throws Exception {
		GenericComparison comparison = new GenericComparison("root", new Node("name1"), new Node("name2"));
		comparison.setMismatch(true);

		assertThat(comparison.isMismatch()).isTrue();
	}

	@Test
	public void testSetMismatchFalse() throws Exception {
		GenericComparison comparison = new GenericComparison("root", new Node("name1"), new Node("name2"));
		comparison.setMismatch(false);

		assertThat(comparison.isMismatch()).isFalse();
	}

	@Test
	public void testEqualsStringObjectObject() throws Exception {
		Node node1 = new Node("name1");
		Node node2 = new Node("name2");
		Node node11 = new Node("name1", new Node[] { node1 });
		Node node12 = new Node("name1", new Node[] { node2 });
		Node node21 = new Node("name2", new Node[] { node1 });
		Node node22 = new Node("name2", new Node[] { node2 });

		assertThat(GenericComparison.equals("root", node1, node1)).isTrue();
		assertThat(GenericComparison.equals("root", node1, node2)).isFalse();
		assertThat(GenericComparison.equals("root", node11, node11)).isTrue();
		assertThat(GenericComparison.equals("root", node11, node12)).isFalse();
		assertThat(GenericComparison.equals("root", node11, node21)).isFalse();
		assertThat(GenericComparison.equals("root", node11, node22)).isFalse();
	}

	@Test
	public void testEqualsStringObjectObjectFields() throws Exception {
		Node node1 = new Node("name1");
		Node node2 = new Node("name2");
		Node node11 = new Node("name1", new Node[] { node1 });
		Node node12 = new Node("name1", new Node[] { node2 });
		Node node21 = new Node("name2", new Node[] { node1 });
		Node node22 = new Node("name2", new Node[] { node2 });

		assertThat(GenericComparison.equals("root", node1, node1, asList("name"))).isTrue();
		assertThat(GenericComparison.equals("root", node1, node2, asList("name"))).isFalse();
		assertThat(GenericComparison.equals("root", node11, node11, asList("name"))).isTrue();
		assertThat(GenericComparison.equals("root", node11, node12, asList("name"))).isTrue();
		assertThat(GenericComparison.equals("root", node11, node21, asList("name"))).isFalse();
		assertThat(GenericComparison.equals("root", node11, node22, asList("name"))).isFalse();
	}

	@Test
	public void testEvalWorkSet() throws Exception {
		Node node = new Node("node");

		assertThat(new GenericComparison(null, node, node).eval(new WorkSet<>())).isTrue();
		assertThat(new GenericComparison(null, "name1", "name1").eval(new WorkSet<>())).isTrue();
		assertThat(new GenericComparison(null, node, null).eval(new WorkSet<>())).isFalse();
		assertThat(new GenericComparison(null, null, node).eval(new WorkSet<>())).isFalse();
		assertThat(new GenericComparison(null, node, "name1").eval(new WorkSet<>())).isFalse();
		assertThat(new GenericComparison(null, "name1", node).eval(new WorkSet<>())).isFalse();
		assertThat(new GenericComparison(null, "name1", "name2").eval(new WorkSet<>())).isFalse();
		assertThat(new GenericComparison(null, "name2", "name1").eval(new WorkSet<>())).isFalse();
		assertThat(new GenericComparison(null, new Node[0], new Node[] { node }).eval(new WorkSet<>())).isFalse();
		assertThat(new GenericComparison(null, new Node[] { node }, new Node[0]).eval(new WorkSet<>())).isFalse();
	}

	@Test
	public void testEvalWorkSetNextStep() throws Exception {
		Node node1 = new Node("node1");
		Node node2 = new Node("node2");

		WorkSet<GenericComparison> todo = new WorkSet<>();
		new GenericComparison(null, node1, node2).eval(todo);
		assertThat(todo).containsExactly(
			new GenericComparison("name", node1.name, node2.name),
			new GenericComparison("children", node1.children, node2.children));
	}

	@Test
	public void testEvalWorkSetArraysNextStep() throws Exception {
		Node[] nodes1 = new Node[] { new Node("node1") };
		Node[] nodes2 = new Node[] { new Node("node2") };

		WorkSet<GenericComparison> todo = new WorkSet<>();
		new GenericComparison(null, nodes1, nodes2).eval(todo);
		assertThat(todo).containsExactly(
			new GenericComparison("[0]", nodes1[0], nodes2[0]));
	}

	@Test
	public void testEvalWorkSetCustomComparator() throws Exception {
		Node node = new Node("node");
		GenericComparator c = Mockito.mock(GenericComparator.class);

		assertThat(new GenericComparison(null, node, node).eval(c, new WorkSet<>())).isTrue();
		assertThat(new GenericComparison(null, "name1", "name1").eval(c, new WorkSet<>())).isTrue();

	}

	@Test
	public void testEvalWorkSetCustomComparatorMatch() throws Exception {
		Node node = new Node("node");
		WorkSet<GenericComparison> todo = new WorkSet<>();
		GenericComparator c = Mockito.mock(GenericComparator.class);

		when(c.compare(any(GenericComparison.class), eq(todo))).thenReturn(MATCH);
		assertThat(new GenericComparison(null, node, null).eval(c, todo)).isTrue();
		assertThat(new GenericComparison(null, null, node).eval(c, todo)).isTrue();
	}

	@Test
	public void testEvalWorkSetCustomComparatorMismatch() throws Exception {
		Node node1 = new Node("node");
		Node node2 = new Node("node");
		WorkSet<GenericComparison> todo = new WorkSet<>();
		GenericComparator c = Mockito.mock(GenericComparator.class);

		when(c.compare(any(GenericComparison.class), eq(todo))).thenReturn(MISMATCH);
		assertThat(new GenericComparison(null, node1, node2).eval(c, todo)).isFalse();
		assertThat(new GenericComparison(null, node1, null).eval(c, todo)).isFalse();
		assertThat(new GenericComparison(null, null, node2).eval(c, todo)).isFalse();
	}

	@Test
	public void testEvalWorkSetCustomComparatorNulls() throws Exception {
		Node node = new Node("node");
		WorkSet<GenericComparison> todo = new WorkSet<>();
		GenericComparator c = Mockito.mock(GenericComparator.class);

		when(c.compare(any(GenericComparison.class), eq(todo))).thenReturn(NOT_APPLYING);
		assertThat(new GenericComparison(null, node, null).eval(c, todo)).isFalse();
		assertThat(new GenericComparison(null, null, node).eval(c, todo)).isFalse();
	}

	@Test
	public void testEvalWorkSetCustomComparatorLiterals() throws Exception {
		WorkSet<GenericComparison> todo = new WorkSet<>();
		GenericComparator c = Mockito.mock(GenericComparator.class);

		when(c.compare(any(GenericComparison.class), eq(todo))).thenReturn(NOT_APPLYING);
		assertThat(new GenericComparison(null, new String("s"), new String("s")).eval(c, todo)).isTrue();
		assertThat(new GenericComparison(null, new String("s1"), new String("s2")).eval(c, todo)).isFalse();
	}

	@Test
	public void testEvalWorkSetCustomComparatorArrays() throws Exception {
		WorkSet<GenericComparison> todo = new WorkSet<>();
		GenericComparator c = Mockito.mock(GenericComparator.class);

		when(c.compare(any(GenericComparison.class), eq(todo))).thenReturn(NOT_APPLYING);
		assertThat(new GenericComparison(null, new String[0], new String[0]).eval(c, todo)).isTrue();
		assertThat(new GenericComparison(null, new String[] { "s" }, new String[] { "s" }).eval(c, todo)).isTrue();
		assertThat(new GenericComparison(null, new String[0], new String[] { "s" }).eval(c, todo)).isFalse();
		assertThat(new GenericComparison(null, new String[] { "s" }, new String[0]).eval(c, todo)).isFalse();
	}

	@Test
	public void testEvalWorkSetCustomComparatorNextStep() throws Exception {
		Node node1 = new Node("node1");
		Node node2 = new Node("node2");
		WorkSet<GenericComparison> todo = new WorkSet<>();
		GenericComparator c = Mockito.mock(GenericComparator.class);

		when(c.compare(any(GenericComparison.class), eq(todo))).thenReturn(NOT_APPLYING);

		new GenericComparison(null, node1, node2).eval(c, todo);

		assertThat(todo).containsExactly(
			new GenericComparison("name", node1.name, node2.name),
			new GenericComparison("children", node1.children, node2.children));
	}

	@Test
	public void testEvalWorkSetCustomComparatorNextStepRestrictedToFields() throws Exception {
		Node node1 = new Node("node1");
		Node node2 = new Node("node2");
		WorkSet<GenericComparison> todo = new WorkSet<>();
		GenericComparator c = Mockito.mock(GenericComparator.class);

		when(c.compare(any(GenericComparison.class), eq(todo))).thenReturn(NOT_APPLYING);

		new GenericComparison(null, node1, node2, asList("name")).eval(c, todo);

		assertThat(todo).containsExactly(
			new GenericComparison("name", node1.name, node2.name));
	}

	@Test
	public void testEvalWorkSetCustomComparatorArraysNextStep() throws Exception {
		WorkSet<GenericComparison> todo = new WorkSet<>();
		GenericComparator c = Mockito.mock(GenericComparator.class);

		when(c.compare(any(GenericComparison.class), eq(todo))).thenReturn(NOT_APPLYING);

		new GenericComparison(null, new String[] { "s" }, new String[] { "s" }).eval(c, todo);

		assertThat(todo).containsExactly(new GenericComparison("[0]", "s", "s"));
	}

	@Test
	public void testCompare() throws Exception {
		Node node1 = new Node("node1");
		Node node2 = new Node("node2");
		WorkSet<GenericComparison> todo = new WorkSet<>();
		todo.add(new GenericComparison(null, node1, node2));
		GenericComparison.compare(todo, (comparison, rem) -> comparison.eval(rem) ? GenericComparatorResult.NOT_APPLYING : GenericComparatorResult.MISMATCH);

		assertThat(todo.getDone()).containsExactly(
			new GenericComparison(null, node1, node2, null, null),
			new GenericComparison("name", node1.name, node2.name, null, true),
			new GenericComparison("children", node1.children, node2.children, null, null));

	}

	@Test
	public void testCompareRecursive() throws Exception {
		Node node1 = new Node("node1");
		Node node2 = new Node("node2");
		node1.children = new Node[] { node2 };
		node2.children = new Node[] { node1 };

		WorkSet<GenericComparison> todo = new WorkSet<>();
		todo.add(new GenericComparison(null, node1, node2));
		GenericComparison.compare(todo, (comparison, rem) -> comparison.eval(rem) ? GenericComparatorResult.NOT_APPLYING : GenericComparatorResult.MISMATCH);

		assertThat(todo.getDone()).containsExactly(
			new GenericComparison(null, node1, node2, null, null),
			new GenericComparison("name", node1.name, node2.name, null, true),
			new GenericComparison("children", node1.children, node2.children, null, false),
			new GenericComparison("children[0]", node1.children[0], node2.children[0], null, false),
			new GenericComparison("children[0].name", node1.children[0].name, node2.children[0].name, null, true),
			new GenericComparison("children[0].children", node1.children[0].children, node2.children[0].children, null, false));
	}

	@Test
	public void testEqualsHashcode() throws Exception {
		Node node1 = new Node("name1");
		Node node2 = new Node("name2");
		GenericComparison comparison = new GenericComparison("root", node1, node2);
		assertThat(comparison).satisfies(DefaultEquality.defaultEquality()
			.andEqualTo(new GenericComparison("root", node1, node2))
			.andEqualTo(new GenericComparison("", node1, node2))
			.andNotEqualTo(new GenericComparison("root", node1, node1))
			.andNotEqualTo(new GenericComparison("root", node2, node2))
			.conventions());
	}

	@Test
	public void testToString() throws Exception {
		Node node1 = new Node("name1");
		Node node2 = new Node("name2");
		assertThat(new GenericComparison("root", node1, node2).toString()).containsWildcardPattern("root*false");
	}

	private static class Node {

		private String name;
		private Node[] children;

		public Node(String name) {
			this(name, new Node[0]);
		}

		public Node(String name, Node[] children) {
			this.name = name;
			this.children = children;
		}

	}

}
