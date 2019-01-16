package net.amygdalum.testrecorder.deserializers;

import static java.util.Collections.emptyList;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TypeManager;

public class Adaptors {

	private Map<Class<? extends SerializedValue>, List<Adaptor<?>>> adaptors;

	public Adaptors() {
		this.adaptors = new LinkedHashMap<>();
	}

	@SuppressWarnings({ "rawtypes" })
	public Adaptors load(List<? extends Adaptor> adaptors) {
		for (Adaptor<?> adaptor : adaptors) {
			add(adaptor);
		}
		return this;
	}

	public Adaptors add(Adaptor<?> adaptor) {
		return add(adaptor.getAdaptedClass(), adaptor);
	}

	public Adaptors add(Class<? extends SerializedValue> clazz, Adaptor<?> adaptor) {
		List<Adaptor<?>> matching = adaptors.computeIfAbsent(clazz, key -> new LinkedList<>());
		if (matching.isEmpty() || adaptor.parent() == null) {
			matching.add(adaptor);
		} else if (matching.size() == 1) {
			Adaptor<?> existing = matching.get(0);
			if (existing.parent() == adaptor.getClass()) {
				matching.add(adaptor);
			} else {
				matching.add(0, adaptor);
			}
		} else {
			ListIterator<Adaptor<?>> iterator = matching.listIterator(matching.size());
			while (iterator.hasPrevious()) {
				Adaptor<?> prev = iterator.previous();
				if (prev.getClass() == adaptor.parent()) {
					break;
				} else if (adaptor.getClass() == prev.parent()) {
					iterator.next();
					break;
				}
			}
			if (iterator.hasPrevious()) {
				iterator.add(adaptor);
			} else {
				matching.add(0, adaptor);
			}
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T extends SerializedValue> Computation tryDeserialize(T value, TypeManager types, Deserializer generator) {
		Class<? extends SerializedValue> clazz = value.getClass();
		List<Adaptor<?>> matching = adaptors.getOrDefault(clazz, emptyList());
		for (Adaptor<?> match : matching) {
			if (match.matches(value.getType())) {
				try {
					return ((Adaptor<T>) match).tryDeserialize(value, generator);
				} catch (DeserializationException e) {
					continue;
				}
			}
		}
		return Computation.NULL;
	}

}
