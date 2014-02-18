package com.maistora.tool.mpg;

import com.maistora.tool.mpg.readers.ExcelToPropertiesReader;
import com.maistora.tool.mpg.readers.PropertiesToExcelReader;
import com.maistora.tool.mpg.writers.ExcelToPropertiesWriter;
import com.maistora.tool.mpg.writers.PropertiesToExcelWriter;

public class ConvertorFacade {
	
	public void convert(ConvertorConfig config) {
		if (config.getDirection() == Direction.EXCEL_TO_PROPERTIES) {
			final ExcelToPropertiesReader reader = new ExcelToPropertiesReader(config.getExcelPath());
			final ExcelToPropertiesWriter writer = new ExcelToPropertiesWriter(config.getPropertiesPath());
			writer.write(reader.read());
		} else {
			final PropertiesToExcelReader reader = new PropertiesToExcelReader(config.getPropertiesPath());
			final PropertiesToExcelWriter writer = new PropertiesToExcelWriter(config.getExcelPath());
			writer.write(reader.read());
		}
	}
	
}