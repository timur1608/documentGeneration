package pdfservice.docapi.common.api.service;

import org.springframework.stereotype.Service;
import pdfservice.docapi.common.api.configuration.S3Properties;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
public class S3Service {
    private S3Presigner s3Presigner;
    private S3Properties props;
    public S3Service(S3Presigner s3Presigner, S3Properties props){
        this.s3Presigner = s3Presigner;
        this.props = props;
    }
    public String getSignedUrl(String key) {
        return s3Presigner.presignGetObject(GetObjectPresignRequest
                .builder()
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(GetObjectRequest
                        .builder()
                        .bucket(props.bucket())
                        .key(key)
                        .build())
                .build()).url().toString();
    }
}
