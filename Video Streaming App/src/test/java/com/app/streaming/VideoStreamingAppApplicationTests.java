package com.app.streaming;

import com.app.streaming.services.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VideoStreamingAppApplicationTests {

    @Autowired
    VideoService videoService;

    @Test
    void contextLoads() {
        videoService.processVideo("95a3b42f-134a-424d-a7ae-5cc25de56fc2");
    }

}
