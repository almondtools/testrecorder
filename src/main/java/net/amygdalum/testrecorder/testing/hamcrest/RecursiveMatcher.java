package net.amygdalum.testrecorder.testing.hamcrest;

import java.util.List;

import net.amygdalum.testrecorder.runtime.GenericComparison;

public interface RecursiveMatcher {
	List<GenericComparison> mismatchesWith(String root, Object item);
}