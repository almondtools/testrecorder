package net.amygdalum.testrecorder.values;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.amygdalum.testrecorder.types.SerializedInteraction;
import net.amygdalum.testrecorder.types.SerializedValue;

public abstract class AbstractSerializedInteraction implements SerializedInteraction {

	protected int id;
	protected StackTraceElement[] call;
	protected Class<?> clazz;
	protected String name;
	protected Type resultType;
	protected SerializedValue result;
	protected Type[] types;
	protected SerializedValue[] arguments;

	public AbstractSerializedInteraction(int id, StackTraceElement[] call, Class<?> clazz, String name, Type resultType, Type[] types) {
		this.id = id;
		this.call = call;
		this.clazz = clazz;
		this.name = name;
		this.resultType = resultType;
		this.types = types;
	}

	public int id() {
		return System.identityHashCode(this);
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getCallerClass() {
		return call.length > 1 ? call[1].getClassName() : null;
	}

	@Override
	public String getCallerMethod() {
		return call.length > 1 ? call[1].getMethodName() : null;
	}

	@Override
	public int getCallerLine() {
		return call.length > 1 ? call[1].getLineNumber() : -1;
	}

	@Override
	public boolean prefixesStackTrace(StackTraceElement[] stackTrace) {
		if (call.length >= stackTrace.length) {
			return false;
		}

		int calleeIndex = 0;
		int calleeIndexInStackTrace = stackTrace.length - call.length;
		if (call.length > 1) {
			for (int callerIndex = calleeIndex + 1, callerIndexInStackTrace = calleeIndexInStackTrace + 1; // 
				callerIndex < call.length && callerIndexInStackTrace < stackTrace.length; //
				callerIndex++, callerIndexInStackTrace++) {
				if (!call[callerIndex].equals(stackTrace[callerIndexInStackTrace])) {
					return false;
				}
			}
		}
		return Objects.equals(call[calleeIndex].getClassName(), stackTrace[calleeIndexInStackTrace].getClassName())
			&& Objects.equals(call[calleeIndex].getMethodName(), stackTrace[calleeIndexInStackTrace].getMethodName());
	}

	@Override
	public Class<?> getDeclaringClass() {
		return clazz;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Type getResultType() {
		return resultType;
	}

	@Override
	public SerializedValue getResult() {
		return result;
	}

	@Override
	public Type[] getTypes() {
		return types;
	}

	@Override
	public SerializedValue[] getArguments() {
		return arguments;
	}

	@Override
	public List<SerializedValue> getAllValues() {
		List<SerializedValue> allValues = new ArrayList<>();
		allValues.add(result);
		for (SerializedValue argument : arguments) {
			allValues.add(argument);
		}
		return allValues;
	}

	@Override
	public int hashCode() {
		return clazz.hashCode() * 37
			+ name.hashCode() * 29
			+ resultType.hashCode() * 17
			+ (result == null ? 0 : result.hashCode() * 13)
			+ Arrays.hashCode(types) * 11
			+ Arrays.hashCode(arguments);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AbstractSerializedInteraction that = (AbstractSerializedInteraction) obj;
		return this.id == that.id
			&& Arrays.equals(this.call, that.call)
			&& this.clazz.equals(that.clazz)
			&& this.name.equals(that.name)
			&& this.resultType.equals(that.resultType)
			&& Objects.equals(this.result, that.result)
			&& Arrays.equals(this.types, that.types)
			&& Arrays.equals(this.arguments, that.arguments);
	}

}