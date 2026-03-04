package pdfservice.docrenderer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class TemplateService {
    private final RestTemplate restTemplate;

    public TemplateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getTemplateById(UUID templateVersionId){
        try {
            return restTemplate.getForObject(
                    "http://localhost:8080/templates/{id}",
                    String.class,
                    templateVersionId
            );
        } catch (Exception e){
            return "No such template";
        }
    }
}
