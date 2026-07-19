package com.redhat.deployforgeworker.services;

import com.redhat.deployforgeworker.config.S3Properties;
import com.redhat.deployforgeworker.exceptions.S3UploadFailedException;
import com.redhat.deployforgeworker.models.Deployment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public void uploadOutputDirectory(Deployment deployment)  {
        // Source path: temp/build-{id}/dist
        Path dir = Paths.get(
                System.getProperty("java.io.tmpdir"),
                "build-" + deployment.getDeploymentId(),
                deployment.getOutputDirectory()
        );

        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            throw new S3UploadFailedException("directory does not exist: " + dir);
        }

        try (Stream<Path> paths = Files.walk(dir)) {
            paths.filter(Files::isRegularFile)
                    .forEach(filePath -> uploadToS3(dir, deployment.getDeploymentSlug(), filePath));
        }
        catch (IOException e) {
            throw new S3UploadFailedException("couldn't access the directory: "+dir);
        }
    }

    private void uploadToS3(Path dir, String deploymentSlug, Path filePath){
        try {
            String relativePath = dir
                    .relativize(filePath)
                    .toString()
                    .replace('\\', '/');

            String s3Key = "deployments/" + deploymentSlug + "/" + relativePath;

            String contentType = resolveContentType(filePath);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.bucketName())
                    .key(s3Key)
                    .contentType(contentType)
                    .cacheControl(resolveCacheControl(filePath))
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromFile(filePath)
            );
            long size = Files.size(filePath);
            log.info("Upload contentType {} ({})", s3Key, contentType);
            log.info("Upload size {} ({})", s3Key, size);

        } catch (S3Exception | SdkClientException | IOException e) {
            log.info("Upload Failed:{}", e.getMessage());
            throw new S3UploadFailedException("Failed to upload file to s3");
        }
    }

    private String resolveContentType(Path filePath) {
        try {
            String type = Files.probeContentType(filePath);
            return type != null ? type : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }

    private String resolveCacheControl(Path filePath) {
        String cacheControl;
        String filename = filePath.getFileName().toString();

        if (filename.equals("index.html")) {
            cacheControl = "public,max-age=0,must-revalidate";
        } else {
            cacheControl = "public,max-age=31536000,immutable";
        }

        return cacheControl;

    }
}
