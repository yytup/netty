package com.jzlx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzlx.entity.TerminalInfo;
import com.jzlx.mapper.TerminalInfoMapper;
import com.jzlx.service.TerminalInfoService;
import org.springframework.stereotype.Service;

@Service
public class TerminalInfoServiceImpl extends ServiceImpl<TerminalInfoMapper, TerminalInfo> implements TerminalInfoService {
}