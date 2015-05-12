package com.janprach.shopper.sreality.util;

import static org.assertj.core.api.Assertions.assertThat;
import lombok.val;

import org.assertj.core.data.Offset;
import org.junit.Test;

public class CoordinateUtilsTest {
	@Test
	public void testUTM() {
		val offsetTolerance = Offset.offset(0.001);
		val wgs84 = CoordinateUtils.seznamCzPpxPpy2CoordinateWGS84(132962386, 135957343);
		assertThat(wgs84.getLatitude()).isCloseTo(50.088567, offsetTolerance);
		assertThat(wgs84.getLongitude()).isCloseTo(14.371980, offsetTolerance);
	}
}
