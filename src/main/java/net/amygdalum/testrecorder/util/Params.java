package net.amygdalum.testrecorder.util;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.amygdalum.testrecorder.deserializers.Templates;

public abstract class Params {

	public static final Params NONE = new Params(new Class<?>[0]) {
		public Object[] values() {
			return new Object[0];
		};

		public Object getValue(java.lang.Class<?> clazz) {
			return null;
		};
	};

	private Class<?>[] classes;

	public Params(Class<?>[] classes) {
		this.classes = classes;
	}

	public Class<?>[] getClasses() {
		return classes;
	}

	public abstract Object getValue(Class<?> clazz);

	public String getDescription(Class<?> clazz) {
		try {
		    Object value = getValue(clazz);
		    if (Types.isLiteral(clazz)) {
                return Templates.asLiteral(value);
		    } else {
		        return Objects.toString(value);
		    }
		} catch (Exception e) {
		 	return "<undescribable>";
		}
	}

	public Object[] values() {
		List<Object> params = new ArrayList<>();
		for (Class<?> clazz : getClasses()) {
			Object value = getValue(clazz);
			params.add(value);
		}
		return params.toArray(new Object[0]);
	}

	public String getDescription() {
		List<String> params = new ArrayList<>();
		for (Class<?> clazz : getClasses()) {
			params.add(getDescription(clazz));
		}
		return params.stream().collect(joining(", ", "(", ")"));
	}

}
