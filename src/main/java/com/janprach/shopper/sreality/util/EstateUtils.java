package com.janprach.shopper.sreality.util;

import com.janprach.shopper.sreality.entity.Estate;
import com.janprach.shopper.sreality.entity.History;
import com.janprach.shopper.sreality.entity.HistoryType;

public class EstateUtils {
	public static void addHistoryRecord(final Estate estate, final HistoryType active,
			final String message) {
		History history = new History(estate, active, message);
		estate.getHistories().add(history);
	}
}
