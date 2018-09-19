package net.amygdalum.testrecorder.types;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class AbstractSerializedInteraction implements SerializedInteraction, Serializable {

	protected int id;
	protected MethodSignature signature;
	protected SerializedResult result;
	protected SerializedArgument[] arguments;

	public AbstractSerializedInteraction(int id, MethodSignature signature) {
		this.id = id;
		this.signature = signature;
		this.arguments = new SerializedArgument[0];
	}

	public int id() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean isStatic() {
		return id == STATIC;
	}

	@Override
	public boolean isComplete() {
		if (result == null) {
			return false;
		}
		if (arguments == null || arguments.length != signature.argumentTypes.length) {
			return false;
		}
		return true;
	}

	@Override
	public boolean hasResult() {
		return signature.resultType != null
			&& signature.resultType != void.class
			&& result != null;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public Class<?> getDeclaringClass() {
		return signature.declaringClass;
	}
	
	@Override
	public String getMethodName() {
		return signature.methodName;
	}
	
	@Override
	public Type getResultType() {
		return signature.resultType;
	}
	
	@Override
	public Type[] getArgumentTypes() {
		return signature.argumentTypes;
	}

	@Override
	public SerializedResult getResult() {
		return result;
	}

	@Override
	public SerializedArgument[] getArguments() {
		return arguments;
	}

	@Override
	public List<SerializedValue> getAllValues() {
		List<SerializedValue> allValues = new ArrayList<>();
		allValues.add(result.getValue());
		for (SerializedArgument argument : arguments) {
			allValues.add(argument.getValue());
		}
		return allValues;
	}

	@Override
	public int hashCode() {
		return signature.hashCode() * 11
			+ (result == null ? 0 : result.hashCode() * 13)
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
			&& this.signature.equals(that.signature)
			&& Objects.equals(this.result, that.result)
			&& Arrays.equals(this.arguments, that.arguments);
	}

	protected SerializedArgument[] argumentsOf(SerializedValue[] argumentValues) {
		if (argumentValues == null) {
			return new SerializedArgument[0];
		} else {
			SerializedArgument[] arguments = new SerializedArgument[argumentValues.length];
			for (int i = 0; i < arguments.length; i++) {
				arguments[i] = new SerializedArgument(i, signature, argumentValues[i]);
			}
			return arguments;
		}
	}

	protected SerializedResult resultOf(SerializedValue resultValue) {
		if (resultValue == null) {
			return null;
		} else {
			return new SerializedResult(signature, resultValue);
		}
	}

}