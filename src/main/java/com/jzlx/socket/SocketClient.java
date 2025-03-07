//package com.jzlx.socket;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.Socket;
//
//@Component
//public class SocketClient implements ApplicationRunner {
//
//    @Value("${socket.client.hostname}")
//    private String hostname;
//
//    @Value("${socket.client.port}")
//    private int port;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        try (Socket socket = new Socket(hostname, port);
//             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
//
//            System.out.println("Connected to the server");
//
//            // 发送消息到服务器
//            out.println("$$connect server ok");
//            out.println("Some other message");
//
//            // 读取服务器的响应（如果需要）
//            String response;
//            while ((response = in.readLine()) != null) {
//                System.out.println("Server response: " + response);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
