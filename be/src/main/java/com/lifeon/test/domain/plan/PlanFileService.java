package com.lifeon.test.domain.plan;

import com.lifeon.test.domain.plan.uitil.DwgToDxfConverter;
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
    private final DwgToDxfConverter dwgToDxfConverter;

    @Value("${file.dir}")
    private String fileDir;

    @Value(("${dxf.output.dir}"))
    private String dxfOutputDir;

    private String dwgFileUrl;
    private String dwgFilePath;
    private String outputDxfFilePath;

    @Transactional
    public void uploadFile(MultipartFile planFile) {

        if (planFile != null && !planFile.isEmpty()) {
            dwgFileUrl = saveFileAndMakPath(planFile);
            convertDwgToDxf();
        }

        log.info("file : {}", dwgFileUrl);
        log.info("planFile : {}", planFile);
    }

    private void convertDwgToDxf() {
        dwgToDxfConverter.convert(dwgFilePath, outputDxfFilePath);
    }

    private String saveFileAndMakPath(MultipartFile planFile) {
        try {
            // 현재 프로젝트 디렉토리
            String projectDir = Paths.get("").toAbsolutePath().toString();

            // 디렉토리 관련 객체 생성
            File directory = new File(projectDir, fileDir);
            log.info("directory: {}", directory);

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

            // dwgFilePath 생성
            dwgFilePath = new File(directory, savedFileName).getAbsolutePath();
            log.info("dwgFilePath: {}", dwgFilePath);

            // dxf 파일 저장 경로 생성
            outputDxfFilePath = new File(projectDir, dxfOutputDir).getAbsolutePath();
            log.info("path: {}", outputDxfFilePath);

            return savedFileName;

        } catch (IOException e) {
            throw new FileUploadException();
        }
    }
}
