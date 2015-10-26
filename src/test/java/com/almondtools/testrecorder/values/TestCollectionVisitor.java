package com.almondtools.testrecorder.values;

import com.almondtools.testrecorder.SerializedCollectionVisitor;
import com.almondtools.testrecorder.visitors.TestValueVisitor;

public class TestCollectionVisitor extends TestValueVisitor implements SerializedCollectionVisitor<String> {

	@Override
	public String visitList(SerializedList value) {
		return "list";
	}

	@Override
	public String visitSet(SerializedSet value) {
		return "set";
	}

	@Override
	public String visitMap(SerializedMap value) {
		return "map";
	}
}