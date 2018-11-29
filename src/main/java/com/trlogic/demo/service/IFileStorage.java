package com.trlogic.demo.service;

import java.io.File;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.trlogic.demo.request.FileBase64;

public interface IFileStorage {

	File[] save(MultipartFile file, boolean isPreview);

	File[] save(FileBase64 file, boolean isPreview);

	File[] saveByUrl(String url, boolean isPreview);

	Resource loadFileAsResource(String fileName);

	Resource loadPreviewFileAsResource(String fileName);

}
