package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.runtime.GenericComparatorResult.MATCH;
import static net.amygdalum.testrecorder.runtime.GenericComparatorResult.MISMATCH;
import static net.amygdalum.testrecorder.runtime.GenericComparatorResult.NOT_APPLYING;
import static net.amygdalum.testrecorder.runtime.SelectedFieldsComparisonStrategy.comparingFields;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.amygdalum.extensions.assertj.conventions.DefaultEquality;
import net.amygdalum.testrecorder.util.WorkSet;
import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class GenericComparisonTest {

	@Test
	void testGenericComparison() throws Exception {
		Node node1 = new Node("name1");
		Node node2 = new Node("name2");
		GenericComparison comparison = new GenericComparison("root", node1, node2);
		assertThat(comparison.getRoot()).isEqualTo("root");
		assertThat(comparison.getLeft()).isEqualTo(node1);
		assertThat(comparison.getRight()).isEqualTo(node2);
		assertThat(comparison.isMismatch()).isFalse();
	}

	@Nested
	class testFrom {
		@Test
		void onCommon() throws Exception {
			Node[] subnodes = new Node[] {new Node("subnode")};
			Node node1 = new Node("node", subnodes);
			Node node2 = new Node("node", subnodes);

			GenericComparison comparison = GenericComparison.from("root", "children", node1, node2);

			assertThat(comparison.getRoot()).isEqualTo("root.children");
			assertThat(comparison.getLeft()).isEqualTo(subnodes);
			assertThat(comparison.getRight()).isEqualTo(subnodes);
			assertThat(comparison.isMismatch()).isFalse();
		}

		@Test
		void onNull() throws Exception {
			Node[] subnodes = new Node[] {new Node("subnode")};
			Node node1 = new Node("node", subnodes);
			Node node2 = new Node("node", subnodes);

			GenericComparison comparison = GenericComparison.from(null, "children", node1, node2);

			assertThat(comparison.getRoot()).isEqualTo("children");
			assertThat(comparison.getLeft()).isEqualTo(subnodes);
			assertThat(comparison.getRight()).isEqualTo(subnodes);
			assertThat(comparison.isMismatch()).isFalse();
		}

		@Test
		void onUnknownField() throws Exception {
			Node[] subnodes = new Node[] {new Node("subnode")};
			Node node1 = new Node("node", subnodes);
			Node node2 = new Node("node", subnodes);

			GenericComparison comparison = GenericComparison.from(null, "child", node1, node2);

			assertThat(comparison.getRoot()).isEqualTo("<error>");
			assertThat(comparison.getLeft()).isNull();
			assertThat(comparison.getRight()).isNull();
			assertThat(comparison.isMismatch()).isTrue();
		}

		@Test
		void onArray() throws Exception {
			Node[] nodes1 = new Node[] {new Node("node1")};
			Node[] nodes2 = new Node[] {new Node("node2")};

			GenericComparison comparison = GenericComparison.from("root", 0, nodes1, nodes2);
			assertThat(comparison.getRoot()).isEqualTo("root[0]");
			assertThat(comparison.getLeft()).isEqualTo(nodes1[0]);
			assertThat(comparison.getRight()).isEqualTo(nodes2[0]);
			assertThat(comparison.isMismatch()).isFalse();
		}

		@Test
		void onArrayNull() throws Exception {
			Node[] nodes1 = new Node[] {new Node("node1")};
			Node[] nodes2 = new Node[] {new Node("node2")};

			GenericComparison comparison = GenericComparison.from(null, 0, nodes1, nodes2);
			assertThat(comparison.getRoot()).isEqualTo("[0]");
			assertThat(comparison.getLeft()).isEqualTo(nodes1[0]);
			assertThat(comparison.getRight()).isEqualTo(nodes2[0]);
			assertThat(comparison.isMismatch()).isFalse();
		}

		@Test
		void onArrayOutOfBounds() throws Exception {
			Node[] nodes1 = new Node[] {new Node("node1")};
			Node[] nodes2 = new Node[] {new Node("node2")};

			GenericComparison comparison = GenericComparison.from(null, 1, nodes1, nodes2);
			assertThat(comparison.getRoot()).isEqualTo("<error>");
			assertThat(comparison.getLeft()).isNull();
			assertThat(comparison.getRight()).isNull();
			assertThat(comparison.isMismatch()).isTrue();
		}

	}

	@Nested
	class testSetMismatch {
		@Test
		void onTrue() throws Exception {
			GenericComparison comparison = new GenericComparison("root", new Node("name1"), new Node("name2"));
			comparison.setMismatch(true);

			assertThat(comparison.isMismatch()).isTrue();
		}

		@Test
		void onFalse() throws Exception {
			GenericComparison comparison = new GenericComparison("root", new Node("name1"), new Node("name2"));
			comparison.setMismatch(false);

			assertThat(comparison.isMismatch()).isFalse();
		}
	}

	@Nested
	class testEquals {
		@Test
		void withRootExpression() throws Exception {
			Node node1 = new Node("name1");
			Node node2 = new Node("name2");
			Node node11 = new Node("name1", new Node[] {node1});
			Node node12 = new Node("name1", new Node[] {node2});
			Node node21 = new Node("name2", new Node[] {node1});
			Node node22 = new Node("name2", new Node[] {node2});

			assertThat(GenericComparison.equals("root", node1, node1)).isTrue();
			assertThat(GenericComparison.equals("root", node1, node2)).isFalse();
			assertThat(GenericComparison.equals("root", node11, node11)).isTrue();
			assertThat(GenericComparison.equals("root", node11, node12)).isFalse();
			assertThat(GenericComparison.equals("root", node11, node21)).isFalse();
			assertThat(GenericComparison.equals("root", node11, node22)).isFalse();
		}

		@Test
		void withRootAndFieldConstraints() throws Exception {
			Node node1 = new Node("name1");
			Node node2 = new Node("name2");
			Node node11 = new Node("name1", new Node[] {node1});
			Node node12 = new Node("name1", new Node[] {node2});
			Node node21 = new Node("name2", new Node[] {node1});
			Node node22 = new Node("name2", new Node[] {node2});

			assertThat(GenericComparison.equals("root", node1, node1, comparingFields("name"))).isTrue();
			assertThat(GenericComparison.equals("root", node1, node2, comparingFields("name"))).isFalse();
			assertThat(GenericComparison.equals("root", node11, node11, comparingFields("name"))).isTrue();
			assertThat(GenericComparison.equals("root", node11, node12, comparingFields("name"))).isTrue();
			assertThat(GenericComparison.equals("root", node11, node21, comparingFields("name"))).isFalse();
			assertThat(GenericComparison.equals("root", node11, node22, comparingFields("name"))).isFalse();
		}
	}

	@Nested
	class testEval {
		@Test
		void onSimpleWorkSets() throws Exception {
			Node node = new Node("node");

			assertThat(new GenericComparison(null, node, node).eval(new WorkSet<>())).isTrue();
			assertThat(new GenericComparison(null, "name1", "name1").eval(new WorkSet<>())).isTrue();
			assertThat(new GenericComparison(null, node, null).eval(new WorkSet<>())).isFalse();
			assertThat(new GenericComparison(null, null, node).eval(new WorkSet<>())).isFalse();
			assertThat(new GenericComparison(null, node, "name1").eval(new WorkSet<>())).isFalse();
			assertThat(new GenericComparison(null, "name1", node).eval(new WorkSet<>())).isFalse();
			assertThat(new GenericComparison(null, "name1", "name2").eval(new WorkSet<>())).isFalse();
			assertThat(new GenericComparison(null, "name2", "name1").eval(new WorkSet<>())).isFalse();
			assertThat(new GenericComparison(null, new Node[0], new Node[] {node}).eval(new WorkSet<>())).isFalse();
			assertThat(new GenericComparison(null, new Node[] {node}, new Node[0]).eval(new WorkSet<>())).isFalse();
		}

		@Test
		void onNextStep() throws Exception {
			Node node1 = new Node("node1");
			Node node2 = new Node("node2");

			WorkSet<GenericComparison> todo = new WorkSet<>();
			new GenericComparison(null, node1, node2).eval(todo);
			assertThat(todo).containsExactly(
				new GenericComparison("name", node1.name, node2.name),
				new GenericComparison("children", node1.children, node2.children));
		}

		@Test
		void onArraysNextStep() throws Exception {
			Node[] nodes1 = new Node[] {new Node("node1")};
			Node[] nodes2 = new Node[] {new Node("node2")};

			WorkSet<GenericComparison> todo = new WorkSet<>();
			new GenericComparison(null, nodes1, nodes2).eval(todo);
			assertThat(todo).containsExactly(
				new GenericComparison("[0]", nodes1[0], nodes2[0]));
		}

		@Test
		void onCustomComparator() throws Exception {
			Node node = new Node("node");
			GenericComparator c = Mockito.mock(GenericComparator.class);

			assertThat(new GenericComparison(null, node, node).eval(c, new WorkSet<>())).isTrue();
			assertThat(new GenericComparison(null, "name1", "name1").eval(c, new WorkSet<>())).isTrue();

		}

		@Test
		void onCustomComparatorMatch() throws Exception {
			Node node = new Node("node");
			WorkSet<GenericComparison> todo = new WorkSet<>();
			GenericComparator c = Mockito.mock(GenericComparator.class);

			when(c.compare(any(GenericComparison.class), eq(todo))).thenReturn(MATCH);
			assertThat(new GenericComparison(null, node, null).eval(c, todo)).isTrue();
			assertThat(new GenericComparison(null, null, node).eval(c, todo)).isTrue();
		}

		@Test
		void onCustomComparatorMismatch() throws Exception {
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
		void onCustomComparatorNulls() throws Exception {
			Node node = new Node("node");
			WorkSet<GenericComparison> todo = new WorkSet<>();
			GenericComparator c = Mockito.mock(GenericComparator.class);

			when(c.compare(any(GenericComparison.class), eq(todo))).thenReturn(NOT_APPLYING);
			assertThat(new GenericComparison(null, node, null).eval(c, todo)).isFalse();
			assertThat(new GenericComparison(null, null, node).eval(c, todo)).isFalse();
		}

		@Test
		void onCustomComparatorLiterals() throws Exception {
			WorkSet<GenericComparison> todo = new WorkSet<>();
			GenericComparator c = Mockito.mock(GenericComparator.class);

			when(c.compare(any(GenericComparison.class), eq(todo))).thenReturn(NOT_APPLYING);
			assertThat(new GenericComparison(null, new String("s"), new String("s")).eval(c, todo)).isTrue();
			assertThat(new GenericComparison(null, new String("s1"), new String("s2")).eval(c, todo)).isFalse();
		}

		@Test
		void onCustomComparatorArrays() throws Exception {
			WorkSet<GenericComparison> todo = new WorkSet<>();
			GenericComparator c = Mockito.mock(GenericComparator.class);

			when(c.compare(any(GenericComparison.class), eq(todo))).thenReturn(NOT_APPLYING);
			assertThat(new GenericComparison(null, new String[0], new String[0]).eval(c, todo)).isTrue();
			assertThat(new GenericComparison(null, new String[] {"s"}, new String[] {"s"}).eval(c, todo)).isTrue();
			assertThat(new GenericComparison(null, new String[0], new String[] {"s"}).eval(c, todo)).isFalse();
			assertThat(new GenericComparison(null, new String[] {"s"}, new String[0]).eval(c, todo)).isFalse();
		}

		@Test
		void onCustomComparatorNextStep() throws Exception {
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
		void onCustomComparatorNextStepRestrictedToFields() throws Exception {
			Node node1 = new Node("node1");
			Node node2 = new Node("node2");
			WorkSet<GenericComparison> todo = new WorkSet<>();
			GenericComparator c = Mockito.mock(GenericComparator.class);

			when(c.compare(any(GenericComparison.class), eq(todo))).thenReturn(NOT_APPLYING);

			new GenericComparison(null, node1, node2, comparingFields("name")).eval(c, todo);

			assertThat(todo).containsExactly(
				new GenericComparison("name", node1.name, node2.name));
		}

		@Test
		void onCustomComparatorArraysNextStep() throws Exception {
			WorkSet<GenericComparison> todo = new WorkSet<>();
			GenericComparator c = Mockito.mock(GenericComparator.class);

			when(c.compare(any(GenericComparison.class), eq(todo))).thenReturn(NOT_APPLYING);

			new GenericComparison(null, new String[] {"s"}, new String[] {"s"}).eval(c, todo);

			assertThat(todo).containsExactly(new GenericComparison("[0]", "s", "s"));
		}

		@Test
		void onWithComparisonException() throws Exception {
			WorkSet<GenericComparison> remainder = new WorkSet<>();
			ComparisonStrategy strategy = Mockito.mock(ComparisonStrategy.class);
			when(strategy.extend(Mockito.any(GenericComparison.class))).thenThrow(new ComparisonException());

			assertThat(new GenericComparison(null, new Node("node1"), new Node("node2"), strategy).eval(remainder)).isFalse();
			assertThat(remainder).isEmpty();
		}

		@SuppressWarnings("unchecked")
		@Test
		void onCustomComparatorWithComparisonException() throws Exception {
			WorkSet<GenericComparison> remainder = new WorkSet<>();
			ComparisonStrategy strategy = Mockito.mock(ComparisonStrategy.class);
			when(strategy.extend(Mockito.any(GenericComparison.class))).thenThrow(new ComparisonException());
			GenericComparator comparator = Mockito.mock(GenericComparator.class);
			when(comparator.compare(any(GenericComparison.class), any(WorkSet.class))).thenReturn(NOT_APPLYING);

			assertThat(new GenericComparison(null, new Node("node1"), new Node("node2"), strategy).eval(comparator, remainder)).isFalse();
			assertThat(remainder).isEmpty();
		}
	}

	@Nested
	class testNewComparison {
		@Test
		void forArray() throws Exception {
			Object a = new String[] {"strA"};
			Object b = new String[] {"strB"};
			GenericComparison comparison = new GenericComparison("@", a, b);

			GenericComparison indexComparison = comparison.newComparison(0);

			assertThat(indexComparison.getRoot()).isEqualTo("@[0]");
			assertThat(indexComparison.isMismatch()).isFalse();
			assertThat(indexComparison.getLeft()).isEqualTo("strA");
			assertThat(indexComparison.getRight()).isEqualTo("strB");
			assertThat(comparison.newComparison(1).getRoot()).isEqualTo("<error>");
		}

		@Test
		void forObject() throws Exception {
			Object a = new Simple("strA");
			Object b = new Simple("strB");
			GenericComparison comparison = new GenericComparison("@", a, b);

			GenericComparison fieldComparison = comparison.newComparison("str");

			assertThat(fieldComparison.getRoot()).isEqualTo("@.str");
			assertThat(fieldComparison.isMismatch()).isFalse();
			assertThat(fieldComparison.getLeft()).isEqualTo("strA");
			assertThat(fieldComparison.getRight()).isEqualTo("strB");
			assertThat(comparison.newComparison("notexistingfield").getRoot()).isEqualTo("<error>");
		}
	}

	@Test
	void testRequireSameClass() throws Exception {
		assertThat(new GenericComparison("@", new Simple(), new Simple()).requireSameClass()).isSameAs(Simple.class);
		assertThatThrownBy(() -> new GenericComparison("@", new Simple(), new Complex()).requireSameClass()).isInstanceOf(ComparisonException.class);
	}

	@Nested
	class testCompare {
		@Test
		void onSimple() throws Exception {
			Node node1 = new Node("node1");
			Node node2 = new Node("node2");
			WorkSet<GenericComparison> todo = new WorkSet<>();
			todo.add(new GenericComparison(null, node1, node2));
			GenericComparison.compare(todo, (comparison, rem) -> comparison.eval(rem) ? NOT_APPLYING : MISMATCH);

			assertThat(todo.getDone()).containsExactly(
				new GenericComparison(null, node1, node2, null, null),
				new GenericComparison("name", node1.name, node2.name, null, true),
				new GenericComparison("children", node1.children, node2.children, null, null));

		}

		@Test
		void onRecursive() throws Exception {
			Node node1 = new Node("node1");
			Node node2 = new Node("node2");
			node1.children = new Node[] {node2};
			node2.children = new Node[] {node1};

			WorkSet<GenericComparison> todo = new WorkSet<>();
			todo.add(new GenericComparison(null, node1, node2));
			GenericComparison.compare(todo, (comparison, rem) -> comparison.eval(rem) ? NOT_APPLYING : MISMATCH);

			assertThat(todo.getDone()).containsExactly(
				new GenericComparison(null, node1, node2, null, null),
				new GenericComparison("name", node1.name, node2.name, null, true),
				new GenericComparison("children", node1.children, node2.children, null, false),
				new GenericComparison("children[0]", node1.children[0], node2.children[0], null, false),
				new GenericComparison("children[0].name", node1.children[0].name, node2.children[0].name, null, true),
				new GenericComparison("children[0].children", node1.children[0].children, node2.children[0].children, null, false));
		}
	}

	@Nested
	class testEqualsHashcode {
		@Test
		void simple() throws Exception {
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
		void recursive() throws Exception {
			Node node1 = new Node("name");
			node1.children = new Node[] {node1};
			Node node2 = new Node("name");
			node2.children = new Node[] {node2};

			assertThat(GenericComparison.equals("", node1, node2)).isTrue();
		}
	}

	@Test
	void testToString() throws Exception {
		Node node1 = new Node("name1");
		Node node2 = new Node("name2");
		assertThat(new GenericComparison("root", node1, node2).toString()).containsWildcardPattern("root*false");
	}

	private static class Node {

		private String name;
		private Node[] children;

		Node(String name) {
			this(name, new Node[0]);
		}

		Node(String name, Node[] children) {
			this.name = name;
			this.children = children;
		}

	}

}
