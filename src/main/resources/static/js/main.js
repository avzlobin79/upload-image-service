'use strict';

var multipleUploadForm = document.querySelector('#multipleUploadForm');
var multipleFileUploadInput = document.querySelector('#multipleFileUploadInput');
var multipleFileUploadError = document.querySelector('#multipleFileUploadError');
var multipleFileUploadSuccess = document.querySelector('#multipleFileUploadSuccess');

function uploadBase64Files(files) {
    
	var json=[];
	
	var promises=[];
	
	for(var index = 0; index < files.length; index++) {
		promises[index]=getJsonFromFile(files[index]);
    }
	Promise.all(promises)
	  .then(json => {
		  
		    var xhr = new XMLHttpRequest();
		    xhr.open("POST", "/upload/json/"+isPreviewCheckBox.checked);
		    xhr.setRequestHeader('Content-type','application/json; charset=utf-8');
		    
		    xhr.onload = function() {
		        console.log(xhr.responseText);
		        var response = JSON.parse(xhr.responseText);
		        if(xhr.status == 200) {
		            multipleFileUploadError.style.display = "none";
		            var content = "<p>All Files Uploaded Successfully</p>";
		            for(var i = 0; i < response.length; i++) {
		                
		            	if (response[i].filePreviewDownloadUri!=null){
		            		content += "<p><a href='" + response[i].fileDownloadUri + "' target='_blank'><img src='" + response[i].filePreviewDownloadUri+"'></a></p>";
		            	}else{
		            		content += "<p>DownloadUrl : <a href='" + response[i].fileDownloadUri + "' target='_blank'>" + response[i].fileDownloadUri + "</a></p>";
		                }
		            }
		            multipleFileUploadSuccess.innerHTML = content;
		            multipleFileUploadSuccess.style.display = "block";
		        } else {
		            multipleFileUploadSuccess.style.display = "none";
		            multipleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
		        }
		    }

		    xhr.send(JSON.stringify(json));
		  
	  });
}


function uploadFilesByUrl(files) {
    

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/upload/url/"+isPreviewCheckBox.checked);
    xhr.onload = function() {
        console.log(xhr.responseText);
        var response = JSON.parse(xhr.responseText);
        if(xhr.status == 200) {
            multipleFileUploadError.style.display = "none";
            var content = "<p>All Files Uploaded Successfully</p>";
            for(var i = 0; i < response.length; i++) {
                
            	if (response[i].filePreviewDownloadUri!=null){
            		content += "<p><a href='" + response[i].fileDownloadUri + "' target='_blank'><img src='" + response[i].filePreviewDownloadUri+"'></a></p>";
            	}else{
            		content += "<p>DownloadUrl : <a href='" + response[i].fileDownloadUri + "' target='_blank'>" + response[i].fileDownloadUri + "</a></p>";
                }
            }
            multipleFileUploadSuccess.innerHTML = content;
            multipleFileUploadSuccess.style.display = "block";
        } else {
            multipleFileUploadSuccess.style.display = "none";
            multipleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
        }
    }

    xhr.send(files);
}

function uploadMultipleFiles(files) {
    var formData = new FormData();
    for(var index = 0; index < files.length; index++) {
        formData.append("files", files[index]);
    }

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/upload/formdata/"+isPreviewCheckBox.checked);

    xhr.onload = function() {
        console.log(xhr.responseText);
        var response = JSON.parse(xhr.responseText);
        if(xhr.status == 200) {
            multipleFileUploadError.style.display = "none";
            var content = "<p>All Files Uploaded Successfully</p>";
            for(var i = 0; i < response.length; i++) {
            if (response[i].filePreviewDownloadUri!=null){
        		content += "<p><a href='" + response[i].fileDownloadUri + "' target='_blank'><img src='" + response[i].filePreviewDownloadUri+"'></a></p>";
        	}else{
        		content += "<p>DownloadUrl : <a href='" + response[i].fileDownloadUri + "' target='_blank'>" + response[i].fileDownloadUri + "</a></p>";
             }
            }
            multipleFileUploadSuccess.innerHTML = content;
            multipleFileUploadSuccess.style.display = "block";
        } else {
            multipleFileUploadSuccess.style.display = "none";
            multipleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
        }
    }

    xhr.send(formData);
}

multipleUploadForm1.addEventListener('submit', function(event){
    var files = multipleFileUploadInput1.files;
    if(files.length === 0) {
        multipleFileUploadError.innerHTML = "Please select at least one file";
        multipleFileUploadError.style.display = "block";
    }
    uploadMultipleFiles(files);
    
    event.preventDefault();
}, true);

multipleUploadForm2.addEventListener('submit', function(event){
    var files = multipleFileUploadInput2.files;
    
    
    if(files.length === 0) {
        multipleFileUploadError.innerHTML = "Please select at least one file";
        multipleFileUploadError.style.display = "block";
    }
  
    uploadBase64Files(files);
    event.preventDefault();
}, true);



uploadByUrls.addEventListener('click', function(event){
    
	
	
	var files=multipleFileUploadInput3.value;
    
	var countFiles=files.split(',').length;
	
	
    if(countFiles.length === 0) {
      multipleFileUploadError.innerHTML = "Please input at least one file";
      multipleFileUploadError.style.display = "block";
    }

  
	uploadFilesByUrl(files);
    event.preventDefault();
}, true);


shutdown.addEventListener('click', function(event){
    
	
	var xhr = new XMLHttpRequest();
    xhr.open("POST", "/actuator/shutdown");

    xhr.onload = function() {
        console.log(xhr.responseText);
        var response = JSON.parse(xhr.responseText);
        if(xhr.status == 200) {
           
            var content = "<p>Shutdown Successfully</p>";
            
            multipleFileUploadSuccess.innerHTML = content;
            multipleFileUploadSuccess.style.display = "block";
        } else {
            multipleFileUploadSuccess.style.display = "none";
            multipleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
        }
    }

    xhr.send();
	
	
}, true);


function getJsonFromFile(file) {

	  return new Promise(function(resolve, reject) {
		  
		  var reader = new FileReader();
		   reader.readAsDataURL(file);
		   reader.onload = function () {
		   var fileBase64=new Object();
		   fileBase64.originalFileName=file.name;
		   fileBase64.contentType=file.type;
		   fileBase64.size=file.size;
		   //cutting
		   fileBase64.payLoad=reader.result.split("base64,")[1];
		   resolve(fileBase64);
		  
		   };
		   reader.onerror = function (error) {
		       console.log('Error: ', error);
			   reject(error);
		   };
  
	})

}

