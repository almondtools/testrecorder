package net.amygdalum.testrecorder.util;

import static java.util.stream.Collectors.joining;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import net.amygdalum.testrecorder.Logger;


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
		Set<Class<?>> classes = new LinkedHashSet<>();
		classes.addAll(filterClassesToRetransform(loaded));
		classes.addAll(getClassesToRetransform());
		return classes.toArray(new Class[0]);
	}
	
	public abstract Collection<Class<?>> filterClassesToRetransform(Class<?>[] loaded);
	public abstract Collection<Class<?>> getClassesToRetransform();

}
