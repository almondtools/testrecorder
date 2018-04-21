package net.amygdalum.testrecorder.runtime;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class PlaceHolderInvocationHandler implements InvocationHandler {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return null;
	}

}
