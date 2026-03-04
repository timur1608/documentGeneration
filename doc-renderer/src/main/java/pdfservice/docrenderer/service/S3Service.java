package pdfservice.docrenderer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pdfservice.docrenderer.configuration.S3Properties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
public class S3Service {
    private S3Client s3Client;
    private S3Properties props;

    @Autowired
    public S3Service(S3Client s3Client, S3Properties props){
        this.s3Client = s3Client;
        this.props = props;
    }

    public String uploadDocToObjectStorage(byte[] bytes, String key) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(props.bucket())
                        .key(key)
                        .build(),
                RequestBody.fromBytes(bytes)
        );
        return key;
    }
}
