package net.amygdalum.testrecorder.util;

import static net.amygdalum.testrecorder.util.Distinct.distinct;

import java.util.IdentityHashMap;
import java.util.Queue;

public class IdentityWorkSet<T> extends WorkSet<T> implements Queue<T> {

	public IdentityWorkSet(Queue<T> base) {
		super(new IdentityHashMap<>(), base.stream()
			.filter(distinct())
			.collect(IdentityWorkQueue::new, IdentityWorkQueue::add, IdentityWorkQueue::addAll));
	}

	public IdentityWorkSet() {
		super(new IdentityHashMap<>(), new IdentityWorkQueue<>());
	}
}
