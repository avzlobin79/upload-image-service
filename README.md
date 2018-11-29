
## upload-image-sevice
https://upload-image-service.herokuapp.com/

HTML page(/index.html)

> There are three ways to upload a file to the server.
- POST multipart/form-data
- POST json/application (payload encoded in Base64)
- Upload file through URLS, which are separated by commas.

> We can get also preview for uploaded files.
CheckBox "isPreview" allows to do it.
Each file after uploading to the server, we can download the link.
It is also possible to stop the service.
> NOTICE:Shutdown service is disabled on Heroku!

### API
  ##### Endpoints 
  ****************************************
 ```sh
  RESPONSE
  It is the same for all paths.
  [{"fileName":"test.txt","fileDownloadUri":"http://localhost:8080/downloadFile/test.txt","filePreviewDownloadUri":null,"fileType":"txt","size":4}]
  ```
  ```sh
    REQUEST
    POST /upload/formdata/{isPreview}
    POST /upload/json/{isPreview} 
     REQUEST Payload is JSON :[{"originalFileName":"test.txt","contentType":"text/plain","size":4,"payLoad":"dGVzdA=="}
   POST /upload/urls/{isPreview}
     Payload is String : https://bipbap.ru/wp-content/uploads/2017/04/11-1.jpg,https://bipbap.ru/wp-content/uploads/2017/04/11-2.jpg 
   POST /actuator/shutdown
   Stop service.
```
### Setup
##### file: main\resources\application.properties

```sh
  
## MULTIPART (MultipartProperties)
# Enable multipart uploads
spring.servlet.multipart.enabled=true
# Threshold after which files are written to disk.
spring.servlet.multipart.file-size-threshold=2KB
# Max file size.
spring.servlet.multipart.max-file-size=200MB
# Max Request Size
spring.servlet.multipart.max-request-size=215MB
## File Storage Properties
# All files uploaded through the REST API will be stored in this directory
file.upload-dir=./uploaded-image
# All preview files uploaded will be stored in this directory
file.upload-dir-preview=./uploaded-image-preview
# Size preview file
file.width-preview=100
file.height-preview=100
## Shotdown (actuator)
# No auth  protected 
management.endpoints.web.exposure.include=*
management.endpoint.shutdown.enabled=true

```
### Run application


/opt/jdk1.8.0_171/bin/java -jar -Dserver.port=8080 upload-image-service-0.0.1-SNAPSHOT.jar
