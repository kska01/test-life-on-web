package com.lifeon.test.domain.plan;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PlanFileController {

    private final PlanFileService planFileService;

    @PostMapping("/plan-files")
    public ResponseEntity uploadFile(@RequestPart(value = "planFile")MultipartFile planFile) {

        planFileService.uploadFile(planFile);

        return ResponseEntity.noContent().build();
    }
}
