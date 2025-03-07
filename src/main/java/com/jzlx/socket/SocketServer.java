//package com.jzlx.socket;
//
//import com.jzlx.websocket.WebSocketServer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//@Component
//public class SocketServer implements ApplicationRunner {
//
//    @Value("${socket.server.port}")
//    private int port;
//
//    @Resource
//    private WebSocketServer webSocketServer;
//
//    private ExecutorService executorService = Executors.newCachedThreadPool();
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        startServer();
//    }
//
//    private void startServer() {
//        new Thread(() -> {
//            try (ServerSocket serverSocket = new ServerSocket(port)) {
//                System.out.println("Socket服务端已启动，监听端口：" + port);
//                while (true) {
//                    Socket clientSocket = serverSocket.accept();
//                    executorService.submit(() -> handleClient(clientSocket));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
//
//    private void handleClient(Socket clientSocket) {
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
//            String message;
//            while ((message = reader.readLine()) != null) {
//                System.out.println("收到客户端消息：" + message);
//                // 假设这里对消息进行了某种解析
//                String parsedMessage = parseMessage(message);
//                // 将解析后的消息放入共享队列
//                SharedDataQueue.add(parsedMessage);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private String parseMessage(String message) throws IOException {
//
//        if (message.equals("$$connect server ok")) {
//
//            WebSocketServer.sendInfo("$$connect server ok", null);
//        }
//        // 实现你的解析逻辑
//        return "parsed_" + message;
//    }
//}