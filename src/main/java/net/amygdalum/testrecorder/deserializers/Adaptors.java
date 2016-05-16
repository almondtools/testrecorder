package net.amygdalum.testrecorder.deserializers;

import static java.util.Collections.emptyList;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.SerializedValue;

public class Adaptors<G> {

	private Map<Class<? extends SerializedValue>, List<Adaptor<?, G>>> adaptors;

	public Adaptors() {
		this.adaptors = new LinkedHashMap<>();
	}
	
	public Adaptors<G> add(Class<? extends SerializedValue> clazz, Adaptor<?, G> adaptor) {
		List<Adaptor<?, G>> matching = adaptors.computeIfAbsent(clazz, key -> new LinkedList<>());
		if (matching.isEmpty() || adaptor.parent() == null) {
			matching.add(adaptor);
		} else if (matching.size() == 1) {
			Adaptor<?, G> existing = matching.get(0);
			if (existing.parent() == adaptor.getClass()) {
				matching.add(adaptor);
			} else {
				matching.add(0, adaptor);
			}
		} else {
			ListIterator<Adaptor<?, G>> iterator = matching.listIterator(matching.size());
			while (iterator.hasPrevious()) {
				Adaptor<?, G> prev = iterator.previous();
				if (prev.getClass() == adaptor.parent()) {
					break;
				} else if (adaptor.getClass() == prev.parent()) {
					iterator.next();
					break;
				}
			}
			if (iterator.hasPrevious()) {
				iterator.previous();
				iterator.add(adaptor);
			} else {
				matching.add(0, adaptor);
			}
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T extends SerializedValue> Computation tryDeserialize(T value, TypeManager types, G generator) {
		Class<? extends SerializedValue> clazz = value.getClass();
		List<Adaptor<?, G>> matching = adaptors.getOrDefault(clazz, emptyList());
		for (Adaptor<?, G> match : matching) {
			if (match.matches(value.getType())) {
				try {
					return ((Adaptor<T, G>) match).tryDeserialize(value, generator);
				} catch (DeserializationException e) {
					continue;
				}
			}
		}
		return null;
	}

}
