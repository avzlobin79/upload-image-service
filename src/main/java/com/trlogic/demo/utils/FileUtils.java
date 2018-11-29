package com.trlogic.demo.utils;

public class FileUtils {

	public static String getExtFile(String fileName) {

		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}

		return extension;
	}

}
