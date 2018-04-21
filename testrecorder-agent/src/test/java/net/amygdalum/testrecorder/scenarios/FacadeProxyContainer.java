package net.amygdalum.testrecorder.scenarios;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.Queue;

import net.amygdalum.testrecorder.profile.Facade;
import net.amygdalum.testrecorder.profile.Recorded;

public class FacadeProxyContainer {

	@Facade
	private FacadeInterfaceExample facade;

	public FacadeProxyContainer(String... values) throws IOException {
		Queue<String> queue = new LinkedList<>();
		for (String value : values) {
			queue.add(value);
		}
		this.facade = (FacadeInterfaceExample) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { FacadeInterfaceExample.class }, new InvocationHandler() {

			@Override
			public Object invoke(Object base, Method method, Object[] args) throws Throwable {
				if (method.getName().equals("read")) {
					return queue.remove();
				} else if (method.getName().equals("write")) {
					queue.add((String) args[0]);
					return null;
				} else if (method.getName().equals("readInt")) {
					return Integer.parseInt(queue.remove());
				} else if (method.getName().equals("writeInt")) {
					queue.add(String.valueOf(args[0]));
					return null;
				}
				throw new UnsupportedOperationException();
			}
		});
	}

	@Recorded
	public String readFromFacade() throws IOException {
		return facade.read();
	}

	@Recorded
	public void writeToFacade(String value) throws IOException {
		facade.write(value);
	}

	@Recorded
	public int readIntFromFacade() throws IOException {
		return facade.readInt();
	}

	@Recorded
	public void writeIntToFacade(int value) throws IOException {
		facade.writeInt(value);
	}

}