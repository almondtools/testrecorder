package net.amygdalum.testrecorder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.util.Types.baseType;

import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.amygdalum.testrecorder.types.Profile;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializerSession;

public class DefaultSerializerSession implements SerializerSession {

	private Map<Object, SerializedValue> serialized;
	private Map<Class<?>, Profile> profiles;

	public DefaultSerializerSession() {
		serialized = new IdentityHashMap<>();
		profiles = new LinkedHashMap<>();
	}

	@Override
	public synchronized Profile log(Type type) {
		return profiles.computeIfAbsent(baseType(type), (t) -> Profile.start(t));
	}

	@Override
	public synchronized List<Profile> dumpProfiles() {
		List<Profile> dump = profiles.values().stream()
			.sorted()
			.limit(20)
			.collect(toList());
		profiles = new LinkedHashMap<>();
		return dump;
	}
	
	@Override
	public SerializedValue find(Object object) {
		return serialized.get(object);
	}

	@Override
	public void resolve(Object object, SerializedValue value) {
		serialized.put(object, value);
	}
}
