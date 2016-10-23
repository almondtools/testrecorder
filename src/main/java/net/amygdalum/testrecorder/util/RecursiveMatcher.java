package net.amygdalum.testrecorder.util;

import java.util.List;

public interface RecursiveMatcher {
	List<GenericComparison> mismatchesWith(String root, Object item);
}