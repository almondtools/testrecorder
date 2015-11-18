package com.almondtools.testrecorder.scenarios;

import java.net.URL;
import java.net.URLClassLoader;

public class InstrumentingClassLoader extends URLClassLoader {

	public InstrumentingClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
		// TODO Auto-generated constructor stub
	}

}
