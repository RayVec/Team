package com.example.lzw.team20.activity.chatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HTTPUtils {
    public static String cookie;
    public static String setCookie(){
        String cookie="";
        try {
            // 1. 获取访问地址URL
            URL url = new URL("http://112.74.177.29:8080/together/user/signin");
            // 2. 创建HttpURLConnection对象
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            /* 3. 设置请求参数等 */
            // 请求方式
            connection.setRequestMethod("POST");
            // 超时时间
            connection.setConnectTimeout(3000);
            // 设置是否输出
            connection.setDoOutput(true);
            // 设置是否读入
            connection.setDoInput(true);
            // 设置是否使用缓存
            connection.setUseCaches(false);
            // 设置此 HttpURLConnection 实例是否应该自动执行 HTTP 重定向
            connection.setInstanceFollowRedirects(true);
            // 设置使用标准编码格式编码参数的名-值对
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 连接
            connection.connect();
            /* 4. 处理输入输出 */
            // 写入参数到请求中
            String params = "username=yichangweirui&pwd=weirui199748";
            OutputStream out = connection.getOutputStream();
            out.write(params.getBytes());
            out.flush();
            out.close();
            // 从连接中读取响应信息
            cookie=connection.getHeaderField("set-cookie");
            cookie=cookie.substring(0,cookie.indexOf(";"));
            // 5. 断开连接
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 处理结果
        return cookie;
    }
    public static String doGet(String target,String cookie){
        String msg="";
        try{
        URL url = new URL(target);
        // 2. 创建HttpURLConnection对象
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //设置cookie
        connection.setRequestProperty("cookie",cookie);
        // 连接
        connection.connect();
        int code = connection.getResponseCode();
        System.out.println(code);
        if (code == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(msg);
                msg += line + "\n";
            }
            reader.close();
        }
        // 5. 断开连接
        connection.disconnect();
    } catch (MalformedURLException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    // 处理结果
      return msg;
    }
    public static String doPost(String target,String cookie,String data){
        String msg="";
        try {
        URL url = new URL(target);
        // 2. 创建HttpURLConnection对象
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        /* 3. 设置请求参数等 */
        // 请求方式
        connection.setRequestMethod("POST");
        connection.setRequestProperty("cookie",cookie);
        // 连接
        connection.connect();
        /* 4. 处理输入输出 */
        OutputStream out = connection.getOutputStream();
        out.write(data.getBytes());
        out.flush();
        out.close();
        // 从连接中读取响应信息
        int code = connection.getResponseCode();
        System.out.println(code);
        if (code == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                msg += line + "\n";
            }
            reader.close();
        }
        // 5. 断开连接
        connection.disconnect();

    } catch (MalformedURLException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    // 处理结果
        return msg;
    }
}
