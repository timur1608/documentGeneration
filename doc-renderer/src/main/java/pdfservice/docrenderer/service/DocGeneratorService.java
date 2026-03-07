package pdfservice.docrenderer.service;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import pdfservice.docapi.common.api.dto.GetJobResponseDto;
import pdfservice.docapi.common.api.dto.OutboxEvent;
import pdfservice.docapi.common.api.dto.OutboxFinishedJobRecord;
import pdfservice.docapi.common.api.dto.OutboxPayload;
import pdfservice.docrenderer.repository.OutboxFinishedJobsRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.StringWriter;
import java.util.Map;
import java.util.UUID;

@Service
public class DocGeneratorService {
    private final TemplateService templateService;
    private final JobService jobService;
    private final Configuration config;
    private final ObjectMapper objectMapper;
    private final S3Service s3Service;
    private final OutboxFinishedJobsRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(DocGeneratorService.class);

    @Autowired
    public DocGeneratorService(TemplateService templateService, JobService jobService, Configuration config,
                               ObjectMapper objectMapper, S3Service s3Service, OutboxFinishedJobsRepository repository){
        this.templateService = templateService;
        this.config = config;
        this.jobService = jobService;
        this.objectMapper = objectMapper;
        this.s3Service = s3Service;
        this.repository = repository;
    }

    public void handle(OutboxEvent event) {
        OutboxPayload payload = event.payload();
        var key = generateByTemplateId(payload.templateVersionId(), payload.jobId());
        OutboxFinishedJobRecord finishedJob = new OutboxFinishedJobRecord(payload.jobId(), key);
        repository.saveEvent(finishedJob);
    }

    String generateByTemplateId(UUID templateVersionId, UUID jobId) {
        String content = templateService.getTemplateById(templateVersionId);
        GetJobResponseDto job = jobService.getJobPayload(jobId);
        try {
            Template temp = new Template("htmlTemplate", content, config);
            StringWriter out = new StringWriter();
            Map<String, Object> map = objectMapper.convertValue(job.payload(), new TypeReference<>() {});
            temp.process(map, out);
            try (Playwright playwright = Playwright.create(); Browser browser = playwright.chromium().launch();){
                Page page = browser.newPage();
                page.setContent(out.toString());
                byte[] bytes = page.pdf(new Page.PdfOptions().setFormat("Letter"));
                //                return s3Service.getSignedUrl(key);
                return s3Service.uploadDocToObjectStorage(bytes, generateKey(jobId));

            }
        } catch (Exception e){
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    private String generateKey(UUID jobId){
        return "file-%s.pdf".formatted(jobId);
    }
}
