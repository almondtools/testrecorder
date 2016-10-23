package net.amygdalum.testrecorder.util;

public interface GenericComparator {

	GenericComparatorResult compare(GenericComparison comparison, WorkSet<GenericComparison> todo);

}
