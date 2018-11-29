package com.trlogic.demo.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
	private String uploadDir;
	private String uploadDirPreview;
	private int widthPreview;
	private int heightPreview;

	public String getUploadDirPreview() {
		return uploadDirPreview;
	}

	public void setUploadDirPreview(String uploadDirPreview) {
		this.uploadDirPreview = uploadDirPreview;
	}

	public String getUploadDir() {
		return uploadDir;
	}

	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}

	public int getWidthPreview() {
		return widthPreview;
	}

	public void setWidthPreview(int widthPreview) {
		this.widthPreview = widthPreview;
	}

	public int getHeightPreview() {
		return heightPreview;
	}

	public void setHeightPreview(int heightPreview) {
		this.heightPreview = heightPreview;
	}

}