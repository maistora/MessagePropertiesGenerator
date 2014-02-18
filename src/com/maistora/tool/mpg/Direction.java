package com.maistora.tool.mpg;

public enum Direction {
	EXCEL_TO_PROPERTIES("to_properties"),
	PROPERTIES_TO_EXCEL("to_excel");

	private String command;
	
	private Direction(String direction) {
		this.command = direction;
	}
	
	public static Direction findDirection(String command) {
		for (Direction dir : values()) {
			if (dir.command.equals(command)) {
				return dir;
			}
		}
		
		return null;
	}

	public String getCommand() {
		return command;
	}
}
