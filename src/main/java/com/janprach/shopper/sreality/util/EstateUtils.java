package com.janprach.shopper.sreality.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.janprach.shopper.sreality.entity.Estate;

public class EstateUtils {
	public static void addHistoryRecord(final Estate estate, final String message) {
		final String msg = DateFormatUtils.format(new Date(), "dd.MM.yyyy") + " " + message;
		final String oldHistory = estate.getHistory();

		if (StringUtils.isEmpty(oldHistory)) {
			estate.setHistory(msg);
		} else {
			estate.setHistory(msg + "\n" + oldHistory);
		}
	}
}
