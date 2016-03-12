package net.amygdalum.testrecorder.values;

import net.amygdalum.testrecorder.visitors.TestValueVisitor;

import net.amygdalum.testrecorder.SerializedCollectionVisitor;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedMap;
import net.amygdalum.testrecorder.values.SerializedSet;

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