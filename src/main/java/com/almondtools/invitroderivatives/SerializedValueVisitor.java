package com.almondtools.invitroderivatives;

import com.almondtools.invitroderivatives.values.SerializedArray;
import com.almondtools.invitroderivatives.values.SerializedField;
import com.almondtools.invitroderivatives.values.SerializedList;
import com.almondtools.invitroderivatives.values.SerializedLiteral;
import com.almondtools.invitroderivatives.values.SerializedMap;
import com.almondtools.invitroderivatives.values.SerializedNull;
import com.almondtools.invitroderivatives.values.SerializedObject;
import com.almondtools.invitroderivatives.values.SerializedSet;

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
