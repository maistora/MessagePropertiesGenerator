package com.maistora.tool.mpg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.maistora.tool.mpg.readers.ExcelToPropertiesReader;
import com.maistora.tool.mpg.readers.PropertiesToExcelReader;
import com.maistora.tool.mpg.writers.ExcelToPropertiesWriter;
import com.maistora.tool.mpg.writers.PropertiesToExcelWriter;

public class TransitionTest {

	private static final String CHARSET = "UTF-8";
	private static final String TEST_ROOT_PATH = "test/com/maistora/tool/mpg/testData/";
	private static final String PROP_DIR_PATH = TEST_ROOT_PATH + "properties/";
	private static final String NEW_PROP_DIR_PATH = TEST_ROOT_PATH + "newProperties/";
	private static final String EXCEL_FILENAME = TEST_ROOT_PATH + "testExcel.xls";

	@BeforeClass
	public static void prepareData() {
		createExcel();
		createNewPropertiesFilesFromTheExcel();
	}

	@AfterClass
	public static void cleanData() {
		new File(EXCEL_FILENAME).delete();
		cleanNewPropertiesFiles();
	}
	
	@Test
	public void testPropertiesToExcelToProperties() {
		assertTrue(checkFilenames());
		final Map<String, Properties> oldProperties = getPropertiesFiles(PROP_DIR_PATH);
		final Map<String, Properties> newProperties = getPropertiesFiles(NEW_PROP_DIR_PATH);
		
		for (String filename : oldProperties.keySet()) {
			assertNotNull(newProperties.get(filename));
			
			final Properties oldProp = oldProperties.get(filename);
			final Properties newProp = newProperties.get(filename);
			
			assertEquals(oldProp, newProp);
		}
	}
	
	private boolean checkFilenames() {
		final File oldPropDir = new File(PROP_DIR_PATH);
		final File newPropDir = new File(NEW_PROP_DIR_PATH);
		final List<String> propList = Arrays.asList(newPropDir.list());
		for (String filename : oldPropDir.list()) {
			if (!propList.contains(filename)) {
				return false;
			}
		}
		return true;
	}

	private Map<String, Properties> getPropertiesFiles(String directory) {
		final File dir = new File(directory);
		final Map<String, Properties> props = new HashMap<String, Properties>();
		
		for (String file : dir.list()) {
			props.put(file, createPropertyFile(directory + file));
		}
		
		return props;
	}

	private Properties createPropertyFile(String file) {
		final Properties property = new Properties();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, Charset.forName(CHARSET));
			property.load(isr);
		} catch (FileNotFoundException e) {
			System.err.println("File " + file + " not found.");
		} catch (IOException e) {
			System.err.println("Error loading file " + file + ".");
		} finally {
			if (null != isr) {
				try {
					isr.close();
				} catch (IOException e) {
					System.err.println("Error while closing InputStreamReader. Reason: " + e.getMessage());
				}
			}
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					System.err.println("Error while closing FileInputStream. Reason: " + e.getMessage());
				}
			}
		}
		return property;
	}

	private static void cleanNewPropertiesFiles() {
		final File dir = new File(NEW_PROP_DIR_PATH);
		final File[] listFiles = dir.listFiles();
		for (File file : listFiles) {
			if (!file.delete()) {
				System.err.println("Failed to delete " + file);
			}
		}
	}

	private static void createExcel() {
		new PropertiesToExcelWriter(EXCEL_FILENAME).write(new PropertiesToExcelReader(PROP_DIR_PATH).read());
	}

	private static void createNewPropertiesFilesFromTheExcel() {
		new ExcelToPropertiesWriter(NEW_PROP_DIR_PATH).write(new ExcelToPropertiesReader(EXCEL_FILENAME).read());
	}
}
