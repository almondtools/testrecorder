package net.amygdalum.testrecorder.util;

import static java.util.stream.Collectors.joining;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


public abstract class AttachableClassFileTransformer implements ClassFileTransformer {
	
	protected CircularityLock lock = new CircularityLock();
	
	public AttachableClassFileTransformer attach(Instrumentation inst) {
		try {
			Class<?>[] classesToRetransform = classesToRetransform(inst.getAllLoadedClasses());
			inst.addTransformer(this, true);
			if (classesToRetransform.length > 0) {
				inst.retransformClasses(classesToRetransform);
			}
		} catch (RuntimeException | UnmodifiableClassException e) {
			Logger.error("unexpected class transforming restriction: ", e);
		}
		return this;
	}

	public void detach(Instrumentation inst) {
		try {
			inst.removeTransformer(this);
			Class<?>[] classesToRetransform = classesToRetransform(new Class[0]);
			if (classesToRetransform.length > 0) {
				Logger.info("restoring " + Arrays.stream(classesToRetransform).map(Class::getName).collect(joining(", ")));
				inst.retransformClasses(classesToRetransform);
			}
		} catch (RuntimeException | UnmodifiableClassException e) {
			Logger.error("unexpected class transforming restriction: ", e);
		}
	}

	public Class<?>[] classesToRetransform(Class<?>[] loaded) {
		if (loaded == null) {
			return new Class[0];
		}
		List<Class<?>> closure = new LinkedList<>();
		WorkSet<Class<?>> todo = new WorkSet<>();
		todo.addAll(filterClassesToRetransform(loaded));
		todo.addAll(getClassesToRetransform());
		while (todo.hasMoreElements()) {
			Class<?> current = todo.remove();
			if (current.getSuperclass() != null) {
				todo.add(current.getSuperclass());
			}
			for (Class<?> interfaceClazz : current.getInterfaces()) {
				todo.add(interfaceClazz);
			}
			insert(current, closure);
		}
		return closure.toArray(new Class[0]);
	}
	
	private void insert(Class<?> clazz, List<Class<?>> closure) {
		ListIterator<Class<?>> iterator = closure.listIterator();
		while (iterator.hasNext()) {
			Class<?> next = iterator.next();
			if (clazz.isAssignableFrom(next)) {
				iterator.set(clazz);
				iterator.add(next);
				return;
			}
		}
		closure.add(clazz);
	}

	public abstract Collection<Class<?>> filterClassesToRetransform(Class<?>[] loaded);
	public abstract Collection<Class<?>> getClassesToRetransform();

}
