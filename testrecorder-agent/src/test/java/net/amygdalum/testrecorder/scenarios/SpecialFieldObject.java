package net.amygdalum.testrecorder.scenarios;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.amygdalum.testrecorder.profile.Recorded;

public class SpecialFieldObject {

	private SpecialInterface delegateInterface;
	
	public SpecialFieldObject() {
	}

	public void init() {
		delegateInterface = (SpecialInterface) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{SpecialInterface.class}, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return Integer.valueOf(42);
			}
		});
	}
	
	@Recorded
	public int method() {
		return delegateInterface.method();
	}

}