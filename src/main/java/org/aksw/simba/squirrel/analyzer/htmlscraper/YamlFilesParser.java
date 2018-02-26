package org.aksw.simba.squirrel.analyzer.htmlscraper;

import java.io.File;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YamlFilesParser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(YamlFilesParser.class);
	
	private HtmlScraper htmlScraper = new HtmlScraper();
	
	
	public YamlFilesParser() {
		
	}
	
	
	public YamlFile loadParameters(){
		YamlFile yf = null;
		
		try {
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			yf = mapper.readValue(new File("src/main/resources/yaml/sample.yaml"), YamlFile.class);
			htmlScraper.scrape(yf);
			System.out.println(ReflectionToStringBuilder.toString(yf,ToStringStyle.MULTI_LINE_STYLE));
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return yf;
		
	}
	
	public static void main(String[] args) {
		new YamlFilesParser().loadParameters();
	}
	

}
