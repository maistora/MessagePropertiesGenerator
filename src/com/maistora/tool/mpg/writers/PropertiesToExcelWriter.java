package com.maistora.tool.mpg.writers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class PropertiesToExcelWriter implements PropertiesWriter {

	private static final int CELL_WIDTH = 10000;
	
	private String excelFile;
	
	public PropertiesToExcelWriter(String excelFile) {
		setExcelFile(excelFile);
	}
	
	public String getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(String excelFile) {
		this.excelFile = excelFile;
	}

	@Override
	public void write(Map<String, Map<String, Properties>> data) {
		FileOutputStream myOutput = null;
		try {
			myOutput = new FileOutputStream(getExcelFile());
		} catch (FileNotFoundException e) {
			System.err.println("Unable to place file in unexisting directory : " + getExcelFile());
			return;
		}
		System.out.println("Used encoding --> " + System.getProperty("file.encoding") + ". Start processing : " + getExcelFile());
 
		final Workbook myWorkBook = new HSSFWorkbook();
		generateFileContents(data, myWorkBook);
		try {
			myWorkBook.write(myOutput);
		} catch (IOException e1) {
			System.err.println("Cannot write data to file. Reason: " + e1.getMessage());
		}
		
		try {
			myOutput.flush();
			myOutput.close();
		} catch (IOException e) {
			System.err.println("Unable to close file input stream. Reason : " + e.getMessage());
		}
		System.out.println("End processing : " + getExcelFile());		
	}

	private void generateFileContents(final Map<String, Map<String, Properties>> propertiesFiles, final Workbook myWorkBook) {
		final Set<String> sheetNames = propertiesFiles.keySet();
		final Iterator<String> sheetIter = sheetNames.iterator();
		
		final CellStyle titleStyle = getTitleStyle(myWorkBook);
		final CellStyle missingValueStyle = getMissingValueStyle(myWorkBook);
		final CellStyle oddEvenRollStyle = oddEvenRollStyle(myWorkBook);
		
		while (sheetIter.hasNext()) {
			final String currSheetName = sheetIter.next();
			final Sheet sheet = myWorkBook.createSheet(currSheetName);
			final Map<String, Integer> sheetTitles = getSheetTitles(propertiesFiles, currSheetName);

			setSheetTitles(sheet, sheetTitles, titleStyle);
			final Set<String> allKeys = fillKeyValues(propertiesFiles.get(currSheetName), sheet, oddEvenRollStyle);
			
			fillDataForLanguages(propertiesFiles.get(currSheetName), allKeys, sheet, sheetTitles, missingValueStyle, oddEvenRollStyle);
		}
	}
	
	private CellStyle oddEvenRollStyle(final Workbook workbook) {
		final CellStyle grayStyle = workbook.createCellStyle();
		grayStyle.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
		grayStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		return grayStyle;
	}
	
	private CellStyle getMissingValueStyle(final Workbook workbook) {
		final CellStyle missingValueStyle = workbook.createCellStyle();
		missingValueStyle.setBorderBottom(CellStyle.BORDER_THICK);
		missingValueStyle.setBorderTop(CellStyle.BORDER_THICK);
		missingValueStyle.setBorderLeft(CellStyle.BORDER_THICK);
		missingValueStyle.setBorderRight(CellStyle.BORDER_THICK);
		missingValueStyle.setBottomBorderColor(HSSFColor.RED.index);
		missingValueStyle.setTopBorderColor(HSSFColor.RED.index);
		missingValueStyle.setLeftBorderColor(HSSFColor.RED.index);
		missingValueStyle.setRightBorderColor(HSSFColor.RED.index);
		return missingValueStyle;
	}

	private CellStyle getTitleStyle(final Workbook workbook) {
		final CellStyle titleStyle = workbook.createCellStyle();
		titleStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		titleStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		titleStyle.setBorderBottom((short) 2);
		titleStyle.setBorderTop((short) 1);
		titleStyle.setBorderLeft((short) 1);
		titleStyle.setBorderRight((short) 1);
		return titleStyle;
	}

	private Map<String, Integer> getSheetTitles(final Map<String, Map<String, Properties>> propertiesFiles, final String currSheetName) {
		final Set<String> keySet = new TreeSet<String>(propertiesFiles.get(currSheetName).keySet());
		final Map<String, Integer> titles = new HashMap<String, Integer>();
		int counter = 1;
		
		titles.put("keys", 0);
		
		for (String key : keySet) {
			titles.put(getValueForKey(key), counter++);
		}
		
		return titles;
	}
	
	private String getValueForKey(String key) {
		if (key.equals(".")) {
			return "default";
		}
		return key;
	}

	private void fillDataForLanguages(Map<String, Properties> propertiesFiles, final Set<String> allKeys, final Sheet sheet,
									  Map<String, Integer> sheetTitles, CellStyle missingValueStyle, CellStyle oddEvenRollStyle) {
		for (String lang : propertiesFiles.keySet()) {
			int rowCount = 1;
			for (String key : allKeys) {
				final String property = getNotNullValue(propertiesFiles.get(lang).getProperty(key));
				final Row currRow = sheet.getRow(rowCount++);
				final Cell currCell = currRow.createCell(sheetTitles.get(getValueForKey(lang)));
				if (property.equals("")) {
					currCell.setCellStyle(missingValueStyle);
				} else if (rowCount % 2 == 1) {
					currCell.setCellStyle(oddEvenRollStyle);
				}
				currCell.setCellValue(property);
			}
			rowCount = 1;
		}
	}

	private String getNotNullValue(String lang) {
		return null == lang ? "" : lang;
	}

	private Set<String> fillKeyValues(final Map<String, Properties> langProp, final Sheet sheet, CellStyle oddEvenRollStyle) {
		final Set<String> allKeys = getAllKeys(langProp);
		int rowCount = 1;
		for (String key : allKeys) {
			final Row currRow = sheet.createRow(rowCount++);
			final Cell currCell = currRow.createCell(0);
			currCell.setCellValue(key);
			if (rowCount % 2 == 1) {
				currCell.setCellStyle(oddEvenRollStyle);
			}
		}
		return allKeys;
	}

	private void setSheetTitles(final Sheet sheet, Map<String, Integer> titles, CellStyle titleStyle) {
		sheet.createFreezePane(0, 1);
		final Row row = sheet.createRow(0);
		for (String title : titles.keySet()) {
			sheet.setColumnWidth(titles.get(title), CELL_WIDTH);
			
			final Cell cell = row.createCell(titles.get(title));
			cell.setCellValue(title.toUpperCase());
			cell.setCellStyle(titleStyle);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Set<String> getAllKeys(Map<String, Properties> langProp) {
		final Set<String> keys = new TreeSet<String>();
		for (Object lang : langProp.keySet()) {
			final Set<Object> propertiesKeys = langProp.get(lang).keySet();
			final List<Object> propObjList = new ArrayList<Object>(propertiesKeys);
			final List<String> propStrList = (List<String>)(List<?>) propObjList;
			keys.addAll(propStrList);
		}
		return keys;
	}
}