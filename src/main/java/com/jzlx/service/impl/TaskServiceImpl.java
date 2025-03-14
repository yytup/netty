package com.jzlx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzlx.entity.Task;
import com.jzlx.mapper.TaskMapper;
import com.jzlx.service.TaskService;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
}