package org.aksw.simba.squirrel.analyzer.htmlscraper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.aksw.simba.squirrel.configurator.HtmlScraperConfiguration;
import org.aksw.simba.squirrel.utils.TempPathUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * 
 * @author gsjunior
 *
 */
public class YamlFilesParser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(YamlFilesParser.class);
	
	private List<YamlFile> yfs = null;
	private final String fileExtension = "yaml";
	
	protected YamlFilesParser(File file)
			throws JsonParseException, JsonMappingException, IOException {
		yfs = loadFiles(file);
	}
	
	protected YamlFilesParser()
			throws JsonParseException, JsonMappingException, IOException {
		yfs = loadFiles(null);
	}
	
	private List<YamlFile> loadFiles(File file) {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		File folder = null;
		if(file != null) {
			 folder = file;
		}else {
			folder = new File(HtmlScraperConfiguration.getHtmlScraperConfiguration().getPath());
		}
		
		List<File> listYamlFiles = filterYamlFiles(TempPathUtils.searchPath4Files(folder));
		List<YamlFile> yamls = new ArrayList<YamlFile>();
		for(int i=0; i<listYamlFiles.size(); i++) {
			try {
				yamls.add(mapper.readValue(listYamlFiles.get(i), YamlFile.class));
			} catch (Exception e) {
				LOGGER.warn("",e);
			}
		}
		

		return yamls;
	}
	
	private List<File> filterYamlFiles(List<File> yamlList) {
		return yamlList.stream().filter(p -> FilenameUtils.getExtension(p.getAbsolutePath()).equals(fileExtension))
				.collect(Collectors.toList());
		
	}
	
	public List<YamlFile> getYamlFiles(){
		
		return yfs;
		
	}


}
