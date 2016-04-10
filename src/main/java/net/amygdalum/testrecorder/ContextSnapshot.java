package net.amygdalum.testrecorder;

import java.lang.reflect.Type;
import java.util.List;

import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedInput;
import net.amygdalum.testrecorder.values.SerializedOutput;

public class ContextSnapshot {

	private Class<?> declaringClass;
	private Type resultType;
	private String methodName;
	private Type[] argumentTypes;

	private boolean valid;
	
	private SerializedValue setupThis;
	private SerializedValue[] setupArgs;
	private SerializedField[] setupGlobals;

	private SerializedValue expectThis;
	private SerializedValue expectResult;
	private SerializedValue expectException;
	private SerializedValue[] expectArgs;
	private SerializedField[] expectGlobals;
	
	private List<SerializedInput> setupInput;
	private List<SerializedOutput> expectOutput;

	public ContextSnapshot(Class<?> declaringClass, Type resultType, String methodName, Type... argumentTypes) {
		this.declaringClass = declaringClass;
		this.resultType = resultType;
		this.methodName = methodName;
		this.argumentTypes = argumentTypes;
		this.valid = true;
	}

	public void invalidate() {
		valid = false;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	public Type getResultType() {
		return resultType;
	}

	public String getMethodName() {
		return methodName;
	}

	public Type[] getArgumentTypes() {
		return argumentTypes;
	}

	public Type getThisType() {
		return setupThis.getType();
	}

	public SerializedValue getSetupThis() {
		return setupThis;
	}
	
	public void setSetupThis(SerializedValue setupThis) {
		this.setupThis = setupThis;
	}

	public SerializedValue[] getSetupArgs() {
		return setupArgs;
	}
	
	public void setSetupArgs(SerializedValue... setupArgs) {
		this.setupArgs = setupArgs;
	}
	
	public SerializedField[] getSetupGlobals() {
		return setupGlobals;
	}

	public void setSetupGlobals(SerializedField... setupGlobals) {
		this.setupGlobals = setupGlobals;
	}

	public SerializedValue getExpectThis() {
		return expectThis;
	}
	
	public void setExpectThis(SerializedValue expectThis) {
		this.expectThis = expectThis;
	}

	public SerializedValue getExpectResult() {
		return expectResult;
	}
	
	public void setExpectResult(SerializedValue expectResult) {
		this.expectResult = expectResult;
	}

	public SerializedValue getExpectException() {
		return expectException;
	}
	
	public void setExpectException(SerializedValue expectException) {
		this.expectException = expectException;
	}

	public SerializedValue[] getExpectArgs() {
		return expectArgs;
	}
	
	public void setExpectArgs(SerializedValue... expectArgs) {
		this.expectArgs = expectArgs;
	}
	
	public SerializedField[] getExpectGlobals() {
		return expectGlobals;
	}

	public void setExpectGlobals(SerializedField... expectGlobals) {
		this.expectGlobals = expectGlobals;
	}
	
	public List<SerializedInput> getSetupInput() {
		return setupInput;
	}
	
	public void setSetupInput(List<SerializedInput> setupInput) {
		this.setupInput = setupInput;
	}
	
	public List<SerializedOutput> getExpectOutput() {
		return expectOutput;
	}

	public void setExpectOutput(List<SerializedOutput> expectOutput) {
		this.expectOutput = expectOutput;
	}

}
