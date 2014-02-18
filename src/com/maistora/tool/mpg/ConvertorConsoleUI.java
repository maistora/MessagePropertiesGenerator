package com.maistora.tool.mpg;

public class ConvertorConsoleUI {

	public static void main(String[] args) {
		new ConvertorConsoleUI().run(args);
	}

	private void run(String[] args) {

		if (args.length > 3 || args.length < 2) {
			System.err.println("Invalid arguments.");
			return;
		}

		final ConvertorConfig config;
		if (args.length == 3) {
			config = getConfigWithInputDirection(args[0], args[1], args[2]);
		} else {
			config = getConfigWithoutDirection(args[0], args[1]);
		}

		if (null == config) {
			return;
		}

		new ConvertorFacade().convert(config);
	}

	private static ConvertorConfig getConfigWithoutDirection(String dir, String excel) {
		return new ConvertorConfig(dir, excel);
	}

	private ConvertorConfig getConfigWithInputDirection(String command, String dir, String excel) {
		final Direction direction = Direction.findDirection(command);

		if (null == direction) {
			System.err.println("Wrong command [" + command + "].");
			System.out.println("Available commands: ");
			for (Direction direct : Direction.values()) {
				System.out.println("> " + direct.getCommand());
			}
			return null;
		}

		return new ConvertorConfig(dir, excel, direction);
	}
}
