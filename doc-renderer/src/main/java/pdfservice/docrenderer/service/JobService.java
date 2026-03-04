package pdfservice.docrenderer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pdfservice.docapi.common.api.dto.GetJobResponseDto;

import java.util.UUID;

@Service
public class JobService {
    private final RestTemplate restTemplate;
    public JobService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }
    public GetJobResponseDto getJobPayload(UUID jobId) {
        return restTemplate.getForObject("http://localhost:8080/jobs/{jobId}",
                GetJobResponseDto.class,
                jobId);
    }
}
