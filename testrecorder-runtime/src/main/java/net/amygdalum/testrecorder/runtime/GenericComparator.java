package net.amygdalum.testrecorder.runtime;

import net.amygdalum.testrecorder.util.WorkSet;

public interface GenericComparator {

	GenericComparatorResult compare(GenericComparison comparison, WorkSet<GenericComparison> todo);

}
