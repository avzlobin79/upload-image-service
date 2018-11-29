package com.trlogic.demo.service;
import java.io.File;
import java.io.IOException;

public interface IImageProcessing {

	File createPreviewImage(File file) throws IOException;
	
}
