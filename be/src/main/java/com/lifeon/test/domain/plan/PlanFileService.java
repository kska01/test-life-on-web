package com.lifeon.test.domain.plan;

import com.lifeon.test.global.exception.FileUploadException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanFileService {

    private final PlanFileRepository planFileRepository;

    @Value("${file.dir}")
    private String fileDir;

    @Transactional
    public void uploadFile (MultipartFile planFile) {
        String fileUrl = null;

        if (planFile != null && !planFile.isEmpty()) {
            fileUrl = saveFile(planFile);
        }

        log.info("file : {}", fileUrl);
        log.info("planFile : {}", planFile);

    }

    String saveFile(MultipartFile planFile) {
        try {
            // 현재 프로젝트 디렉토리
            String projectDir = Paths.get("").toAbsolutePath().toString();

            // 디렉토리 관련 객체 생성
            File directory = new File(projectDir, fileDir);

            // 디렉토리가 없으면 생성
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 원본 파일명
            String originalName = planFile.getOriginalFilename();

            // 저장할 파일명 (UUID + 원본파일명)
            String savedFileName = UUID.randomUUID() + "_" + originalName;

            // 파일 저장
            planFile.transferTo(new File(directory, savedFileName));

            return savedFileName;


        } catch (IOException e){
            throw new FileUploadException();
        }
    }
}
