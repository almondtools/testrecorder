package net.amygdalum.testrecorder.profile;

import java.lang.reflect.Proxy;

public class ProxyClasses implements Classes {

	public ProxyClasses() {
	}

	public static ProxyClasses proxies() {
		return new ProxyClasses();
	}

	@Override
	public boolean matches(Class<?> type) {
		return Proxy.isProxyClass(type);
	}

	@Override
	public boolean matches(String className) {
		return className.startsWith("com/sun/proxy/$Proxy");
	}

}
