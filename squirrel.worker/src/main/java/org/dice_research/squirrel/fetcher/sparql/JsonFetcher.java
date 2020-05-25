package org.dice_research.squirrel.fetcher.sparql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.fetcher.Fetcher;
import org.dice_research.squirrel.fetcher.delay.Delayer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonFetcher implements Fetcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonFetcher.class);

	private InputStream is = null;

	@Override
	public void close() throws IOException {
		is.close();
	}

	@Override
	public File fetch(CrawleableUri uri, Delayer delayer) {
		try {
			delayer.getRequestPermission();
			is = uri.getUri().toURL().openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			File file = File.createTempFile("fetched_", "", FileUtils.getTempDirectory());
			FileWriter fw = new FileWriter(file);
			fw.write(json.toString());
			fw.close();
			uri.addData("type", "json");
			return file;
		} catch (Exception e) {
			LOGGER.error("Could not fetch Json from URI: " + uri.getUri().toString(), e);
		}
		return null;
	}

	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

}
