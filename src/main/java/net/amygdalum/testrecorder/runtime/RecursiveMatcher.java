package net.amygdalum.testrecorder.runtime;

import java.util.List;

public interface RecursiveMatcher {
	List<GenericComparison> mismatchesWith(String root, Object item);
}