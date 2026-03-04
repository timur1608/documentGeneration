package pdfservice.docapi.common.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pdfservice.docapi.common.api.repository.TemplateRepository;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/templates")
public class TemplateController {
    private final TemplateRepository templateRepository;

    @Autowired
    public TemplateController(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }
    @GetMapping("/{templateVersionId}")
    public ResponseEntity<String> getTemplate(@PathVariable UUID templateVersionId) {
        Optional<String> content = templateRepository.getContent(templateVersionId);
        return content.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.badRequest().body("No templateVersionId found"));
    }
}
