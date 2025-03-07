package com.jzlx.utils;

import com.jzlx.entity.TerminalInfo;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
@Component
public class TerminalInfoParser {

    public static TerminalInfo parse(String report) throws ParseException {
        // 去除末尾的换行符和回车符
        report = report.trim().replaceAll("[\\r\\n]", "");

        // 分割字符串
        String[] parts = report.split(",");

        // 创建 TerminalInfo 对象
        TerminalInfo terminalInfo = new TerminalInfo();

        // 解析各个部分
        terminalInfo.setId(generateTaskId()); // 使用生成的 UUID
        terminalInfo.setTerminalId(parts[0]);
        terminalInfo.setTime(convertTime(parts[1]));
        terminalInfo.setChannel(parts[2]);
        terminalInfo.setLatitude(parseLatitude(parts[3]));
        terminalInfo.setLongitude(parseLongitude(parts[4]));
        terminalInfo.setAltitude(Double.parseDouble(parts[5]));
        terminalInfo.setBat(Integer.parseInt(parts[6]));
        terminalInfo.setMr(Double.parseDouble(parts[7].split("\\*")[0]));
        terminalInfo.setChecksum(parts[7].split("\\*")[1]);

        return terminalInfo;
    }

    private static String generateTaskId() {
        // 生成 UUID
        return java.util.UUID.randomUUID().toString();
    }

    private static Date convertTime(String time) throws ParseException {
        // 原始时间格式为 yymmddhhmmss
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = originalFormat.parse(time);
        return date;
    }

    private static Double parseLatitude(String lat) {
        // 去除 N 并转换为 Double
        return Double.parseDouble(lat.substring(0, lat.length() - 1)) / 100;
    }

    private static Double parseLongitude(String lon) {
        // 去除 E 并转换为 Double
        return Double.parseDouble(lon.substring(0, lon.length() - 1)) / 100;
    }

    public static void main(String[] args) {
        String report = "p:1,250219181823,tt,3150.7228N,11711.9293E,56.3,85,110*7b";
        try {
            TerminalInfo terminalInfo = TerminalInfoParser.parse(report);
            System.out.println(terminalInfo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
