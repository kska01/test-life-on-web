package com.lifeon.test.domain.plan.uitil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DwgToDxfConverter {

    @Value("${odaConverterPath}")
    private String odaConverterPath;

    public boolean convert(String dwgFilePath, String outputDxfFilePath) {
        // dwg 파일 객체
        File dwgFile = new File(dwgFilePath);
        // dxf 파일 저장되는 폴더 경로
        File outputDxfFile = new File(outputDxfFilePath);
        // updloads 폴더까지 경로
        File parentDir = dwgFile.getParentFile();

        if (parentDir == null) {
            parentDir = new File("."); // 현재 디렉토리 사용
        }

        // uploads 폴더 안에 생성되는 oda_input 폴더 와 oda_output 폴더 경로
        File inputDir = new File(parentDir, "oda_input");
        File outputDir = new File(parentDir, "oda_output");

        if (!inputDir.exists()) inputDir.mkdirs();
        if (!outputDir.exists()) outputDir.mkdirs();

        // oda_input 폴더 안에 dwg 파일과 같은 이름으로 만들어진 객체, 실제파일이 아니라 dwg 파일이 복사될 위치를 가르키는 객체
        File tempDwgFile = new File(inputDir, dwgFile.getName());
        try {
            // copy() 메서드가 path type 을 받는다.
            // dwg 파일을 tempDwgFile 객체 경로에 복사한다, 이미 존재하면 덮어쓰기.
            Files.copy(dwgFile.toPath(), tempDwgFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("DWG 파일 복사 중 오류 발생", e);
            // 파일 복사 실패 시 처리
            deleteDirectory(inputDir);
            deleteDirectory(outputDir);
            return false;
        }

        // dwg -> dxf 변환위한 processBuilder 객체 만들기
        ProcessBuilder processBuilder = new ProcessBuilder(
            odaConverterPath,
            inputDir.getAbsolutePath(),         // 입력 폴더
            outputDir.getAbsolutePath(),        // 출력 폴더
            "ACAD2018",                         // converter version
            "DXF",                              // 출력 타입
            "0",                                // 재귀 플래그
            "0"                                 // dwg 각 파일을 별도 오류 검사 안함
        );

        try {
            Process process = processBuilder.start();

            // converter가 종료될 때까지 현재 스레드 일시 중지
            // converter가 정상 종료시 0을 exitCode에 반환, 오류 발생하여 종료되면 다른값 반환
            int exitCode = process.waitFor();

            // outputDxfFile에 해당하는 폴더가 없으면 생성
            if (!outputDxfFile.exists()) {
                outputDxfFile.mkdirs();
                log.info("Output DXF directory created: {}", outputDxfFile.getAbsolutePath());
            }

            // dxfFiles 폴더에 저장할 dxf로 변환된 파일 객체
            File convertedDxf = new File(outputDir, dwgFile.getName().replaceFirst("(?i)\\.dwg$", ".dxf"));

            if (exitCode == 0 && convertedDxf.exists()) {
                // 최종 DXF 파일 저장 경로 생성 (폴더 + 파일명)
                File targetDxfFile = new File(outputDxfFile, dwgFile.getName().replaceFirst("(?i)\\.dwg$", ".dxf"));

                // 출력 파일이 이미 존재하면 삭제
                if (targetDxfFile.exists()) {
                    targetDxfFile.delete();
                }

                Files.move(convertedDxf.toPath(), targetDxfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                deleteDirectory(inputDir);
                deleteDirectory(outputDir);
                return true;
            } else {
                // 변환 실패 시 메시지 출력
                System.err.println("DWG to DXF 변환 실패 (종료 코드: " + exitCode + ")");
                deleteDirectory(inputDir);
                deleteDirectory(outputDir);
                return false;
            }

        } catch (IOException | InterruptedException e) {
            log.error("DWG to DXF 변환 중 오류 발생", e);
            deleteDirectory(inputDir);
            deleteDirectory(outputDir);
            return false;
        }
    }

    private boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            return directory.delete();
        }
        return true;
    }

}
