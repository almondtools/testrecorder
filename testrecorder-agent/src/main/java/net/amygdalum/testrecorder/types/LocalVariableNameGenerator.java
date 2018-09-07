package net.amygdalum.testrecorder.types;

import static java.lang.Character.toLowerCase;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class LocalVariableNameGenerator {

	private Map<String, FreeIndexes> names;

	public LocalVariableNameGenerator() {
		this.names = new HashMap<>();
	}

	public String fetchName(Type type) {
		return fetchName(base(type));
	}

	public String fetchName(String base) {
		//TODO test var followed by var1 and var1 followed by 11 times var
		if (base.isEmpty() || Character.isDigit(base.charAt(base.length() - 1))) {
			base += "_";
		}
		return base + names.computeIfAbsent(base, key -> new FreeIndexes()).next();
	}

	private String base(Type type) {
		if (type instanceof Class<?>) {
			return base((Class<?>) type);
		} else if (type instanceof GenericArrayType) {
			return base(((GenericArrayType) type).getGenericComponentType()) + "Array";
		} else if (type instanceof ParameterizedType) {
			return base(((ParameterizedType) type).getRawType());
		} else {
			return normalize(type.getTypeName());
		}
	}

	private String base(Class<?> clazz) {
		if (clazz.isArray()) {
			return base(clazz.getComponentType()) + "Array";
		} else {
			return variableName(clazz);
		}
	}

	private String variableName(Class<?> clazz) {
		String variableName = clazz.getSimpleName();
		if (variableName.isEmpty()) {
			String fullName = clazz.getName();
			int lastdot = fullName.lastIndexOf('.');
			variableName = fullName.substring(lastdot + 1);
		}
		return normalize(variableName);
	}

	private String normalize(String name) {
		if (name.isEmpty()) {
			return "_";
		}
		name = name.replaceAll("[^\\w$]+", "_").replaceAll("^_+|_+$", "");
		char lastChar = name.charAt(name.length() - 1);
		if (Character.isDigit(lastChar)) {
			name = name + '_';
		}
		return toLowerCase(name.charAt(0)) + name.substring(1);
	}

	public void freeName(String name) {
		int pos = splitNameAndIndex(name);
		String base = name.substring(0, pos);
		int index = Integer.parseInt(name.substring(pos));
		FreeIndexes free = names.get(base);
		free.free(index);
	}

	private int splitNameAndIndex(String name) {
		int pos = name.length() - 1;
		while (pos >= 0 && Character.isDigit(name.charAt(pos))) {
			pos--;
		}
		return pos + 1;
	}

	private static class FreeIndexes {
		private int last;
		private BitSet free;

		FreeIndexes() {
			this.last = 0;
		}

		public void free(int index) {
			if (last == index) {
				last--;
			} else {
				if (free == null) {
					free = new BitSet();
				}
				free.set(index);
			}
		}

		public int next() {
			if (free != null) {
				int next = free.nextSetBit(0);
				if (next != -1) {
					free.clear(next);
					return next;
				}
				free = null;
			}
			last++;
			return last;
		}
	}
}
