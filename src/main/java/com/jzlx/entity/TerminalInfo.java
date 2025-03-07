package com.jzlx.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 终端信息帧实体类
 * @Author: yyt
 * @CreateTime: 2025/3/4 13:04
 */
@TableName(value = "terminal")
@Data
public class TerminalInfo {

    private String id;
    private String taskId;
    private String terminalId;  // 终端编码
    private Date time;        // 时间
    private String channel;     // 信息采用的通道
    private Double latitude;    // 纬度
    private Double longitude;   // 经度
    private Double altitude;    // 高度
    private Integer bat;        // 电池剩余电量百分比
    private Double mr;          // MR-10-H测量值
    private String checksum;    // 异或校验和
}