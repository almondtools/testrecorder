package net.amygdalum.testrecorder.util;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPublic;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

import net.amygdalum.xrayinterface.ReflectionFailedException;

public class Reflections {

	private static final String MODIFIERS = "modifiers";

	public static <T extends AccessibleObject & Member> Accessing<T> accessing(T o) {
		return new Accessing<>(o);
	}

	public static class Accessing<T extends AccessibleObject & Member> {

		private T object;

		public Accessing(T object) {
			this.object = object;
		}

		public <S> S call(AccessFunction<T, S> code) throws ReflectiveOperationException {
			boolean reset = ensureAccess();
			try {
				S result = code.apply();
				return result;
			} finally {
				resetAccess(reset);
			}
		}

		public void exec(AccessConsumer<T> code) throws ReflectiveOperationException {
			boolean reset = ensureAccess();
			try {
				code.accept();
			} finally {
				resetAccess(reset);
			}
		}

		private boolean ensureAccess() {
			int modifiers = object.getModifiers();
			if (isFinal(modifiers) && object instanceof Field) {
				makeNonFinal((Field) object);
			}
			if (isPublic(modifiers) && isPublic(object.getDeclaringClass().getModifiers())) {
				return false;
			} else if (!object.isAccessible()) {
				object.setAccessible(true);
				return true;
			} else {
				return false;
			}
		}

		private void makeNonFinal(Field field) {
			try {
				Field modifiersField = Field.class.getDeclaredField(MODIFIERS);
				modifiersField.setAccessible(true);
				modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			} catch (ReflectiveOperationException e) {
				throw new ReflectionFailedException(e);
			}
		}

		private void resetAccess(boolean reset) {
			if (reset) {
				object.setAccessible(false);
			}
		}

	}
	
	public interface AccessFunction<T,S> {

		S apply() throws ReflectiveOperationException;
		
	}

	public interface AccessConsumer<T> {

		void accept() throws ReflectiveOperationException;
		
	}

}
