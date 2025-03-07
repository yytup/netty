package com.jzlx.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description:
 * @Author: yyt
 * @CreateTime: 2025/3/4 15:46
 */
@TableName(value = "task")
@Data
public class Task {
    
    private String id;
    
    private String taskId;

    private String taskName;

    /**
     * 辐射监测数据,应急救援数据,环境监测数据,人员监测数据
     */
    private String taskType;

    private String terminalId;
}
