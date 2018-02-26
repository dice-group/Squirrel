package org.aksw.simba.squirrel.analyzer.htmlscraper;

import java.io.File;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YamlFilesParser {
	
//	private File yaml_file;
	
	
	public YamlFilesParser() {
		
	}
	
	
	public YamlFile loadParameters(){
		YamlFile yf = null;
		
		try {
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			yf = mapper.readValue(new File("src/main/resources/yaml/sample.yaml"), YamlFile.class);
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
