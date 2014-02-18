package com.maistora.tool.mpg;

public class ConvertorConfig {
	
	private String propertiesPath;
	private String excelPath;
	private Direction direction = Direction.EXCEL_TO_PROPERTIES;
	
	public ConvertorConfig(String propPath, String excelPath) {
		this.propertiesPath = propPath;
		this.excelPath = excelPath;
	}
	
	public ConvertorConfig(String propPath, String excelPath, Direction direction) {
		this(propPath, excelPath);
		this.direction = direction;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public String getExcelPath() {
		return excelPath;
	}
	
	public String getPropertiesPath() {
		return propertiesPath;
	}

	public void setPropertiesPath(String propertiesPath) {
		this.propertiesPath = propertiesPath;
	}

	public void setExcelPath(String excelPath) {
		this.excelPath = excelPath;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
}