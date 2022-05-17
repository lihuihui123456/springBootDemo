package com.example.service;

import com.example.grpc.FileOperateServiceGrpc;
import com.example.grpc.UploadFileRequest;
import com.example.grpc.UploadFileResponse;
import com.example.util.FileUtils;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class FileHttpService {

    @Value("${grpc.client.address}")
    String serverIp;

    /**
     * 上传文件
     * @param fileName 保存到服务端的文件名
     * @param filepath 要上传的文件路径
     * @throws IOException
     */
    public String uploadFile(String fileName, String filepath) throws IOException {
        System.out.println("传入参数------》" + filepath + "----" + fileName);
        DataInputStream in = null;
        OutputStream out = null;
        HttpURLConnection conn = null;
        JSONObject resposeTxt = null;
        InputStream ins = null;
        ByteArrayOutputStream outStream = null;
        try {
            URL url = new URL("http://"+serverIp+":8080/httpFileUpload?fileName="+fileName);
            conn = (HttpURLConnection) url.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setChunkedStreamingMode(1024*1024*8);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/html");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.connect();
            conn.setConnectTimeout(10000);
            out = conn.getOutputStream();

            File file = new File(filepath);
            in = new DataInputStream(new FileInputStream(file));

            int length = 0;
            byte[] buffer = new byte[1024*1024*8];
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            out.flush();
            // 返回流
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
          /*      ins = conn.getInputStream();
                outStream = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int count = -1;
                while ((count = ins.read(data, 0, 1024)) != -1) {
                    outStream.write(data, 0, count);
                }
                data = null;
                resposeTxt = JSONObject.parseObject(new String(outStream
                        .toByteArray(), "UTF-8"));*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (ins != null) {
                ins.close();
            }
            if (outStream != null) {
                outStream.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return  "SUCESS";
    }
}