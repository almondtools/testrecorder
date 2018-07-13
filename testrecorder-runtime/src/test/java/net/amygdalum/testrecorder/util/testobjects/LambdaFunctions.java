package net.amygdalum.testrecorder.util.testobjects;

import java.io.Serializable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class LambdaFunctions {

	public static BiFunction<Integer, Integer, Integer> plus = new BiFunction<Integer, Integer, Integer>() {

		@Override
		public Integer apply(Integer t, Integer u) {
			return t + u;
		}
	};
	public static BiFunction<Integer, Integer, Integer> lplus = (x, y) -> x + y;
	public static BiFunction<Integer, Integer, Integer> splus = (BiFunction<Integer, Integer, Integer> & Serializable) (x, y) -> x + y;

	private int base;

	public LambdaFunctions(int base) {
		this.base = base;
	}

	public static Function<Integer, Integer> splusCapturing(int base) {
		return (Function<Integer, Integer> & Serializable) x -> x + base;
	}

	public static Function<Integer, Integer> invokeStatic() {
		return (Function<Integer, Integer> & Serializable) x -> x + 1;
	}

	public static Function<Integer, Integer> invokeSpecial() {
		return new LambdaFunctions(1).privateInvokeSpecial();
	}

	private Function<Integer, Integer> privateInvokeSpecial() {
		return (Function<Integer, Integer> & Serializable) x -> x + base;
	}

	public static Function<Integer, Integer> invokeVirtual() {
		return (Function<Integer, Integer> & Serializable) new VirtualIntegerFunction()::apply;
	}

	public static Function<Integer, Integer> invokeInterface() {
		IntegerFunction integerFunction = new InterfaceIntegerFunction();
		return (Function<Integer, Integer> & Serializable) integerFunction::apply;
	}

	public static Supplier<String> invokeNewSpecial() {
		return (Supplier<String> & Serializable) String::new;
	}

	public static class VirtualIntegerFunction implements Function<Integer, Integer>, Serializable {
		@Override
		public Integer apply(Integer i) {
			return i + 1;
		}

	}

	public interface IntegerFunction extends Function<Integer, Integer> {

	}

	public static class InterfaceIntegerFunction implements IntegerFunction, Serializable {
		@Override
		public Integer apply(Integer i) {
			return i + 1;
		}

	}
}
