package pdfservice.docapi.common.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import pdfservice.docapi.common.api.dto.WebHookRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WebHookServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WebHookService webHookService;

    @Test
    public void uploadFileToUrl(){
        String url = "www.example.com";
        UUID jobId = UUID.randomUUID();
        String payload = "testPayload";

        ArgumentCaptor<WebHookRequest> slot = ArgumentCaptor.forClass(WebHookRequest.class);

        webHookService.uploadFileToUrl(url, jobId, payload);

        verify(restTemplate).postForEntity(eq(url), slot.capture(), eq(Void.class));

        WebHookRequest request = slot.getValue();

        assertEquals(jobId, request.jobId());
        assertEquals("COMPLETED", request.status());
        assertEquals(payload, request.s3Url());
    }
}
