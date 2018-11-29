package com.trlogic.demo.service;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.trlogic.demo.property.FileStorageProperties;
import com.trlogic.demo.utils.FileUtils;

@Service
public class ImageProcessingServiceImpl implements IImageProcessing {

	@Autowired
	FileStorageProperties fileStorageProperties;

	@Override
	public File createPreviewImage(File file) throws IOException {

		// Size preview file
		int widthPrev = fileStorageProperties.getWidthPreview();

		int heightPrev = fileStorageProperties.getHeightPreview();

		BufferedImage img = new BufferedImage(widthPrev, heightPrev, BufferedImage.TYPE_INT_RGB);
		img.createGraphics().drawImage(

				ImageIO.read(file).getScaledInstance(widthPrev, heightPrev, Image.SCALE_SMOOTH), 0, 0, null);

		File prevFile = new File(fileStorageProperties.getUploadDirPreview() + "/prev_" + widthPrev + "_" + heightPrev
				+ "_" + file.getName());

		if (ImageIO.write(img, FileUtils.getExtFile(file.getName()), prevFile)) {

			return prevFile;

		}

		throw new IOException("Could not create preview file for" + file.getName());

	}

}
