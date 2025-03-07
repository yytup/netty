package com.jzlx.websocket;

import com.alibaba.fastjson.JSON;
import com.jzlx.entity.ConnectObject;
import com.jzlx.entity.Task;
import com.jzlx.entity.TerminalInfo;
import com.jzlx.service.TaskService;
import com.jzlx.service.TerminalInfoService;
import com.jzlx.socket.SharedDataQueue;
import com.jzlx.utils.TerminalInfoParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description:ServerEndpoint它的功能主要是将目前的类定义成一个websocket服务器端,注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 * connectObject // 终端在连接服务器后主动上报的信息
 * disconnect // 终端与服务器断开连接后的信息
 * infoReport // 信息上报 终端上报命令内容*
 * @Author: yyt
 * @CreateTime: 2022/11/12 11:09
 */

@Component
@Slf4j
@ServerEndpoint("/websocket/{sid}")
public class WebSocketServer implements ApplicationContextAware {

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收sid
    private String sid = "";

    // 保存taskId和terminalId
    private String taskId;
    private String terminalId;

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketServer.applicationContext = applicationContext;
    }



    // 任务调度器
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    @PostConstruct
    public void init() {
        // 启动定时任务，定期检查共享队列并发送数据
        scheduler.scheduleAtFixedRate(this::sendDataFromQueue, 1, 1, TimeUnit.SECONDS);
    }

    private void sendDataFromQueue() {
        try {
            String data = SharedDataQueue.take();
            sendInfo(data, "infoReport");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        this.sid = sid;
        addOnlineCount();           //在线数加1
        log.info("有新窗口开始监听:" + sid + ",当前在线人数为:" + getOnlineCount());
//        // 模拟设备连接并推送消息
//        simulateDeviceConnection();
//        // 每秒发送一次数据帧
//        scheduler.scheduleAtFixedRate(this::simulateDataFrames, 1, 1, TimeUnit.SECONDS);
//
//        // 10分钟后模拟设备断开连接
//        scheduler.schedule(this::simulateDeviceDisconnection, 600, TimeUnit.SECONDS);

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        //断开连接情况下，更新主板占用情况为释放
        log.info("释放的sid为："+sid);
        //这里写你 释放的时候，要处理的业务
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
        // 关闭调度器
        scheduler.shutdown();
    }

    /**
     * 收到客户端消息后调用的方法
     * @ Param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到来自窗口" + sid + "的信息:" + message);
        //群发消息
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @ Param session
     * @ Param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        //加同步锁，解决多线程下发送消息异常关闭
        synchronized (this.session){
            this.session.getBasicRemote().sendText(message);
        }
    }

    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message, @PathParam("sid") String sid) throws IOException {
        log.info("推送消息到窗口" + sid + "，推送内容:" + message);

        for (WebSocketServer item : webSocketSet) {
            try {
                //这里可以设定只推送给这个sid的，为null则全部推送
                if (sid == null) {
//                    item.sendMessage(message);
                } else if (item.sid.equals(sid)) {
                    item.sendMessage(message);
                }
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    public static CopyOnWriteArraySet<WebSocketServer> getWebSocketSet() {
        return webSocketSet;
    }

    /**
     * 模拟设备连接并推送消息
     */
    private void simulateDeviceConnection() {
        taskId = UUID.randomUUID().toString();
        terminalId = "p:1";
        TaskService taskService = applicationContext.getBean(TaskService.class);
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTaskId(taskId);
        task.setTerminalId(terminalId);
        task.setTaskName("任务-" + System.currentTimeMillis());
        task.setTaskType("辐射监测数据");
        taskService.save(task);

        ConnectObject connectObject = new ConnectObject();
        connectObject.setTaskId(taskId);
        connectObject.setTerminalId(terminalId);
        try {
            sendInfo(JSON.toJSONString(connectObject), "connectObject");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟每秒发送一次数据帧
     */
    private void simulateDataFrames() {
        String report = "p:1,250219181823,tt,3150.7228N,11711.9293E,56.3,85,110*7b";
        try {
            TerminalInfo terminalInfo = TerminalInfoParser.parse(report);
            terminalInfo.setTaskId(taskId);
            terminalInfo.setTerminalId(terminalId);
            // 将解析的帧数据存储数据库，并通过websocket发送给前端
            terminalInfo.setTime(new Date());
            TerminalInfoService terminalInfoService = applicationContext.getBean(TerminalInfoService.class);
            terminalInfoService.save(terminalInfo);
            sendInfo(JSON.toJSONString(terminalInfo), "infoReport");
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟设备断开连接并推送消息
     */
    private void simulateDeviceDisconnection() {
        ConnectObject disconnectObject = new ConnectObject();
        disconnectObject.setTaskId(taskId);
        disconnectObject.setTerminalId(terminalId);
        try {
            sendInfo(JSON.toJSONString(disconnectObject), "disconnect");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
