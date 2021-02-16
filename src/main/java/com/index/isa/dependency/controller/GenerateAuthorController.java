package com.index.isa.dependency.controller;

import com.index.isa.dependency.config.DownloadConfig;
import com.index.isa.dependency.model.MessageModel;
import com.index.isa.dependency.service.DependencyService;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class GenerateAuthorController {

    @Autowired
    DownloadConfig download;

    @Autowired
    DependencyService service;

    @GetMapping("/generate-Author")
    public void generateAuthorController(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam(value = "topic") String topic,
                                         @RequestParam(value = "Start") String start,
                                         @RequestParam(value = "end") String end) throws IOException {
        try {
            MessageModel msg = new MessageModel();
            msg = service.generateAuthor(topic, start, end);
            msg.setStatus("true");
            msg.setMessage("success");
            String full = download.getPath_save() + "/" + msg.getData();
            File files = new File(full);
            System.out.println("files "+files);
            InputStream myStream = new FileInputStream(files);
            response.addHeader("Content-Disposition", "attachment;filename=" + msg.getData() + "");
            response.setContentType("application/octet-stream");
            IOUtils.copy(myStream, response.getOutputStream());
            response.flushBuffer();
            System.out.println("anjay");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
