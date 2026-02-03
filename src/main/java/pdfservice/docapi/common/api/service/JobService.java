package pdfservice.docapi.common.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pdfservice.docapi.common.api.dto.CreateJobRequestDto;
import pdfservice.docapi.common.api.repository.JobRepository;
import pdfservice.docapi.common.api.repository.OutboxRepository;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    @Autowired
    public JobService(JobRepository jobRepository, OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.jobRepository = jobRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
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
        outboxRepository.publishJob("job", jobId, "job.queued", getJobPayload(request, jobId));
        return jobId;
    }

    private String getJobPayload(CreateJobRequestDto request, UUID jobId) {
        return objectMapper.valueToTree(
                Map.ofEntries(
                        Map.entry("jobId", jobId),
                        Map.entry("tenantId", request.tenantId()),
                        Map.entry("templateVersionId", request.templateVersionId()),
                        Map.entry("requestId", request.requestId())
                )
        ).toString();
    }
}
