package net.amygdalum.testrecorder.scenarios;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Supplier;

import net.amygdalum.testrecorder.profile.Recorded;

@SuppressWarnings({ "unchecked" })
public class Lambdas {

	private Function<Object, Object> id = o -> o;
	private Function<Object, Object> serializableId = (Serializable & Function<Object, Object>) o -> o;
	
	public Lambdas() {
	}
	
	@Recorded
	public Object id(Object o) {
		return id.apply(o);
	}

	@Recorded
	public Object serializedId(Object o) {
		return serializableId.apply(o);
	}

	@Recorded
	public Object exec(Supplier<Object> object) {
		return object.get();
	}

	@Recorded
	public Supplier<Object> defer(Object object) {
		return () -> object;
	}

}