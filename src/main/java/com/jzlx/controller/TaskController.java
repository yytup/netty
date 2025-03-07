package com.jzlx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jzlx.common.ResponseResult;
import com.jzlx.entity.Task;
import com.jzlx.entity.TaskPageRequest;
import com.jzlx.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);


    @PostMapping("/save")
    public ResponseResult<Boolean> save(@RequestBody Task task) {
        boolean result = taskService.save(task);
        return result ? ResponseResult.success(result, "Task saved successfully") : ResponseResult.error(500, "Failed to save task");
    }

    @PostMapping("/delete")
    public ResponseResult<Boolean> delete(@RequestBody Task task) {
        boolean result = taskService.removeById(task.getId());
        return result ? ResponseResult.success(result, "Task deleted successfully") : ResponseResult.error(500, "Failed to delete task");
    }

    @PostMapping("/update")
    public ResponseResult<Boolean> update(@RequestBody Task task) {
        boolean result = taskService.updateById(task);
        return result ? ResponseResult.success(result, "Task updated successfully") : ResponseResult.error(500, "Failed to update task");
    }

    @PostMapping("/getById")
    public ResponseResult<Task> getById(@RequestBody Task task) {
        Task result = taskService.getById(task.getId());
        return result != null ? ResponseResult.success(result, "Task retrieved successfully") : ResponseResult.error(404, "Task not found");
    }

    @PostMapping("/page")
    public ResponseResult<Page<Task>> list(@RequestBody TaskPageRequest request) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<Task>()
                .eq(request.getTaskId() != null, "task_id", request.getTaskId())
                .eq(request.getTerminalId() != null, "terminal_id", request.getTerminalId());
        logger.info("QueryWrapper: {}", queryWrapper.getSqlSegment());

        Page<Task> taskPage = taskService.page(new Page<>(request.getPageNum(), request.getPageSize()), queryWrapper);
        logger.info("Query Result Total: {}, Records Size: {}", taskPage.getTotal(), taskPage.getRecords().size());

        return ResponseResult.success(taskPage, "Tasks retrieved successfully");
    }
}