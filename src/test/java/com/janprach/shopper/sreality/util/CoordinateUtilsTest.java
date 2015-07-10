package com.janprach.shopper.sreality.util;

import static org.assertj.core.api.Assertions.assertThat;
import lombok.val;

import org.assertj.core.data.Offset;
import org.junit.Test;

public class CoordinateUtilsTest {
	@Test
	public void testUTM() {
		val offsetTolerance = Offset.offset(0.001);
		val coordinate = CoordinateUtils.seznamCzPpxPpy2WGS84(132962386, 135957343);
		assertThat(coordinate.getLatitude()).isCloseTo(50.088567, offsetTolerance);
		assertThat(coordinate.getLongitude()).isCloseTo(14.371980, offsetTolerance);
	}
}
