package org.aksw.simba.squirrel.data.uri.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegexBasedWhiteListFilter extends RDBKnownUriFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegexBasedWhiteListFilter.class);

	private Set<String> whiteList;

	public RegexBasedWhiteListFilter(String hostname, Integer port) {
		super(hostname, port);
	}

	public RegexBasedWhiteListFilter(String hostname, Integer port, File whitelistfile) {
		super(hostname, port);
		try {
			whiteList = loadWhiteList(whitelistfile);
		} catch (IOException e) {
			LOGGER.error("A problem was found when loading the WhiteList");
		}
	}

	@Override
	public boolean isUriGood(CrawleableUri uri) {
		if (super.isUriGood(uri) && whiteList != null && whiteList.size() > 0) {

			for (String s : whiteList) {

				Pattern p = Pattern.compile(s.toLowerCase());
				Matcher m = p.matcher(uri.getUri().toString().toLowerCase());

				if (m.find()) {
					return true;
				}

			}

		}
		return false;
	}

	private Set<String> loadWhiteList(File whiteListFile) throws IOException {
		Set<String> list = new LinkedHashSet<String>();

		FileReader fr = new FileReader(whiteListFile);
		BufferedReader br = new BufferedReader(fr);

		String line;

		while ((line = br.readLine()) != null) {
			list.add(line);
		}

		br.close();

		return list;
	}

	@Override
	public void add(CrawleableUri uri) {
		super.add(uri);

	}

	@Override
	public void add(CrawleableUri uri, long timestamp) {
		super.add(uri, timestamp);

	}

	@Override
	public void close() {
		super.close();

	}

	@Override
	public void open() {
		super.open();

	}

}
