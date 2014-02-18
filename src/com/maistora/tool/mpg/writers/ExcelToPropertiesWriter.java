package com.maistora.tool.mpg.writers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class ExcelToPropertiesWriter implements PropertiesWriter {

	private static final String DIR_SEPARATOR = System.getProperty("file.separator");
	private static final String CHARSET = "UTF-8";
	private static final String UNDERSCORE = "_";
	private static final String PROPERTIES = ".properties";
	private static final String DEFAULT = "default";

	private String destDir;

	public ExcelToPropertiesWriter(String dir) {
		this.destDir = dir + DIR_SEPARATOR;
	}

	public String getDestDir() {
		return destDir;
	}

	@Override
	public void write(Map<String, Map<String, Properties>> data) {
		System.out.println("Preparing to write data to properties files...");
		final File dirToSave = new File(getDestDir());
		dirToSave.mkdirs();
		
		if (!dirToSave.isDirectory() || !dirToSave.canWrite()) {
			System.err.println("The directory does not exist or is not writable : " + dirToSave.getAbsolutePath());
			System.exit(-1);
		}

		for (String sheetName : data.keySet()) {
			for (String lang : data.get(sheetName).keySet()) {
				saveData(generateFileName(sheetName, lang), sort(data.get(sheetName).get(lang)));
			}
		}
		System.out.println("Finished writing data. Charset used: " + CHARSET);
	}

	private Properties sort(Properties prop) {
		final Properties sortedProp = new SortedProperties();
		sortedProp.putAll(prop);
		return sortedProp;
	}

	private void saveData(String filename, Properties propFile) {
		Writer out = null;
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			out = new OutputStreamWriter(fos, Charset.forName(CHARSET));
			propFile.store(out, null);
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find file: " + filename);
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("Error saving file: " + filename);
			System.exit(-1);
		} finally {
			if (null != out) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					System.err.println("Cannot close output stream. Reason: " + e.getMessage());
					System.exit(-1);
				}
			}
		}
	}

	private String generateFileName(String sheetName, String colTitle) {
		final StringBuilder filename = new StringBuilder();

		filename.append(sheetName);
		if (!colTitle.toLowerCase().equals(DEFAULT)) {
			filename.append(UNDERSCORE).append(colTitle.toLowerCase());
		}
		filename.append(PROPERTIES);

		return getDestDir() + filename.toString();
	}
	

	private static class SortedProperties extends Properties {
		private static final long serialVersionUID = 1L;

		@Override
		public Set<Object> keySet() {
			return Collections.unmodifiableSet(new TreeSet<Object>(super.keySet()));
		}

		@Override
		public synchronized Enumeration<Object> keys() {
			return Collections.enumeration(new TreeSet<Object>(super.keySet()));
		}
	}

}
