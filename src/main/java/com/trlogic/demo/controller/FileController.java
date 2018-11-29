package com.trlogic.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.trlogic.demo.request.FileBase64;
import com.trlogic.demo.response.UploadFileResponse;
import com.trlogic.demo.service.FileStorageServiceImpl;
import com.trlogic.demo.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

@RestController
public class FileController {

	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	@Autowired
	private FileStorageServiceImpl fileStorageService;

	// endpoints

	@PostMapping("/upload/formdata/{isPreview}")
	public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,
			@PathVariable boolean isPreview) {
		return Arrays.asList(files).stream().map(file -> uploadFile(file, isPreview)).collect(Collectors.toList());
	}

	@PostMapping(path = "/upload/json/{isPreview}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public List<UploadFileResponse> uploadMultipleFiles(@RequestBody FileBase64[] files,
			@PathVariable boolean isPreview) {

		return Arrays.asList(files).stream().map(file -> uploadFile(file, isPreview)).collect(Collectors.toList());
	}

	@PostMapping(path = "/upload/url/{isPreview}")
	public List<UploadFileResponse> uploadRemoteFiles(@RequestBody String urls, @PathVariable boolean isPreview) {

		String[] files = urls.split(",");

		return Arrays.asList(files).stream().map(file -> uploadFileByUrl(file, isPreview)).collect(Collectors.toList());
	}

	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		// Load file as Resource
		Resource resource = fileStorageService.loadFileAsResource(fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@GetMapping("/previewFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadPreviewFile(@PathVariable String fileName, HttpServletRequest request) {
		// Load file as Resource
		Resource resource = fileStorageService.loadPreviewFileAsResource(fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	private UploadFileResponse uploadFile(MultipartFile file, Boolean isPreview) {

		File[] filesStorage = fileStorageService.save(file, isPreview);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
				.path(filesStorage[0].getName()).toUriString();
		logger.info("upload file (FormData) ->{}", file.getOriginalFilename());

		UploadFileResponse response = new UploadFileResponse(filesStorage[0].getName(), fileDownloadUri,
				FileUtils.getExtFile(filesStorage[0].getName()), filesStorage[0].length());

		if (isPreview) {

			String filePreviewDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/previewFile/")
					.path(filesStorage[1].getName()).toUriString();

			logger.info("create preview file (url) ->{}" + "->{}", filesStorage[1].getName());

			response.setFilePreviewDownloadUri(filePreviewDownloadUri);
		}

		return response;

	}

	private UploadFileResponse uploadFile(FileBase64 file, boolean isPreview) {

		File[] filesStorage = fileStorageService.save(file, isPreview);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
				.path(filesStorage[0].getName()).toUriString();
		logger.info("upload file (json,base64) ->{}" + "->{}", file.getOriginalFileName());

		UploadFileResponse response = new UploadFileResponse(filesStorage[0].getName(), fileDownloadUri,
				FileUtils.getExtFile(filesStorage[0].getName()), filesStorage[0].length());

		if (isPreview) {

			String filePreviewDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/previewFile/")
					.path(filesStorage[1].getName()).toUriString();

			logger.info("create preview file (url) ->{}" + "->{}", filesStorage[1].getName());

			response.setFilePreviewDownloadUri(filePreviewDownloadUri);
		}

		return response;
	}

	private UploadFileResponse uploadFileByUrl(String url, boolean isPreview) {

		File[] filesStorage = fileStorageService.saveByUrl(url, isPreview);

		String fileName = filesStorage[0].getName();

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
				.path(fileName).toUriString();
		logger.info("upload file (url) ->{}" + "->{}", fileName);

		UploadFileResponse response = new UploadFileResponse(fileName, fileDownloadUri, FileUtils.getExtFile(fileName),
				filesStorage[0].length());

		if (isPreview) {

			String filePreviewDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/previewFile/")
					.path(filesStorage[1].getName()).toUriString();

			logger.info("create preview file (url) ->{}" + "->{}", filesStorage[1].getName());

			response.setFilePreviewDownloadUri(filePreviewDownloadUri);

		}

		return response;
	}

}
