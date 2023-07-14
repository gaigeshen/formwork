package work.gaigeshen.formwork.basal.upload;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import java.util.Objects;

/**
 *
 * @author gaigeshen
 */
public class MinioUploader implements Uploader {

    private final MinioClient minioClient;

    private final String serverHost;

    public MinioUploader(MinioClient minioClient, String serverHost) {
        if (Objects.isNull(minioClient) || Objects.isNull(serverHost)) {
            throw new IllegalArgumentException("minioClient and serverHost cannot be null");
        }
        this.serverHost = serverHost;
        this.minioClient = minioClient;
    }

    @Override
    public UploadResult upload(Upload upload) throws UploadException {
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(upload.getBucket()).object(upload.getObject())
                .contentType(upload.getContentType()).stream(upload.getInputStream(), -1, 10485760)
                .build();
        try {
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            throw new UploadException(e.getMessage(), e);
        }
        return new UploadResult(serverHost, upload.getBucket(), upload.getObject());
    }
}
