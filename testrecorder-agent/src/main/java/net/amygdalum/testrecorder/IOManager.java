package net.amygdalum.testrecorder;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IOManager {
	
	private static final Set<String> NONE = Collections.emptySet();
	
	private Map<String, Set<String>> in;
	private Map<String, Set<String>> out;

	public IOManager() {
		this.in = new HashMap<>();
		this.out = new HashMap<>();
	}
	
	public void propagate(String name, String superName, List<String> interfaceNames) {
		propagate(name, superName, interfaceNames, in);
		propagate(name, superName, interfaceNames, out);
	}

	private void propagate(String name, String superName, List<String> interfaceNames, Map<String, Set<String>> inout) {
		Set<String> propagatedIn = inout.computeIfAbsent(name, key -> new HashSet<>());
		propagatedIn.addAll(inout.getOrDefault(superName, NONE));
		for (String interfaceName : interfaceNames) {
			propagatedIn.addAll(inout.getOrDefault(interfaceName, NONE));
		}
	}
	
	public void registerInput(String className, String methodName, String methodDesc) {
		in.compute(className, (key, value ) -> {
			if (value == null) {
				value = new HashSet<>();
			}
			value.add(methodName + methodDesc);
			return value;
		});
	}

	public boolean isInput(String className, String methodName, String methodDesc) {
		return in.getOrDefault(className, NONE)
			.contains(methodName + methodDesc);
	}

	public void registerOutput(String className, String methodName, String methodDesc) {
		out.compute(className, (key, value ) -> {
			if (value == null) {
				value = new HashSet<>();
			}
			value.add(methodName + methodDesc);
			return value;
		});
	}
	
	public boolean isOutput(String className, String methodName, String methodDesc) {
		return out.getOrDefault(className, NONE)
			.contains(methodName + methodDesc);
	}

}
