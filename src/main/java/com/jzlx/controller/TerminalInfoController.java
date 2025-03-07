package com.jzlx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jzlx.common.ResponseResult; // 引入新的ResponseResult类
import com.jzlx.entity.TaskPageRequest;
import com.jzlx.entity.TerminalInfo;
import com.jzlx.service.TerminalInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/terminalInfo")
public class TerminalInfoController {

    @Autowired
    private TerminalInfoService terminalInfoService;

    // 新增终端信息
    @PostMapping("/save")
    public ResponseResult<Void> add(@RequestBody TerminalInfo terminalInfo) {
        boolean result = terminalInfoService.save(terminalInfo);
        if (result) {
            return ResponseResult.success(null, "操作成功");
        } else {
            return ResponseResult.error(500, "操作失败");
        }
    }

    // 删除终端信息
    @PostMapping("/delete")
    public ResponseResult<Void> delete(@RequestBody TerminalInfo terminalInfo) {
        boolean result = terminalInfoService.removeById(terminalInfo.getId());
        if (result) {
            return ResponseResult.success(null, "操作成功");
        } else {
            return ResponseResult.error(500, "操作失败");
        }
    }

    // 更新终端信息
    @PostMapping("/update")
    public ResponseResult<Void> update(@RequestBody TerminalInfo terminalInfo) {
        boolean result = terminalInfoService.updateById(terminalInfo);
        if (result) {
            return ResponseResult.success(null, "操作成功");
        } else {
            return ResponseResult.error(500, "操作失败");
        }
    }

    // 查询所有终端信息
    @PostMapping("/findAll")
    public ResponseResult<List<TerminalInfo>> findAll() {
        List<TerminalInfo> terminalInfos = terminalInfoService.list();
        return ResponseResult.success(terminalInfos, "查询成功");
    }

    // 分页查询终端信息
    @PostMapping("/page")
    public ResponseResult<Page<TerminalInfo>> findPage(@RequestBody TaskPageRequest request) {
        QueryWrapper<TerminalInfo> queryWrapper = new QueryWrapper<>();
        if (request.getTaskId() != null) {
            queryWrapper.eq("task_id", request.getTaskId());
        }
        if (request.getTerminalId() != null) {
            queryWrapper.eq("terminal_id", request.getTerminalId());
        }
        // 添加时间区间的查询条件，并调整时间差
        if (request.getStartTime() != null) {
            Date adjustedStartTime = new Date(request.getStartTime().getTime() + TimeUnit.HOURS.toMillis(8));
            queryWrapper.ge("time", adjustedStartTime);
        }
        if (request.getEndTime() != null) {
            Date adjustedEndTime = new Date(request.getEndTime().getTime() + TimeUnit.HOURS.toMillis(8));
            queryWrapper.le("time", adjustedEndTime);
        }
        Page<TerminalInfo> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<TerminalInfo> resultPage = terminalInfoService.page(page, queryWrapper);
        return ResponseResult.success(resultPage, "查询成功");
    }

    // 根据taskId和terminalId查询终端信息，返回集合
    @PostMapping("/findByTaskAndTerminalId")
    public ResponseResult<List<TerminalInfo>> findByTaskAndTerminalId(@RequestBody TaskPageRequest request) {
        QueryWrapper<TerminalInfo> queryWrapper = new QueryWrapper<>();
        if (request.getTaskId() != null) {
            queryWrapper.eq("task_id", request.getTaskId());
        }
        if (request.getTerminalId() != null) {
            queryWrapper.eq("terminal_id", request.getTerminalId());
        }
        // 添加时间区间的查询条件，并调整时间差
        if (request.getStartTime() != null) {
            Date adjustedStartTime = new Date(request.getStartTime().getTime() + TimeUnit.HOURS.toMillis(8));
            queryWrapper.ge("time", adjustedStartTime);
        }
        if (request.getEndTime() != null) {
            Date adjustedEndTime = new Date(request.getEndTime().getTime() + TimeUnit.HOURS.toMillis(8));
            queryWrapper.le("time", adjustedEndTime);
        }
        List<TerminalInfo> terminalInfos = terminalInfoService.list(queryWrapper);
        return ResponseResult.success(terminalInfos, "查询成功");
    }


}