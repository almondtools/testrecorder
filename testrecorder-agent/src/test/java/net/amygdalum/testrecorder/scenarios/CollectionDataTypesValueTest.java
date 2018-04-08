package net.amygdalum.testrecorder.scenarios;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.codeserializer.CodeSerializer;

public class CollectionDataTypesValueTest {

	@Test
	public void testCodeSerializerArrayList() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		assertThat(codeSerializer.serialize(createArrayList(1, 2, 3))).containsSequence(
			"arrayList1 = new ArrayList<>()",
			"arrayList1.add(1)",
			"arrayList1.add(2)",
			"arrayList1.add(3)");
	}

	@Test
	public void testCodeSerializerLinkedList() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		assertThat(codeSerializer.serialize(createLinkedList(1, 2, 3))).containsSequence(
			"linkedList1 = new LinkedList<>()",
			"linkedList1.add(1)",
			"linkedList1.add(2)",
			"linkedList1.add(3)");
	}

	@Test
	public void testCodeSerializerHashSet() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		assertThat(codeSerializer.serialize(createHashSet(1, 2, 3))).containsSequence(
			"hashSet1 = new HashSet<>()",
			"hashSet1.add(1)",
			"hashSet1.add(2)",
			"hashSet1.add(3)");
	}

	@Test
	public void testCodeSerializerLinkedHashSet() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		assertThat(codeSerializer.serialize(createLinkedHashSet(1, 2, 3))).containsSequence(
			"linkedHashSet1 = new LinkedHashSet<>()",
			"linkedHashSet1.add(1)",
			"linkedHashSet1.add(2)",
			"linkedHashSet1.add(3)");
	}

	@Test
	public void testCodeSerializerHashMap() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		assertThat(codeSerializer.serialize(createHashMap(1, 2, 3))).containsSequence(
			"hashMap1 = new HashMap<>()",
			"hashMap1.put(1, 2)",
			"hashMap1.put(2, 4)",
			"hashMap1.put(3, 6)");
	}

	@Test
	public void testCodeSerializerLinkedHashMap() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		assertThat(codeSerializer.serialize(createLinkedHashMap(1, 2, 3))).containsSequence(
			"linkedHashMap1 = new LinkedHashMap<>()",
			"linkedHashMap1.put(1, 2)",
			"linkedHashMap1.put(2, 4)",
			"linkedHashMap1.put(3, 6)");
	}

	private List<Integer> createArrayList(int... elements) {
		return IntStream.of(elements)
			.mapToObj(i -> i)
			.collect(toCollection(ArrayList::new));
	}

	private List<Integer> createLinkedList(int... elements) {
		return IntStream.of(elements)
			.mapToObj(i -> i)
			.collect(toCollection(LinkedList::new));
	}

	private Set<Integer> createHashSet(int... elements) {
		return IntStream.of(elements)
			.mapToObj(i -> i)
			.collect(toCollection(HashSet::new));
	}

	private Set<Integer> createLinkedHashSet(int... elements) {
		return IntStream.of(elements)
			.mapToObj(i -> i)
			.collect(toCollection(LinkedHashSet::new));
	}

	private Map<Integer, Integer> createHashMap(int... elements) {
		return IntStream.of(elements)
			.mapToObj(i -> i)
			.collect(toMap(i -> i, i -> i * 2, (i, j) -> j, HashMap::new));
	}

	private Map<Integer, Integer> createLinkedHashMap(int... elements) {
		return IntStream.of(elements)
			.mapToObj(i -> i)
			.collect(toMap(i -> i, i -> i * 2, (i, j) -> j, LinkedHashMap::new));
	}

}