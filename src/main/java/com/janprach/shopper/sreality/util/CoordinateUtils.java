package com.janprach.shopper.sreality.util;

import javax.measure.quantity.Length;

import lombok.Value;
import lombok.val;

import org.jscience.geography.coordinates.UTM;
import org.jscience.geography.coordinates.crs.ReferenceEllipsoid;

public class CoordinateUtils {
	public static Coordinates seznamCzPpxPpy2WGS84(final double ppx, final double ppy) {
		return utm33N2WGS84(ppx / 32.0 - 3700000, ppy / 32.0 + 1300000);
	}

	public static Coordinates utm33N2WGS84(final double easting, final double northing) {
		val utm = UTM.valueOf(33, 'N', easting, northing, Length.UNIT);
		val wgs = UTM.utmToLatLong(utm, ReferenceEllipsoid.WGS84);
		return new Coordinates(wgs.getOrdinate(0), wgs.getOrdinate(1));
	}

	@Value
	public static class Coordinates {
		private double latitude;
		private double longitude;
	}
}
