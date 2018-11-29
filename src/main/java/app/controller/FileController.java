package app.controller;

import app.model.Image;
import app.payload.UploadFileResponse;
import app.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file,@RequestHeader("id") Integer id){
        System.out.println(id);
        Image image = fileStorageService.storeFile(file,id);

        String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("downloadFile/").path(image.getId().toString()).toUriString();


        return new UploadFileResponse(image.getFileName(), fileDownloadUri, file.getContentType(), file.getSize());

    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,@RequestHeader("id") Integer id){
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file,id))
                .collect(Collectors.toList());
    }


   @GetMapping("/downloadFile/{fileId}")
   public ResponseEntity<Resource> downloadFile(@PathVariable Integer fileId){

        Image image = fileStorageService.getFile(fileId);

        return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(image.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                    .body(new ByteArrayResource(image.getData()));

    }


}
