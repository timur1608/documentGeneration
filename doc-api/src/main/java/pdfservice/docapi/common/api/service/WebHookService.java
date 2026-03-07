package pdfservice.docapi.common.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pdfservice.docapi.common.api.dto.WebHookRequest;

import java.util.UUID;

@Service
public class WebHookService {
    private RestTemplate restTemplate;

    @Autowired
    public WebHookService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public void uploadFileToUrl(String url, UUID jobId, String payload){
        restTemplate.postForEntity(url, new WebHookRequest(
            jobId, "COMPLETED", payload
        ), Void.class);
    }
}
