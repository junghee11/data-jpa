package com.develop.datajpa.service.image;

import com.develop.core.exception.ClientException;
import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;
    
    public String upload(String category, MultipartFile file) {
        if(file.isEmpty()) {
            throw new ClientException("파일이 확인되지 않습니다");
        }
        
        try (InputStream is = file.getInputStream()) {
            String fileName = buildFileName(category, file.getOriginalFilename());
            S3Resource upload = s3Template.upload(bucketName, fileName, is);

            return upload.getFilename();
        } catch (IOException | S3Exception e) {
            throw new ClientException(String.format("파일 업로드에 실패 : %s", e.getMessage()));
        }
    }

    private static final String FILE_EXTENSION_SEPARATOR = ".";
    private static final String TIME_SEPARATOR = "_";

    public static String buildFileName(String category, String originalFileName) {
        int fileExtensionIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        String fileExtension = originalFileName.substring(fileExtensionIndex);
        String fileName = originalFileName.substring(0, fileExtensionIndex);
        String now = String.valueOf(System.currentTimeMillis());

        return category + fileName + TIME_SEPARATOR + now + fileExtension;
    }

}
