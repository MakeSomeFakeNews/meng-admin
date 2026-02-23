package me.dqq.admin.module.log.dto;

import lombok.Data;

@Data
public class LogOperateQueryDTO {
    private String module;
    private String operatorName;
    private Integer status;
    private String startTime;
    private String endTime;
    private Long current = 1L;
    private Long size = 10L;
}
