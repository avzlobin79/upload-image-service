package com.trlogic.demo.response;

public class UploadFileResponse {
	private String fileName;
	private String fileDownloadUri;
	private String filePreviewDownloadUri;
	private String fileType;
	private long size;

	public UploadFileResponse(String fileName, String fileDownloadUri, String fileType, long size) {
		this.fileName = fileName;
		this.fileDownloadUri = fileDownloadUri;
		this.fileType = fileType;
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileDownloadUri() {
		return fileDownloadUri;
	}

	public void setFilePreviewDownloadUri(String filePreviewDownloadUri) {
		this.filePreviewDownloadUri = filePreviewDownloadUri;
	}

	public String getFilePreviewDownloadUri() {
		return filePreviewDownloadUri;
	}

	public String getFileType() {
		return fileType;
	}

	public long getSize() {
		return size;
	}

}
