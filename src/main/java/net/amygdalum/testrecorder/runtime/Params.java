package net.amygdalum.testrecorder.runtime;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

public class Params {

	public static final Params NONE = new Params(new Class<?>[0], ValueFactory.NONE);

	private Class<?>[] classes;
    private ValueFactory factory;

	public Params(Class<?>[] classes, ValueFactory factory) {
		this.classes = classes;
        this.factory = factory;
	}

	public Class<?>[] getClasses() {
		return classes;
	}

	public Object[] values() {
		List<Object> params = new ArrayList<>();
		for (Class<?> clazz : getClasses()) {
			Object value = factory.newValue(clazz);
			params.add(value);
		}
		return params.toArray(new Object[0]);
	}

	public String getDescription() {
		List<String> params = new ArrayList<>();
		for (Class<?> clazz : getClasses()) {
			params.add(factory.getDescription(clazz));
		}
		return params.stream().collect(joining(", ", "(", ")"));
	}

}
