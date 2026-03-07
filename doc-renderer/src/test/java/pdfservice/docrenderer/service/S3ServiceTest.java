package pdfservice.docrenderer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pdfservice.docrenderer.configuration.S3Properties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Properties props;

    @InjectMocks
    private S3Service s3Service;

    @Test
    void uploadDocToObjectStorage_putsObjectAndReturnsKey() {
        String bucket = "bucket";
        String key = "file.pdf";
        byte[] bytes = "data".getBytes();

        when(props.bucket()).thenReturn(bucket);

        String result = s3Service.uploadDocToObjectStorage(bytes, key);

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> bodyCaptor = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client).putObject(requestCaptor.capture(), bodyCaptor.capture());

        PutObjectRequest request = requestCaptor.getValue();
        assertEquals(bucket, request.bucket());
        assertEquals(key, request.key());
        assertEquals(key, result);
    }
}
