package com.trlogic.demo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.JSONException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.junit.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UploadImageServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:test-application.properties")
public class FileController_IntegrationTest {

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();

	HttpHeaders headers = new HttpHeaders();

	private ResponseEntity<String> uploadMultipleFile() throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("files", getTestFile("test-file1.JPG"));
		body.add("files", getTestFile("test-file2.JPG"));

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		String serverUrl = createURLWithPort("/upload/formdata/false");
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
		System.out.println("Response code: " + response.getStatusCode());
		return response;
	}

	public static Resource getTestFile(String fileName) throws IOException {

		Path testFile = Paths.get("temp-files-test/" + fileName);

		if (!Files.exists(testFile)) {
			Files.createFile(testFile);
			System.out.println("Creating Test File: " + testFile);
			Files.write(testFile, "Hello World !!, This is a test file.".getBytes());
		}
		return new FileSystemResource(testFile.toFile());
	}

	@Test
	public void testRetrieveUploadFileFormDataResponse() throws JSONException, IOException {

		ResponseEntity<String> res = uploadMultipleFile();

		String expected = "[{\"fileName\":\"test-file1.JPG\",\"fileDownloadUri\":\"http://localhost:8090/downloadFile/test-file1.JPG\",\"filePreviewDownloadUri\":null,\"fileType\":\"JPG\",\"size\":36},{\"fileName\":\"test-file2.JPG\",\"fileDownloadUri\":\"http://localhost:8090/downloadFile/test-file2.JPG\",\"filePreviewDownloadUri\":null,\"fileType\":\"JPG\",\"size\":36}]";

		JSONAssert.assertEquals(expected, res.getBody(), false);

		Path file1 = Paths.get("uploaded-image/test-file1.JPG");
		Path file2 = Paths.get("uploaded-image/test-file2.JPG");

		Assert.assertTrue(Files.exists(file1));
		Assert.assertTrue(Files.exists(file2));

		Files.delete(file1);
		Files.delete(file2);

	}

	public void deleteFile(Path file) throws IOException {

		if (Files.exists(file))
			Files.delete(file);
	}

	@After
	public void clearDirAfterTest() {
		try {
			Path testDir = Paths.get("temp-files-test");

			Files.list(testDir).forEach(f -> {

				try {
					deleteFile(f);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}

}
