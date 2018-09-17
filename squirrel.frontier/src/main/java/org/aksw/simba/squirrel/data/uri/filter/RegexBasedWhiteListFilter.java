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

public class RegexBasedWhiteListFilter extends AbstractKnownUriFilterDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegexBasedWhiteListFilter.class);

    public static RegexBasedWhiteListFilter create(KnownUriFilter decorated, File whitelistfile) {
        try {
            Set<String> whiteList = loadWhiteList(whitelistfile);
            return new RegexBasedWhiteListFilter(decorated, whiteList);
        } catch (IOException e) {
            LOGGER.error("A problem was found when loading the WhiteList");
        }
        return null;
    }

    private Set<String> whiteList;

    public RegexBasedWhiteListFilter(KnownUriFilter decorated, Set<String> whiteList) {
        super(decorated);
        this.whiteList = whiteList;
    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        if (super.isUriGood(uri) && (whiteList == null || whiteList.isEmpty())) {
            return true;
        }

        else if (super.isUriGood(uri) && whiteList != null && !whiteList.isEmpty()) {

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

    protected static Set<String> loadWhiteList(File whiteListFile) throws IOException {
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
    public void add(CrawleableUri uri, long timestamp) {
        super.add(uri, timestamp);

    }

}
