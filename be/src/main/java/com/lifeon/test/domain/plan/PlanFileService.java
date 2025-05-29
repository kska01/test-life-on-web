package com.lifeon.test.domain.plan;

import com.lifeon.test.domain.plan.uitil.DwgToDxfConverter;
import com.lifeon.test.global.exception.FileUploadException;
import jakarta.annotation.PostConstruct;
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
    private final String projectDirPath = Paths.get("").toAbsolutePath().toString();
    private File dwgStorageDirPath;

    @Value("${file.dir}")
    private String fileDir;

    @Value(("${dxf.output.dir}"))
    private String dxfOutputDir;

    @PostConstruct
    public void initPath() {
        dwgStorageDirPath = new File(projectDirPath, fileDir);

        // 디렉토리가 없으면 생성
        if (!dwgStorageDirPath.exists()) {
            dwgStorageDirPath.mkdirs();
        }
    }

    @Transactional
    public void uploadFile(MultipartFile planFile) {

        if (planFile != null && !planFile.isEmpty()) {
            String dwgFileSavedName = saveFile(planFile);
            String dwgFilePath = makeDwgFilePath(dwgFileSavedName);

            convertDwgToDxf(dwgFilePath);
            log.info("file : {}", dwgFileSavedName);
        }

        log.info("planFile : {}", planFile);
    }

    private void convertDwgToDxf(String dwgFilePath) {
        String dxfOutputDirPath = new File(projectDirPath, dxfOutputDir).getAbsolutePath();
        dwgToDxfConverter.convert(dwgFilePath, dxfOutputDirPath);
    }

    private String saveFile(MultipartFile planFile) {
        try {
            // 원본 파일명
            String originalName = planFile.getOriginalFilename();

            // 저장할 파일명 (UUID + 원본파일명)
            String dwgFileSavedName = UUID.randomUUID() + "_" + originalName;

            // 파일 저장
            planFile.transferTo(new File(dwgStorageDirPath, dwgFileSavedName));

            return dwgFileSavedName;
        } catch (IOException e) {
            throw new FileUploadException();
        }
    }

    private String makeDwgFilePath(String dwgFileUrl) {
        return new File(dwgStorageDirPath, dwgFileUrl).getAbsolutePath();
    }
}
