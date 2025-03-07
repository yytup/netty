package com.jzlx.netty;

import com.alibaba.fastjson.JSON;
import com.jzlx.entity.ConnectObject;
import com.jzlx.entity.Task;
import com.jzlx.entity.TerminalInfo;
import com.jzlx.service.TaskService;
import com.jzlx.service.TerminalInfoService;
import com.jzlx.utils.TerminalInfoParser;
import com.jzlx.websocket.WebSocketServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NettyServerHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware {

    // 使用 ConcurrentHashMap 来存储 IP 地址和终端 ID
    private static final Map<String, ConnectObject> clientTerminalMap = new ConcurrentHashMap<>();



    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        NettyServerHandler.applicationContext = applicationContext;
    }

    /**
     * 建立连接
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected: " + ctx.channel().remoteAddress().toString());
        // 发送 "getTerminalId" 消息给客户端
        ctx.writeAndFlush(Unpooled.copiedBuffer("@@cmd=getUAID\\r\\n", CharsetUtil.UTF_8));
    }

    /**
     * 根据接受的消息进行业务处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws ParseException {
        ByteBuf in = (ByteBuf) msg;
        try {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            String receivedMessage = new String(bytes, StandardCharsets.UTF_8);
            System.out.println(receivedMessage);
            String clientIp = ctx.channel().remoteAddress().toString();
            /// 检查是否是终端ID
            if (receivedMessage.startsWith("$$res=getUAID&value=")) {
                String terminalId = this.getTerminalId(receivedMessage);

                ConnectObject connectObject = new ConnectObject();
                String taskId = UUID.randomUUID().toString();
                connectObject.setTaskId(taskId);
                connectObject.setTerminalId(terminalId);
                // 数据库存储任务信息
                Task task = new Task();
                task.setId(UUID.randomUUID().toString());
                task.setTaskId(taskId);
                task.setTerminalId(terminalId);
                task.setTaskName("任务-" + System.currentTimeMillis());
                task.setTaskType("辐射监测数据");
                TaskService taskService = applicationContext.getBean(TaskService.class);
                taskService.save(task);
                clientTerminalMap.put(clientIp, connectObject);
                WebSocketServer.sendInfo(JSON.toJSONString(connectObject), "connectObject");
            }
            // 处理每帧的数据
            if (receivedMessage.contains("$$rep=p:")) {
                String receiveData = receivedMessage.substring(receivedMessage.indexOf("$$rep=p:") + 8);
                TerminalInfo terminalInfo = TerminalInfoParser.parse(receiveData);
                terminalInfo.setTaskId(clientTerminalMap.get(clientIp).getTaskId());
                // 将解析的帧数据存储数据库，并通过websocket发送给前端
                TerminalInfoService terminalInfoService = applicationContext.getBean(TerminalInfoService.class);
                terminalInfoService.save(terminalInfo);
                WebSocketServer.sendInfo(JSON.toJSONString(terminalInfo), "infoReport");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.release();
        }
    }

    /**
     * 断开连接处理
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String clientIp = ctx.channel().remoteAddress().toString();
        System.out.println("Client disconnected: " + clientIp);

        // 从 Map 中获取终端 ID
        ConnectObject connectObject = clientTerminalMap.get(clientIp);
        if (connectObject != null) {
            // 通过 WebSocket 告知前端哪个终端断开连接
            sendTerminalDisconnectMessageToWebSocket(connectObject);
        }
        clientTerminalMap.remove(clientIp);

        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public String getTerminalId(String input) {
        // 定义正则表达式
        String regex = "value=(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            // 提取匹配的值
            String value = matcher.group(1);
            return value;
        } else {
            System.out.println("Value not found");
        }
        return null;
    }
    private void sendTerminalDisconnectMessageToWebSocket(ConnectObject connectObject) {
        // 这里假设你有一个方法来发送消息到 WebSocket
        // 你需要根据你的 WebSocket 实现来编写这个方法
        // 例如：
        // WebSocketServer.sendMessage("Terminal " + terminalId + " disconnected");
        try {
            WebSocketServer.sendInfo(JSON.toJSONString(connectObject), "disconnect");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}