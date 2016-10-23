package net.amygdalum.testrecorder.util;

import static net.amygdalum.testrecorder.util.GenericObject.copyArrayValues;
import static net.amygdalum.testrecorder.util.GenericObject.copyField;
import static net.amygdalum.testrecorder.util.Types.allFields;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Queue;

public class SetupInput implements InputProvider {

	private Queue<InputProvision> provided;

	public SetupInput() {
		this.provided = new LinkedList<>();
	}

	public SetupInput provide(Class<?> clazz, String method, Object result, Object... args) {
		provided.add(new InputProvision(clazz, method, result, args));
		return this;
	}

	@Override
	public Object requestInput(Class<?> clazz, String method, Object... args) {
		InputProvision providedInput = provided.remove();
		providedInput.verify(clazz, method, args);
		sync(providedInput.args, args);
		return providedInput.result;
	}

	private void sync(Object[] fromArgs, Object[] toArgs) {
		for (int i = 0; i < toArgs.length; i++) {
			sync(fromArgs[i], toArgs[i]);
		}
	}

	private void sync(Object from, Object to) {
		if (from.getClass() != to.getClass()) {
			return;
		}
		Class<?> current = from.getClass();
		if (current.isArray()) {
			copyArrayValues(from, to);
			return;
		}
		for (Field field : allFields(current)) {
			copyField(field, from, to);
		}
	}

	private static class InputProvision {

		private Class<?> clazz;
		private String method;
		private Object result;
		private Object[] args;

		public InputProvision(Class<?> clazz, String method, Object result, Object... args) {
			this.clazz = clazz;
			this.method = method;
			this.result = result;
			this.args = args;
		}

		public void verify(Class<?> clazz, String method, Object[] args) {
			if (!this.clazz.equals(clazz)) {
				throw new AssertionError("expected output " + this.clazz.getName() + ", but found " + clazz.getName());
			}
			if (!this.method.equals(method)) {
				throw new AssertionError("expected output " + this.method + ", but found " + method);
			}
			if (this.args.length != args.length) {
				throw new AssertionError("expected output " + this.args.length + " arguments, but found " + args.length + " arguments");
			}
		}

	}
}
