package net.amygdalum.testrecorder.visitors;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.SerializedCollectionVisitor;
import net.amygdalum.testrecorder.SerializedImmutableVisitor;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.SerializedValueVisitor;
import net.amygdalum.testrecorder.util.GenericObject;
import net.amygdalum.testrecorder.util.GenericObjectException;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedBigDecimal;
import net.amygdalum.testrecorder.values.SerializedBigInteger;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedMap;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.values.SerializedSet;

public class Deserializer implements SerializedValueVisitor<Object>, SerializedCollectionVisitor<Object>, SerializedImmutableVisitor<Object> {

	private Map<SerializedValue, Object> deserialized;

	public Deserializer() {
		this.deserialized = new IdentityHashMap<>();
	}

	@SuppressWarnings("unchecked")
	private <T> T fetch(SerializedValue key, Supplier<T> supplier, Consumer<T> init) {
		T value = (T) deserialized.get(key);
		if (value == null) {
			value = supplier.get();
			deserialized.put(key, value);
			init.accept(value);
		}
		return value;
	}

	@Override
	public Object visitField(SerializedField field) {
		throw new DeserializationException(field.toString());
	}

	@Override
	public Object visitObject(SerializedObject value) {
		try {
			Object object = fetch(value, () -> GenericObject.newInstance(value.getValueType()), base -> {
				for (SerializedField field : value.getFields()) {
					GenericObject.setField(base, field.getName(), field.getValue().accept(this));
				}
			});
			return object;
		} catch (GenericObjectException e) {
			throw new DeserializationException(value.toString());
		}
	}

	@Override
	public Object visitBigDecimal(SerializedBigDecimal value) {
		return fetch(value, () -> value.getValue(), noInit());
	}

	@Override
	public Object visitBigInteger(SerializedBigInteger value) {
		return fetch(value, () -> value.getValue(), noInit());
	}

	@Override
	public Object visitList(SerializedList value) {
		List<Object> list = fetch(value, ArrayList::new, base -> {
			for (SerializedValue element : value) {
				base.add(element.accept(this));
			}
		});
		return list;
	}

	@Override
	public Object visitMap(SerializedMap value) {
		Map<Object, Object> map = fetch(value, LinkedHashMap::new, base -> {
			for (Map.Entry<SerializedValue, SerializedValue> entry : value.entrySet()) {
				Object k = entry.getKey().accept(this);
				Object v = entry.getValue().accept(this);
				base.put(k, v);
			}
		});
		return map;
	}

	@Override
	public Object visitSet(SerializedSet value) {
		Set<Object> set = fetch(value, LinkedHashSet::new, base -> {
			for (SerializedValue element : value) {
				base.add(element.accept(this));
			}
		});
		return set;
	}

	@Override
	public Object visitArray(SerializedArray value) {
		Class<?> componentType = value.getRawType();
		SerializedValue[] rawArray = value.getArray();
		Object[] array = fetch(value, () -> (Object[]) Array.newInstance(componentType, rawArray.length), base -> {
			for (int i = 0; i < rawArray.length; i++) {
				base[i] = rawArray[i].accept(this);
			}
		});
		return array;
	}

	@Override
	public Object visitLiteral(SerializedLiteral value) {
		return fetch(value, () -> ((SerializedLiteral) value).getValue(), noInit());
	}

	@Override
	public Object visitNull(SerializedNull value) {
		return null;
	}

	@Override
	public Object visitUnknown(SerializedValue value) {
		throw new DeserializationException(value.toString());
	}

	private <T> Consumer<T> noInit() {
		return base -> {
		};
	}

}
