package com.revature.deliveryservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name:revcart-statics}")
    private String bucketName;

    @Value("${aws.s3.region:eu-north-1}")
    private String region;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Upload a file to S3 and return the public URL
     * 
     * @param file   The file to upload
     * @param folder The folder path in S3 (e.g., "delivery/aadhar",
     *               "delivery/licenses")
     * @return The public URL of the uploaded file
     */
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            String key = folder + "/" + uniqueFilename;

            // Upload to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // Return the public URL
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);

        } catch (S3Exception e) {
            throw new RuntimeException("Failed to upload file to S3: " + e.awsErrorDetails().errorMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + e.getMessage(), e);
        }
    }

    /**
     * Validate file size and type
     * 
     * @param file        The file to validate
     * @param maxSizeInMB Maximum file size in MB
     */
    public void validateFile(MultipartFile file, int maxSizeInMB) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        // Check file size
        long maxSizeInBytes = maxSizeInMB * 1024L * 1024L;
        if (file.getSize() > maxSizeInBytes) {
            throw new IllegalArgumentException(
                    String.format("File size exceeds maximum allowed size of %d MB", maxSizeInMB));
        }

        // Check file type (allow common image types)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
    }
}
