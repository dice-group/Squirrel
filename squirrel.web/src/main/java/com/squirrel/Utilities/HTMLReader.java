package com.squirrel.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;

public abstract class HTMLReader {

    public static String getHTMLErrorPage(String errorMessage) {
        return TemplateHelper.replace(getText("./WEB-INF/pages/_exception.html"), Collections.singletonMap("error", Collections.singletonList(errorMessage)));
    }

    /**
     * Reads a given file and converts it to a String using {@link BufferedReader}
     *
     * @param filename the path and name of the (HTML) file, e.g. {@code ./WEB-INF/pages/index.html}
     * @return the content of the file (without line brakes)
     */
    @SuppressWarnings("resource")
	public static String getText(String filename) {
        try {
            return new BufferedReader(new FileReader(filename)).lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append) + "";
        } catch (FileNotFoundException e) {
            if (filename.endsWith("_exception.html")) {
            	System.out.println("not found " + filename);
                return "<html><head><title>Unresolvable error</title></head><body>FILE EXCEPTION. Only files are: " + Arrays.deepToString(new File("./").list()) + ""
                		+ "</br> not found : "+ filename +" </body></html>";
            }
            return getHTMLErrorPage(e.getMessage());
        }
    }
}
