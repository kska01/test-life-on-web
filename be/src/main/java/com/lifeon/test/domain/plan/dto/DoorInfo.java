package com.lifeon.test.domain.plan.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class DoorInfo {

    private double x; // 문의 중심 x 좌표 또는 시작 x 좌표
    private double y; // 문의 중심 y 좌표 또는 시작 y 좌표
    private double width; // 문의 너비 (추정이 필요할 수 있음)
    private double height; // 문의 높이 (추정이 필요할 수 있음)
    private double rotation; // 문의 회전 각도
    private String layerName; // 문이 속한 레이어 이름
    private String blockName; // 문이 블록일 경우 블록 이름

    public DoorInfo(double x, double y, double width, double height, double rotate,
        String currentLayerName, String blockName) {
    }
}
