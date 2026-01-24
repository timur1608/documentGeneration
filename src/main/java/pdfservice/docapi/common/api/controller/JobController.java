package pdfservice.docapi.common.api.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pdfservice.docapi.common.api.dto.CreateJobRequestDto;
import pdfservice.docapi.common.api.dto.CreateJobResponseDto;
import pdfservice.docapi.common.api.service.JobService;

import java.util.UUID;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private final JobService jobService;
    @Autowired
    public JobController(JobService jobService){
        this.jobService = jobService;
    }

    @PostMapping
    CreateJobResponseDto createJob(@Valid CreateJobRequestDto request) {
        UUID jobId = jobService.createJob(request);
        return new CreateJobResponseDto(jobId, "QUEUED");
    }
}
