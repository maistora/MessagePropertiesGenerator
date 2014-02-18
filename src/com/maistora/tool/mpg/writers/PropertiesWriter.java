package com.maistora.tool.mpg.writers;

import java.util.Map;
import java.util.Properties;

public interface PropertiesWriter {
	
	void write(Map<String, Map<String, Properties>> data);
}
