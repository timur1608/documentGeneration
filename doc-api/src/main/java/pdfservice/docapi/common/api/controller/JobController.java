package pdfservice.docapi.common.api.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pdfservice.docapi.common.api.dto.CreateJobRequestDto;
import pdfservice.docapi.common.api.dto.CreateJobResponseDto;
import pdfservice.docapi.common.api.dto.GetJobResponseDto;
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
    ResponseEntity<CreateJobResponseDto> createJob(@Valid @RequestBody CreateJobRequestDto request) {
        UUID jobId = jobService.createJob(request);
        return ResponseEntity.ok(new CreateJobResponseDto(jobId, "QUEUED"));
    }

    @GetMapping("/{jobId}")
    ResponseEntity<GetJobResponseDto> findJob(@PathVariable("jobId") UUID jobId) {
        return ResponseEntity.ok(new GetJobResponseDto(jobService.getJobPayload(jobId)));
    }
}
