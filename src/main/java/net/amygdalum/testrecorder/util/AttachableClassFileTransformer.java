package net.amygdalum.testrecorder.util;

import static java.util.stream.Collectors.joining;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;

public abstract class AttachableClassFileTransformer implements ClassFileTransformer {

	public AttachableClassFileTransformer attach(Instrumentation inst) {
		try {
			inst.addTransformer(this, true);
			Class<?>[] classesToRetransform = classesToRetransform();
			if (classesToRetransform.length > 0) {
				inst.retransformClasses(classesToRetransform);
			}
		} catch (RuntimeException | UnmodifiableClassException e) {
			System.err.println("unexpected class transforming restriction: " + e.getMessage());
			e.printStackTrace(System.err);
		}
		return this;
	}

	public void detach(Instrumentation inst) {
		try {
			inst.removeTransformer(this);
			Class<?>[] classesToRetransform = classesToRetransform();
			if (classesToRetransform.length > 0) {
				System.out.println("restoring " + Arrays.stream(classesToRetransform).map(Class::getName).collect(joining(", ")));
				inst.retransformClasses(classesToRetransform);
			}
		} catch (RuntimeException | UnmodifiableClassException e) {
			System.err.println("unexpected class transforming restriction: " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}

	public abstract Class<?>[] classesToRetransform();

}
