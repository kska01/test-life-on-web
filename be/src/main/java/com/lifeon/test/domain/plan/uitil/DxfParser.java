package com.lifeon.test.domain.plan.uitil; // 패키지명은 사용자 환경에 맞게

import com.lifeon.test.domain.plan.dto.DoorInfo;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFInsert;
import org.kabeja.dxf.DXFLayer;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DxfParser {

    // DXF 파일 경로를 인자로 받도록 수정 (기존 코드와 동일)
    public List<DoorInfo> extractDoorInfo(String dxfFilePath) { // 반환 타입을 List<DoorInfo>로 명시
        List<DoorInfo> doorInfos = new ArrayList<>();
        Parser parser = ParserBuilder.createDefaultParser();

        try {
            log.info("Starting DXF parsing for file: {}", dxfFilePath);
            parser.parse(new FileInputStream(dxfFilePath), "Cp1252");
            DXFDocument doc = parser.getDocument();

            // 1. 지정된 레이어에서 문(Door) 엔티티 검색
            String[] targetLayerNames = {
                "Door", "A-DOOR", "DOOR", "I-DOOR-SWING", // 기존 이름들
                "L-Door", "AA-DWXM-DOOR"                 // DXF 파일에서 확인된 이름들
            }; // 예시 레이어 이름들
            log.debug("Target layer names for door search: {}", (Object)targetLayerNames);

            for (String layerName : targetLayerNames) {
                DXFLayer layer = doc.getDXFLayer(layerName);
                // Kabeja는 지정된 레이어가 없으면 기본 "0" 레이어를 반환하거나 새로 생성할 수 있으므로,
                // 실제 해당 이름의 레이어인지 확인하거나, layer.getName()을 비교하는 것이 더 정확할 수 있습니다.
                // 여기서는 layer 객체가 null이 아니고, 실제 이름이 layerName과 일치하는 경우로 가정합니다. (Kabeja 0.4 기준으로는 getDXFLayer가 null을 반환하지 않을 수 있음)
                if (layer != null && layer.getName().equalsIgnoreCase(layerName)) { // 실제 레이어 이름과 대조
                    log.info("Processing layer: {}", layer.getName());
                    List<DXFEntity> entitiesInLayer = getAllEntitiesFromLayer(layer);

                    if (entitiesInLayer.isEmpty()) {
                        log.info("No entities found in layer: {}", layer.getName());
                        continue;
                    }

                    for (DXFEntity entity : entitiesInLayer) {
                        processEntityForDoor(entity, doc, doorInfos, layer.getName());
                    }
                } else {
                    log.debug("Layer '{}' not found or not matching the exact name.", layerName);
                }
            }

            // 2. 지정된 레이어에서 문을 찾지 못한 경우, 문서 전체의 모든 INSERT 엔티티를 검색
            if (doorInfos.isEmpty()) {
                log.info("No doors found in specific target layers. Scanning all INSERT entities from all layers in the document...");
                Iterator<DXFLayer> layerIterator = doc.getDXFLayerIterator();
                while (layerIterator.hasNext()) {
                    DXFLayer currentLayer = layerIterator.next();
                    log.debug("Scanning for INSERT entities in layer: {}", currentLayer.getName());
                    // 특정 레이어에서 "INSERT" 타입의 엔티티만 가져옵니다.
                    List<DXFEntity> insertEntitiesInLayer = currentLayer.getDXFEntities("INSERT");
                    if (insertEntitiesInLayer != null) {
                        for (DXFEntity entity : insertEntitiesInLayer) {
                            // processEntityForDoor 내부에서 INSERT 타입 및 블록 이름 필터링 수행
                            processEntityForDoor(entity, doc, doorInfos, currentLayer.getName());
                        }
                    }
                }
            }

        } catch (FileNotFoundException e) {
            log.error("DXF File not found: {}", dxfFilePath, e);
            // 필요시 사용자 정의 예외를 던지거나, 빈 리스트 반환 유지
        } catch (Exception e) { // 보다 구체적인 ParseException 등을 잡는 것이 좋을 수 있습니다.
            log.error("Error parsing DXF file: {}", dxfFilePath, e);
        }

        log.info("Extraction complete. Found {} door(s).", doorInfos.size());
        if (log.isDebugEnabled()) { // 디버그 레벨일 때만 상세 정보 로깅
            doorInfos.forEach(doorInfo -> log.debug("Extracted Door: {}", doorInfo.toString()));
        }
        return doorInfos; // 추출된 문 정보 리스트 반환
    }

    /**
     * DXFLayer에서 모든 엔티티를 가져옵니다.
     */
    private List<DXFEntity> getAllEntitiesFromLayer(DXFLayer layer) {
        List<DXFEntity> entitiesInLayer = new ArrayList<>();
        if (layer == null) {
            return entitiesInLayer;
        }

        Iterator<String> entityTypeIterator = layer.getDXFEntityTypeIterator();
        if (entityTypeIterator != null) {
            while (entityTypeIterator.hasNext()) {
                String entityType = entityTypeIterator.next();
                List<DXFEntity> entitiesOfType = layer.getDXFEntities(entityType);
                if (entitiesOfType != null) {
                    entitiesInLayer.addAll(entitiesOfType);
                }
            }
        }
        return entitiesInLayer;
    }

    /**
     * 개별 엔티티를 처리하여 문 정보를 추출하고 doorInfos 리스트에 추가합니다.
     */
    private void processEntityForDoor(DXFEntity entity, DXFDocument doc, List<DoorInfo> doorInfos, String currentLayerName) {
        if (!"INSERT".equals(entity.getType())) {
            return;
        }

        DXFInsert insert = (DXFInsert) entity;
        String blockName = insert.getBlockID();

        // INFO 레벨로 모든 INSERT 블록의 이름과 레이어를 로그에 남겨서 확인
        log.info("Found INSERT entity: BlockName='{}', Layer='{}'", blockName, currentLayerName);

        // 문을 나타내는 블록 이름 키워드 (대소문자 무시)
        // 아래 키워드들을 실제 발견되는 문 블록 이름에 맞춰 수정해야 합니다.
        String[] doorKeywords = {"DOOR", "문", "_OPEN", "SLIDING", "HINGE", "D_"}; // 예시 키워드 (매우 일반적)
        boolean isDoorBlock = false;
        if (blockName != null) {
            String upperBlockName = blockName.toUpperCase();
            for (String keyword : doorKeywords) {
                // 정확한 일치 또는 포함 여부 (도면 표준에 따라)
                // 예: if (upperBlockName.equals(keyword.toUpperCase())) 또는 if (upperBlockName.startsWith(keyword.toUpperCase()))
                if (upperBlockName.contains(keyword.toUpperCase())) {
                    isDoorBlock = true;
                    break;
                }
            }
        }

        if (isDoorBlock) {
            log.info("Potential door block MATCHED: Name='{}', Layer='{}'", blockName, currentLayerName); // MATCHED 로그 추가
            Point insertionPoint = insert.getPoint();
            // ... (이하 동일) ...
            DoorInfo door = new DoorInfo(
                insertionPoint.getX(),
                insertionPoint.getY(),
                insert.getScaleX(),
                insert.getScaleY(),
                insert.getRotate(),
                currentLayerName,
                blockName
            );
            doorInfos.add(door);
            log.info("Door added: Block='{}', Layer='{}', X={}, Y={}", blockName, currentLayerName, door.getX(), door.getY());

        } else if (blockName != null) {
            log.debug("Non-door INSERT entity SKIPPED: BlockName='{}', Layer='{}'", blockName, currentLayerName); // SKIPPED 로그 추가
        } else {
            log.debug("INSERT entity with null blockName SKIPPED on layer '{}'", currentLayerName);
        }
    }
}

