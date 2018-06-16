package net.amygdalum.testrecorder.runtime;

import java.util.List;

public interface ComparisonStrategy {

	List<GenericComparison> extend(GenericComparison comparison) throws ComparisonException;

	ComparisonStrategy next();

}
