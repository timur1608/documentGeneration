package pdfservice.docapi.common.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pdfservice.docapi.common.api.dto.CreateJobRequestDto;
import pdfservice.docapi.common.api.repository.JobRepository;

import java.util.UUID;

@Service
public class JobService {
    private final JobRepository jobRepository;
    @Autowired
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }
    @Transactional
    public UUID createJob(CreateJobRequestDto request) {
        UUID jobId = UUID.randomUUID();
        jobId = jobRepository.createJobOrFindExisting(
                jobId,
                request.tenantId(),
                request.templateVersionId(),
                request.requestId(),
                request.payload().toString()
        );
        return jobId;
    }
}
