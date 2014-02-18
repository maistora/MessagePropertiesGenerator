package com.maistora.tool.mpg.readers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelToPropertiesReader implements PropertiesReader {

	private String excelFile;
	
	public ExcelToPropertiesReader(String file) {
		this.excelFile = file;
	}

	public String getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(String excelFile) {
		this.excelFile = excelFile;
	}

	@Override
	public Map<String, Map<String, Properties>> read() {
		System.out.println("Start collecting data from : " + excelFile);
		
		final Workbook myWorkBook = getExcelWorkbook(getExcelFile());
		final Map<String, Map<String, Properties>> result = new HashMap<String, Map<String,Properties>>();
		
		try {
			int sheetNum = 0;
			while (myWorkBook.getSheetAt(sheetNum) != null) {
				final Sheet sheet = myWorkBook.getSheetAt(sheetNum++);
				final Map<Integer, String> colHeadings = getColumnHeadings(sheet);
				final Map<String, Properties> langProp = new HashMap<String, Properties>();
				
				for (Integer col : colHeadings.keySet()) {
					langProp.putAll(getPropertiesFile(sheet, col, colHeadings));
					result.put(sheet.getSheetName(), langProp);
				}
			}
		} catch (IllegalArgumentException e) {
			System.out.println("No more data to read...");
		}

		System.out.println("Finish collecting data from excel : " + excelFile);

		return result;
	}

	private Workbook getExcelWorkbook(String excelFile) {
		
		final FileInputStream myInput = getFileInputStream(excelFile);
		final POIFSFileSystem myFileSystem = getFileSystem(myInput);
		try {
			myInput.close();
		} catch (IOException e) {
			System.err.println("Unable to close file input stream. Reason : " + e.getMessage()); // <-- HERE
		}
		
		return getWorkbook(myFileSystem);
	}

	private Workbook getWorkbook(final POIFSFileSystem myFileSystem) {
		Workbook myWorkBook = null;
		try {
			myWorkBook = new HSSFWorkbook(myFileSystem);
		} catch (IOException e) {
			System.err.println("Unable to create Excel Workbook POI object. Reason : " + e.getMessage());
			System.exit(-1);
		}
		return myWorkBook;
	}

	private FileInputStream getFileInputStream(String excelFile) {
		FileInputStream myInput = null;
		try {
			myInput = new FileInputStream(excelFile);
		} catch (FileNotFoundException e) {
			System.err.println("Unable to find Excel file : " + excelFile);
			System.exit(-1);
		}
		return myInput;
	}

	private POIFSFileSystem getFileSystem(FileInputStream myInput) {
		POIFSFileSystem myFileSystem = null;
		try {
			myFileSystem = new POIFSFileSystem(myInput);
		} catch (IOException e) {
			System.err.println("Unable to create Excel POI object. Reason : " + e.getMessage());
			System.exit(-1);
		}
		return myFileSystem;
	}

	private Map<String, Properties> getPropertiesFile(final Sheet sheet, Integer col, Map<Integer, String> colHeadings) {
		final Map<String, Properties> langProp = new HashMap<String, Properties>();
		final Properties propFile = new Properties();
		final Map<Integer, String> sheetKeys = getSheetKeys(sheet);
		
		for (Integer row : sheetKeys.keySet()) {
			final String cellValue = getCellValue(sheet, col, row);
			if (null != cellValue && !cellValue.equals("")) {
				propFile.put(sheetKeys.get(row), cellValue);
			}
		}
		
		if (!propFile.isEmpty()) {
			langProp.put(colHeadings.get(col), propFile);
		}
		
		return langProp;
	}

	private String getCellValue(Sheet sheet, Integer col, Integer row) {
		if (null == sheet.getRow(row).getCell(col)) {
			return null;
		}
		return sheet.getRow(row).getCell(col).getStringCellValue();
	}

	private Map<Integer, String> getSheetKeys(Sheet sheet) {
		final Map<Integer, String> keys = new HashMap<Integer, String>();
		int rowIdx = 1;
		Row row = sheet.getRow(rowIdx);

		while (row != null && row.getCell(0) != null) {
			keys.put(rowIdx, row.getCell(0).getStringCellValue());

			rowIdx++;
			row = sheet.getRow(rowIdx);
		}

		return keys;
	}
	
	private Map<Integer, String> getColumnHeadings(Sheet sheet) {
		final Row row = sheet.getRow(0);
		final Map<Integer, String> result = new HashMap<Integer, String>();
		
		if (row == null) {
			return result;
		}
 		
		int cellNum = 1;
		while (row.getCell(cellNum) != null) {
			final String stringCellValue = getCellValue(row, cellNum);
			result.put(cellNum, stringCellValue);
			cellNum++;
		}
		return result;
	}
	
	private String getCellValue(final Row row, int cellNum) {
		return row.getCell(cellNum).getStringCellValue().trim();
	}
}
