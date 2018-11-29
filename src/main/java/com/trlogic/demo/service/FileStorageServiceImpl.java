package com.trlogic.demo.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

import com.trlogic.demo.exception.FileStorageException;
import com.trlogic.demo.exception.FileUriSyntaxException;
import com.trlogic.demo.exception.MyFileNotFoundException;
import com.trlogic.demo.property.FileStorageProperties;
import com.trlogic.demo.request.FileBase64;

@Service
public class FileStorageServiceImpl implements IFileStorage {

	private final Path fileStorageLocation;
	private final Path prevFileStorageLocation;

	@Autowired
	IImageProcessing imageProcessingService;

	@Autowired
	public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
		this.prevFileStorageLocation = Paths.get(fileStorageProperties.getUploadDirPreview()).toAbsolutePath()
				.normalize();
		try {

			Files.createDirectories(this.fileStorageLocation);

		} catch (Exception ex) {

			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}

		try {

			Files.createDirectories(this.prevFileStorageLocation);

		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the preview files will be stored.",
					ex);
		}

	}

	public File[] saveByUrl(String url, boolean isPreview) {

		// [0]->file, [1]->prevFile
		File[] result = new File[2];
		// Normalize file name
		String fileName = null;
		try {

			fileName = Paths.get(new URI(url).getPath()).getFileName().toString();

			InputStream is = new URL(url).openStream();

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);

			Files.copy(is, targetLocation, StandardCopyOption.REPLACE_EXISTING);

			// create preview file
			if (isPreview) {

				result[1] = imageProcessingService.createPreviewImage(targetLocation.toFile());

			}

			result[0] = targetLocation.toFile();

			return result;

		} catch (IOException ex) {

			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);

		} catch (URISyntaxException ex) {

			throw new FileUriSyntaxException("Could not get fileName from URL " + url + ". Please try again!", ex);

		}

	}

	public File[] save(FileBase64 file, boolean isPreview) {

		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFileName());
		// [0]->file, [1]->prevFile
		File[] result = new File[2];

		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}

			String base64 = file.getPayLoad();

			byte[] decoded = Base64.getDecoder().decode(base64);

			InputStream is = new ByteArrayInputStream(decoded);

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);

			Files.copy(is, targetLocation, StandardCopyOption.REPLACE_EXISTING);

			if (isPreview) {

				result[1] = imageProcessingService.createPreviewImage(targetLocation.toFile());

			}

			result[0] = targetLocation.toFile();

			return result;
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}

	}

	public File[] save(MultipartFile file, boolean isPreview) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		// [0]->file, [1]->prevFile
		File[] result = new File[2];

		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(fileName);

			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			// create preview file
			if (isPreview) {

				result[1] = imageProcessingService.createPreviewImage(targetLocation.toFile());

			}

			result[0] = targetLocation.toFile();

			return result;

		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	public Resource loadFileAsResource(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found " + fileName, ex);
		}
	}

	public Resource loadPreviewFileAsResource(String fileName) {
		try {
			Path filePath = this.prevFileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found " + fileName, ex);
		}
	}

}
