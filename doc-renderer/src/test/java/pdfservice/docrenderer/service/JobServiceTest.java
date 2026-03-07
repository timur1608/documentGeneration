package pdfservice.docrenderer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import pdfservice.docapi.common.api.dto.GetJobResponseDto;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private JobService jobService;

    @Test
    void getJobPayload_returnsResponse() throws Exception {
        UUID jobId = UUID.randomUUID();
        GetJobResponseDto response = new GetJobResponseDto(new ObjectMapper().readTree("{\"a\":1}"));

        when(restTemplate.getForObject(
                eq("http://localhost:8080/jobs/{jobId}"),
                eq(GetJobResponseDto.class),
                eq(jobId)
        )).thenReturn(response);

        GetJobResponseDto result = jobService.getJobPayload(jobId);

        assertEquals(response, result);
    }
}
