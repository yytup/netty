//package com.jzlx.timetask;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.jzlx.entity.ConnectObject;
//import com.jzlx.entity.Task;
//import com.jzlx.entity.TerminalInfo;
//import com.jzlx.service.TaskService;
//import com.jzlx.service.TerminalInfoService;
//import com.jzlx.utils.TerminalInfoParser;
//import com.jzlx.websocket.WebSocketServer;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.text.ParseException;
//import java.time.LocalDateTime;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
///**
// * @Description:
// * @Author: yyt
// * @CreateTime: 2025/3/4 13:14
// */
//@Component
//@EnableScheduling
//@Slf4j
//@EnableAsync
//public class TimeTask {
//
//    @Resource
//    private TaskService taskService;
//
//    @Resource
//    private TerminalInfoService terminalInfoService;
//
//    private Map<String,String> map = new HashMap<>();
//
//
////    @Async
////    @Scheduled(fixedDelay = 1000)  //间隔1秒
////    public void task0() throws Exception {
////        System.out.println("第一个定时任务开始 : " + LocalDateTime.now().toLocalTime() + "\r\n线程 : " + Thread.currentThread().getName());
////
////        // 第一步，接入硬件设备，硬件设备发送消息告诉已连接上，后端新增一条任务Task对象，记录任务id和终端id到数据库，同时发送一条websocket给前端进行通知
////        Task task = new Task();
////        task.setId(UUID.randomUUID().toString());
////        task.setTaskId(UUID.randomUUID().toString());
////        task.setTerminalId("p:1");
////        task.setTaskName("任务-"+ System.currentTimeMillis());
////        task.setTaskType("辐射监测数据");
////        taskService.save(task);
////        ConnectObject connectObject = new ConnectObject();
////        connectObject.setTaskId(task.getTaskId());
////        connectObject.setTerminalId(task.getTerminalId());
////        WebSocketServer.sendInfo(JSON.toJSONString(connectObject),"connectObject");
////
////        // 第二步，模拟
////
////        // 先从硬件上获取数据帧信息，然后转成json格式，这里先模拟数据帧生成
////        String report = "p:1,250219181823,tt,3150.7228N,11711.9293E,56.3,85,110*7b";
////        try {
////            TerminalInfo terminalInfo = TerminalInfoParser.parse(report);
////            //将解析的帧数据存储数据库，并通过websocket发送给前端
////            terminalInfo.setTaskId(task.getTaskId());
////            terminalInfoService.save(terminalInfo);
////            WebSocketServer.sendInfo(JSON.toJSONString(terminalInfo),"infoReport");
////        } catch (ParseException e) {
////            e.printStackTrace();
////        }
////
////        // 如果软件与硬件断开连接，发送消息通知前端已断开连接
////        ConnectObject disconnectObject = new ConnectObject();
////        disconnectObject.setTaskId(task.getTaskId());
////        disconnectObject.setTerminalId(task.getTerminalId());
////        WebSocketServer.sendInfo(JSON.toJSONString(connectObject) ,"disconnect ");
////
////    }
//    private boolean isConnectionInitialized = false;
//    private boolean isConnectionTerminated = false;
//
//    @Async
//    @Scheduled(fixedDelay = 60000, initialDelay = 0)  // 服务启动时运行一次
//    public void initializeConnection() throws Exception {
//        if (!isConnectionInitialized) {
//            System.out.println("初始化连接任务开始 : " + LocalDateTime.now().toLocalTime() + "\r\n线程 : " + Thread.currentThread().getName());
//
//            // 第一步，接入硬件设备，硬件设备发送消息告诉已连接上，后端新增一条任务Task对象，记录任务id和终端id到数据库，同时发送一条websocket给前端进行通知
//            Task task = new Task();
//            task.setId(UUID.randomUUID().toString());
//            task.setTaskId(UUID.randomUUID().toString());
//            task.setTerminalId("p:1");
//            task.setTaskName("任务-" + System.currentTimeMillis());
//            task.setTaskType("辐射监测数据");
//            taskService.save(task);
//            map.put("taskId", task.getTaskId());
//            map.put("terminalId", task.getTerminalId());
//            ConnectObject connectObject = new ConnectObject();
//            connectObject.setTaskId(task.getTaskId());
//            connectObject.setTerminalId(task.getTerminalId());
//            WebSocketServer.sendInfo(JSON.toJSONString(connectObject), "connectObject");
//
//            isConnectionInitialized = true;
//        }
//    }
//
//    /**
//     * 发送数据帧任务
//     */
//    @Async
//    @Scheduled(fixedRate = 1000, initialDelay = 10000)  // 服务启动10秒后开始每秒执行一次
//    public void sendDataFrames() throws Exception {
//        if (isConnectionInitialized && !isConnectionTerminated) {
//            System.out.println("发送数据帧任务开始 : " + LocalDateTime.now().toLocalTime() + "\r\n线程 : " + Thread.currentThread().getName());
//
//            // 先从硬件上获取数据帧信息，然后转成json格式，这里先模拟数据帧生成
//            String report = "p:1,250219181823,tt,3150.7228N,11711.9293E,56.3,85,110*7b";
//            try {
//                TerminalInfo terminalInfo = TerminalInfoParser.parse(report);
//                // 将解析的帧数据存储数据库，并通过websocket发送给前端
//                terminalInfo.setTaskId(map.get("taskId"));
//                terminalInfo.setTerminalId(map.get("terminalId"));
//                terminalInfo.setTime(new Date());
//                terminalInfoService.save(terminalInfo);
//                WebSocketServer.sendInfo(JSON.toJSONString(terminalInfo), "infoReport");
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 终止连接任务
//     */
//    @Async
//    @Scheduled(fixedDelay = 900000, initialDelay = 900000)  // 15分钟后执行一次
//    public void terminateConnection() throws IOException {
//        if (!isConnectionTerminated) {
//            System.out.println("终止连接任务开始 : " + LocalDateTime.now().toLocalTime() + "\r\n线程 : " + Thread.currentThread().getName());
//
//            // 如果软件与硬件断开连接，发送消息通知前端已断开连接
//            ConnectObject disconnectObject = new ConnectObject();
//            disconnectObject.setTaskId(map.get("taskId"));
//            disconnectObject.setTerminalId(map.get("terminalId"));
//            WebSocketServer.sendInfo(JSON.toJSONString(disconnectObject), "disconnect");
//
//            isConnectionTerminated = true;
//        }
//    }
//
//}
