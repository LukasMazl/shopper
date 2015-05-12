package com.janprach.shopper.sreality.util;

import javax.measure.quantity.Length;

import lombok.Value;
import lombok.val;

import org.jscience.geography.coordinates.UTM;
import org.jscience.geography.coordinates.crs.ReferenceEllipsoid;

public class CoordinateUtils {
	public static CoordinateWGS84 seznamCzPpxPpy2CoordinateWGS84(final double ppx, final double ppy) {
		return utm33N2CoordinateWGS84(ppx / 32.0 - 3700000, ppy / 32.0 + 1300000);
	}

	public static CoordinateWGS84 utm33N2CoordinateWGS84(final double easting, final double northing) {
		val utm = UTM.valueOf(33, 'N', easting, northing, Length.UNIT);
		val wgs = UTM.utmToLatLong(utm, ReferenceEllipsoid.WGS84);
		return new CoordinateWGS84(wgs.getOrdinate(0), wgs.getOrdinate(1));
	}

	@Value
	public static class CoordinateWGS84 {
		private double latitude;
		private double longitude;
	}
}
