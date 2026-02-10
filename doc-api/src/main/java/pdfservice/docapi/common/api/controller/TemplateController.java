package pdfservice.docapi.common.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/templates")
public class TemplateController {
    @GetMapping("/{templateVersionId}")
    public ResponseEntity<String> getTemplate(@PathVariable UUID templateVersionId) {
        return ResponseEntity.ok("hello, world");
    }
}
