package net.amygdalum.testrecorder.scenarios;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

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

import net.amygdalum.testrecorder.CodeSerializer;

public class CollectionDataTypesValueTest {

	@Test
	public void testCodeSerializerArrayList() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();
		
		assertThat(codeSerializer.serialize(createArrayList(1,2,3)), allOf(
			containsString("list1 = new ArrayList<>()"), 
			containsString("list1.add(1)"), 
			containsString("list1.add(2)"), 
			containsString("list1.add(3)")));
	}

	@Test
	public void testCodeSerializerLinkedList() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();
		
		assertThat(codeSerializer.serialize(createLinkedList(1,2,3)), allOf(
			containsString("list1 = new LinkedList<>()"), 
			containsString("list1.add(1)"), 
			containsString("list1.add(2)"), 
			containsString("list1.add(3)")));
	}
	
	@Test
	public void testCodeSerializerHashSet() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();
		
		assertThat(codeSerializer.serialize(createHashSet(1,2,3)), allOf(
			containsString("set1 = new HashSet<>()"), 
			containsString("set1.add(1)"), 
			containsString("set1.add(2)"), 
			containsString("set1.add(3)")));
	}

	@Test
	public void testCodeSerializerLinkedHashSet() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();
		
		assertThat(codeSerializer.serialize(createLinkedHashSet(1,2,3)), allOf(
			containsString("set1 = new LinkedHashSet<>()"), 
			containsString("set1.add(1)"), 
			containsString("set1.add(2)"), 
			containsString("set1.add(3)")));
	}
	
	@Test
	public void testCodeSerializerHashMap() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();
		
		assertThat(codeSerializer.serialize(createHashMap(1,2,3)), allOf(
			containsString("map1 = new HashMap<>()"), 
			containsString("map1.put(1, 2)"), 
			containsString("map1.put(2, 4)"), 
			containsString("map1.put(3, 6)")));
	}
	
	@Test
	public void testCodeSerializerLinkedHashMap() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();
		
		assertThat(codeSerializer.serialize(createLinkedHashMap(1,2,3)), allOf(
			containsString("map1 = new LinkedHashMap<>()"), 
			containsString("map1.put(1, 2)"), 
			containsString("map1.put(2, 4)"), 
			containsString("map1.put(3, 6)")));
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
			.collect(toMap(i -> i, i-> i *2, (i,j) -> j, HashMap::new));
	}
	
	private Map<Integer, Integer> createLinkedHashMap(int... elements) {
		return IntStream.of(elements)
			.mapToObj(i -> i)
			.collect(toMap(i -> i, i-> i * 2, (i,j) -> j, LinkedHashMap::new));
	}
	
}