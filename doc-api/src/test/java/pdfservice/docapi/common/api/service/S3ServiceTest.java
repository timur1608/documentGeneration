package pdfservice.docapi.common.api.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pdfservice.docapi.common.api.configuration.S3Properties;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private S3Properties props;

    @InjectMocks
    private S3Service s3Service;

    @Test
    public void getSignedUrlTest() throws MalformedURLException {
        String key = "randomKey";
        String bucket = "someBucket";
        String url = "https://example.com";
        when(props.bucket()).thenReturn(bucket);
        PresignedGetObjectRequest presignedGetObjectRequest = mock(PresignedGetObjectRequest.class);

        when(presignedGetObjectRequest.url()).thenReturn(URI.create(url).toURL());

        ArgumentCaptor<GetObjectPresignRequest> slot = ArgumentCaptor.forClass(GetObjectPresignRequest.class);

        when(s3Presigner.presignGetObject(slot.capture())).thenReturn(presignedGetObjectRequest);

        String resultUrl = s3Service.getSignedUrl(key);

        GetObjectPresignRequest req = slot.getValue();

        GetObjectRequest objectRequest = req.getObjectRequest();

        assertEquals(Duration.ofMinutes(5), req.signatureDuration());
        assertEquals(key, objectRequest.key());
        assertEquals(bucket, objectRequest.bucket());
        assertEquals(url, resultUrl);
    }

}
