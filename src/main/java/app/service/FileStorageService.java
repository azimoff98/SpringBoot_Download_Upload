package app.service;

import app.exception.FileStorageException;
import app.exception.MyFileNotFoundException;
import app.model.Image;
import app.property.FileStorageProperies;
import app.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {


    @Autowired
    private ImageRepository repository;


    public Image storeFile(MultipartFile file,Integer id){
        //Normalize filename
        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        try{
            //Checks if filename contains invalid characters
            if(filename.contains("..")){
                throw new FileStorageException("Sorry! Filename contains invalid characters " + filename);
            }

            Image image = new Image(filename, file.getContentType(), file.getBytes());
            image.setId(id);

            return repository.save(image);
        }catch (IOException ex){
            throw new FileStorageException("Could not store file " + filename + ". Please try again ");
        }


    }


    public Image getFile(Integer fileId){
        return repository.findById(fileId).orElseThrow(
                () -> new MyFileNotFoundException("File not found with id: " + fileId)
        );
    }


//    public FileStorageService(FileStorageProperies fileStorageProperies){
//        this.fileStorageLocation = Paths.get(fileStorageProperies.getUploadDir())
//                .toAbsolutePath().normalize();
//
//        try{
//            Files.createDirectories(this.fileStorageLocation);
//        }catch (Exception ex){
//            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
//        }
//    }
//
//    public String storeFile(MultipartFile file){
//        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//
//        try{
//            if(fileName.contains("..")){
//                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
//            }
//
//
//            Path targetLocation = this.fileStorageLocation.resolve(fileName);
//            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//
//            return fileName;
//        }catch (IOException ex){
//            throw new FileStorageException("Could not store file " + fileName + " Please try again " + ex);
//        }
//    }
//
//    public Resource loadFileAsResource(String fileName){
//        try{
//            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
//            Resource resource = new UrlResource(filePath.toUri());
//            if(resource.exists()){
//                return resource;
//            }else{
//                throw new MyFileNotFoundException("File not found: " + fileName);
//            }
//        }catch (MalformedURLException ex){
//            throw new MyFileNotFoundException("File not found: " + fileName, ex);
//        }
//    }



}


