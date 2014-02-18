package com.maistora.tool.mpg.readers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesToExcelReader implements PropertiesReader {

	private static final int SPACE_BETWEEN = 2;
	private static final String PROPERTIES = ".properties";
	private static final String CHARSET = "UTF-8";
	
	private String directory;
	
	public PropertiesToExcelReader(String dir) {
		this.setDirectory(dir + "\\");
	}
	
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getDirectory() {
		return directory;
	}
	
	@Override
	public Map<String, Map<String, Properties>> read() {
		final File dir = new File(getDirectory());
		
		if (!dir.isDirectory() || !dir.canWrite()) {
			System.err.println("The directory does not exist or is not writable : " + dir.getAbsolutePath());
			System.exit(-1);
		}

		final Map<String, Map<String, Properties>> filePropMap = new HashMap<String, Map<String, Properties>>();
		final Set<String> sheetNames = getSheetNames(getDirectory());
		
		for (String sheetName : sheetNames) {
			final Map<String, Properties> langProp = new HashMap<String, Properties>();
			for (String filename : dir.list()) {
				fillData(filePropMap, sheetName, langProp, filename);
			}
		}
		
		return filePropMap;
	}

	private void fillData(final Map<String, Map<String, Properties>> filePropMap, String sheetName,
						  final Map<String, Properties> langProp, String filename) {
		try {
			if (!filenameMatchesTheSheet(filename, sheetName)) {
				return;
			}
			final Properties propertiesFile = loadPropertieFile(getDirectory(), filename);

			final String lang = findLanguageFromFilename(filename);
			langProp.put(lang, propertiesFile);

			filePropMap.put(sheetName, langProp);

		} catch (FileNotFoundException e) {
			System.err.println("Cannot find file " + getDirectory() + filename);
		} catch (IOException e) {
			System.err.println("Cannot load propertie file " + getDirectory() + filename + ".\n Reason: " + e.getMessage());
		}
	}
	
	private Properties loadPropertieFile(String dir, String filename) throws IOException, FileNotFoundException {
		final Properties propertiesFile = new Properties();
		final InputStreamReader inputStreamUtf8 = loadFileWithUtf8Encoding(dir + filename);
		propertiesFile.load(inputStreamUtf8);
		inputStreamUtf8.close();
		
		return propertiesFile;
	}

	private InputStreamReader loadFileWithUtf8Encoding(String filename) throws FileNotFoundException {
		return new InputStreamReader(new FileInputStream(filename), Charset.forName(CHARSET));
	}

	private String findLanguageFromFilename(String filename) {
		final Pattern pattern = Pattern.compile("(.*)_(.{2})\\.properties");
		final Matcher matcher = pattern.matcher(filename);
		
		if (matcher.matches()) {
			return matcher.group(2);
		}
		
		return ".";
	}
	
	private boolean filenameMatchesTheSheet(String filename, String sheetName) {
		final Pattern pattern = Pattern.compile(sheetName + "(\\_.{2})?\\.properties");
		final Matcher matcher = pattern.matcher(filename);
		
		return matcher.matches();
	}

	private Set<String> getSheetNames(String dir) {
		final Set<String> filenames = new HashSet<String>();
		final File directory = new File(dir);
		
		if (!directory.isDirectory() || !directory.canWrite()) {
			System.err.println("The directory does not exist or is not writable : " + directory.getAbsolutePath());
			System.exit(-1);
		}
		
		final String[] filesInDir = directory.list();
		String currName = "";
		int lastIdx = -1;
		int fstIdx = -1;
		for (String fileName : filesInDir) {
			if (!fileName.toLowerCase().endsWith(PROPERTIES)) {
				continue;
			}
			fstIdx = fileName.lastIndexOf("_");
			lastIdx = fileName.lastIndexOf(".");
			if ((lastIdx - fstIdx) - 1 == SPACE_BETWEEN) {
				currName = fileName.substring(0, fstIdx);
			} else {
				currName = fileName.substring(0, lastIdx);
			}
			filenames.add(currName);
		}
		
		return filenames;
	}
}