package com.almondtools.invivoderived;

import com.almondtools.invivoderived.values.SerializedArray;
import com.almondtools.invivoderived.values.SerializedField;
import com.almondtools.invivoderived.values.SerializedList;
import com.almondtools.invivoderived.values.SerializedLiteral;
import com.almondtools.invivoderived.values.SerializedMap;
import com.almondtools.invivoderived.values.SerializedNull;
import com.almondtools.invivoderived.values.SerializedObject;
import com.almondtools.invivoderived.values.SerializedSet;

public interface SerializedValueVisitor<T> {

	T visitField(SerializedField field);

	T visitObject(SerializedObject value);

	T visitList(SerializedList value);

	T visitSet(SerializedSet value);

	T visitMap(SerializedMap value);

	T visitArray(SerializedArray value);

	T visitLiteral(SerializedLiteral value);

	T visitNull(SerializedNull value);


}
