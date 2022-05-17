package com.example.controller;
import net.minidev.json.JSONObject;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Controller
public class FileController {

    @PostMapping("/httpFileUpload")
    public String uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String fileName = request.getParameter("fileName");
        System.out.println("filename:"+fileName);
        String fileFullPath = "/Users/lizhenghui/tmp/";
        String filePath= "/Users/lizhenghui/tmp/" + fileName;
        InputStream input = null;
        FileOutputStream fos = null;
        try {
            input = request.getInputStream();
            File file = new File(fileFullPath);
            if(!file.exists()){
                file.mkdirs();
            }
            fos = new FileOutputStream(filePath);
            int size = 0;
            byte[] buffer = new byte[1024*1024*8];
            while ((size = input.read(buffer)) != -1) {
                fos.write(buffer, 0, size);
            }

            //响应信息 json字符串格式
          /*  Map<String,Object> responseMap = new HashMap<String,Object>();
            responseMap.put("flag", true);

            //生成响应的json字符串
            String jsonResponse = JSONObject.toJSONString(responseMap);
            sendResponse(jsonResponse,response);*/
        } catch (IOException e) {
            //响应信息 json字符串格式
            Map<String,Object> responseMap = new HashMap<String,Object>();
            responseMap.put("flag", false);
            responseMap.put("errorMsg", e.getMessage());
            String jsonResponse = JSONObject.toJSONString(responseMap);
            sendResponse(jsonResponse,response);
        } finally{
            if(input != null){
                input.close();
            }
            if(fos != null){
                fos.close();
            }
        }

        return null;
    }

    /**
     * 返回响应
     *
     * @throws Exception
     */
    private void sendResponse(String responseString,HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            pw.write(responseString);
            pw.flush();
        } finally {
            IOUtils.closeQuietly(pw);
        }
    }
}
