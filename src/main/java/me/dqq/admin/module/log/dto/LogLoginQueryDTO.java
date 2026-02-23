package me.dqq.admin.module.log.dto;

import lombok.Data;

@Data
public class LogLoginQueryDTO {
    private String username;
    private Integer status;
    private String startTime;
    private String endTime;
    private Long current = 1L;
    private Long size = 10L;
}
