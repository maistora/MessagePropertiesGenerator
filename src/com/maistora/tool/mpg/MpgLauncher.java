package com.maistora.tool.mpg;

public class MpgLauncher {
	
	public static void main(String[] args) {
        JarClassLoader jcl = new JarClassLoader();
        try {
            jcl.invokeMain("com.maistora.tool.mpg.ConvertorConsoleUI", args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
