package com.almondtools.invivoderived.scenarios;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import com.almondtools.invivoderived.Snapshot;

public class GenericCollectionDataTypes {

	public GenericCollectionDataTypes() {
	}

	@Snapshot
	public List<BigInteger> bigIntegerLists(List<BigInteger> ints) {
		ints.add(ints.stream()
			.reduce((bi1, bi2) -> bi1.add(bi2))
			.orElse(BigInteger.ONE));
		return ints;
	}

	@Snapshot
	public List<BigDecimal> bigDecimalLists(List<BigDecimal> decs) {
		decs.add(decs.stream()
			.reduce((bi1, bi2) -> bi1.add(bi2))
			.orElse(BigDecimal.ONE.setScale(2))
			.divide(BigDecimal.valueOf(2 * decs.size() + 1), RoundingMode.UP));
		return decs;
	}

}