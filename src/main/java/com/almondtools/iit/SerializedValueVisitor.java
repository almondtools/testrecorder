package com.almondtools.iit;

import com.almondtools.iit.values.SerializedArray;
import com.almondtools.iit.values.SerializedField;
import com.almondtools.iit.values.SerializedList;
import com.almondtools.iit.values.SerializedLiteral;
import com.almondtools.iit.values.SerializedMap;
import com.almondtools.iit.values.SerializedNull;
import com.almondtools.iit.values.SerializedObject;
import com.almondtools.iit.values.SerializedSet;

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
