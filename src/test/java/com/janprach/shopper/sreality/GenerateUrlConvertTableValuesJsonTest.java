package com.janprach.shopper.sreality;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import javax.script.ScriptEngineManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import lombok.val;

@Ignore("This is for one time use only. It extracts json constants from all.js")
public class GenerateUrlConvertTableValuesJsonTest {
	@Test
	public void testGenerateUrlConvertTableValuesJsonTest() throws Exception {
		val allJsString = IOUtils.toString(new URL("http://www.sreality.cz/js/all.js"), StandardCharsets.UTF_8);
		val pattern = Pattern.compile("Sreality\\.constant\\(.URL_CONVERT_TABLE_VALUES.,[^\\{]*\\{");
		val matcher = pattern.matcher(allJsString);
		if (matcher.find()) {
			val openingPosition = matcher.end() - 1;
			val closingPosition = findClosing(allJsString, openingPosition);
			val urlConvertTableValuesString = allJsString.substring(openingPosition, closingPosition + 1);
			val engineManager = new ScriptEngineManager();
			val engine = engineManager.getEngineByName("nashorn");
			engine.eval("var urlConvertTableValues = " + urlConvertTableValuesString);
			val urlConvertTableValues = engine.eval("JSON.stringify(urlConvertTableValues, null, '  ')").toString();
			FileUtils.writeStringToFile(new File("src/main/resources/URL_CONVERT_TABLE_VALUES.json"),
					urlConvertTableValues, StandardCharsets.UTF_8);
			// System.out.println(urlConvertTableValues);
		} else {
			System.err.println("ERROR: Cannot find URL_CONVERT_TABLE_VALUES in all.js");
		}
	}

	public int findClosing(final String string, final int openingPosition) {
		int closingPosition = openingPosition;
		int counter = 1;
		while (counter > 0) {
			char c = string.charAt(++closingPosition);
			if (c == '{') {
				counter++;
			} else if (c == '}') {
				counter--;
			}
		}
		return closingPosition;
	}
}
