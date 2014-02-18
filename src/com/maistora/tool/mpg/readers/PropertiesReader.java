package com.maistora.tool.mpg.readers;

import java.util.Map;
import java.util.Properties;

public interface PropertiesReader {
	
	Map<String, Map<String, Properties>> read();
}
