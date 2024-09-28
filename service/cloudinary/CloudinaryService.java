package com.grad.akemha.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService { //for converting the image into URL
    //In CloudinaryService, I make the dynamic type for the folder, so I can put the image into folder that I want.
    @Resource
    private Cloudinary cloudinary;

    // FOR SAMI TO USE IT IN CONSULTATIONS
    public String uploadFile(MultipartFile file, String folderName, String userID) {
        try {
            String imageName = System.currentTimeMillis() + "-" + userID;
            HashMap<Object, Object> options = new HashMap<>();
            options.put("folder", folderName);

            //to change the name
            options.put("public_id", imageName);
            Map uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
            String publicId = (String) uploadedFile.get("public_id");
            System.out.println("public Id is " + publicId);
            return cloudinary.url().secure(true).generate(publicId);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String destroyFile(String publicId) {
        try {
            //        Deleting an image with the public ID of sample:
            Map uploadedFile = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


    // FOR SALIMO TO USE IT IN POSTS
    public Map<String, String> uploadOneFile(MultipartFile file, String folderName, String userID) {
        try {
            String imageName = System.currentTimeMillis() + "-" + userID;
            HashMap<Object, Object> options = new HashMap<>();

            options.put("folder", folderName);
            //to change the name
            options.put("public_id", imageName);

            Map uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
            String publicId = (String) uploadedFile.get("public_id");

            Map<String, String> returnedMap = new HashMap<>();
            returnedMap.put("public_id", publicId);
            returnedMap.put("image_url", cloudinary.url().secure(true).generate(publicId));

            return returnedMap;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void destroyOneFile(String publicId) {
        try {
            //        Deleting an image with the public ID of sample:
            Map uploadedFile = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public Map<String, String> uploadRawFile(File file, String folder, String name) throws IOException {
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "resource_type", "raw",
                "public_id", folder + "/" + name
        );
        Map uploadedFile = cloudinary.uploader().upload(file, uploadParams);
        Map<String, String> returnedMap = new HashMap<>();
        returnedMap.put("public_id", (String) uploadedFile.get("public_id"));

        // Generate the URL for the raw file
        String url = cloudinary.url()
                .resourceType("raw")
                .secure(true)
                .generate((String) uploadedFile.get("public_id"));
        returnedMap.put("url", url);
        return returnedMap;
    }

}
