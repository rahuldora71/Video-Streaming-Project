package com.app.streaming.services.ServiceImple;

import com.app.streaming.entities.Video;
import com.app.streaming.repositories.VideoRepository;
import com.app.streaming.services.VideoService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoRepository videoRepository;
    @Value("${files.video}")
    String DIR;

    @Value("${files.video.hls}")
    String HLS_DIR;

    @PostConstruct
    public void init() {
        System.out.println(DIR);
        File file = new File(DIR);

        try {
            Files.createDirectories(Path.of(HLS_DIR));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!file.exists()) {
            file.mkdirs();
            System.out.println("Folder Created");
        }else {
            System.out.println("Folder Already Exists");
        }
    }

    @Override
    public Video saveVideo(Video video, MultipartFile file) {

        try {

            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            InputStream inputStream = file.getInputStream();

            System.out.println(fileName + " |||||| " + contentType + " ||||||| " + inputStream);
            // Folder Create

            String cleanFileName = StringUtils.cleanPath(fileName);
            String cleanFolder = StringUtils.cleanPath(DIR);
            Path path = Paths.get(cleanFolder, cleanFileName);

            // Copy the file to the specified path+
            Files.copy(inputStream,path, StandardCopyOption.REPLACE_EXISTING);

            // Video meta data
            video.setContentType(contentType);
            video.setFilePath(path.toString());

            // Save the video to the database
            Video save = videoRepository.save(video);

            //processing Video
            processVideo(save.getVideoId());

            //delete actual file and database entry  if exception occurs


            return save;


        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Video getVideoById(String videoId) {
        Video video = videoRepository.findByVideoId(videoId).orElseThrow(() -> new RuntimeException("Video Not Found"));
        return video;
    }

    @Override
    public Video getVideoByTitle(String title) {
        return null;
    }

    @Override
    public List<Video> getAllVideos() {

        return videoRepository.findAll();
    }

    @Override
    public void getAll() {

    }

    @Override
    public String processVideo(String videoId) {

        System.out.println("Processing video ......");
        Video video = this.getVideoById(videoId);
        String filePath = video.getFilePath();
        //path where to store data :

        Path videoPath = Paths.get(filePath);
//        String output360p=HLS_DIR+videoId+ "/360p/";
//        String output720p=HLS_DIR+videoId+ "/720p/";
//        String output1080p=HLS_DIR+videoId+ "/1080p/";

        try {
//        Files.createDirectories(Paths.get(output360p));
//        Files.createDirectories(Paths.get(output720p));
//        Files.createDirectories(Paths.get(output1080p));

        // ffmpeg Command
            Path outputPath=Paths.get(HLS_DIR,videoId);
            Files.createDirectories(outputPath);

            String ffmpegCmd = String.format(
                    "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%3d.ts\"  \"%s/master.m3u8\" ",
                    videoPath, outputPath, outputPath
            );



//            StringBuilder ffmpegCmd=new StringBuilder();
//            ffmpegCmd.append("ffmpeg -i")
//                    .append(videoPath.toString())
//                    .append(" -c:v libx264 -c:a aac")
//                    .append(" ")
//                    .append("-map 0:v -map 0:a -s:v:0 640x360 -b:v:0 800k ")
//                    .append("-map 0:v -map 0:a -s:v:1 1280x720 -b:v:1 2800k ")
//                    .append("-map 0:v -map 0:a -s:v:2 1920x1080 -b:v:2 5000k ")
//                    .append("-var_stream_map \"v:0,a:0 v:1,a:0 v:2,a:0\" ")
//                    .append("-master_pl_name ").append(HLS_DIR).append(videoId).append("/master.m3u8 ")
//                    .append("-f hls -hls_time 10 -hls_list_size 0 ")
//                    .append("-hls_segment_filename \"").append(HLS_DIR).append(videoId).append("/v%v/fileSequence%d.ts\" ")
//                    .append("\"").append(HLS_DIR).append(videoId).append("/v%v/prog_index.m3u8\"");

            System.out.println(ffmpegCmd);
            //file this command
            ProcessBuilder processBuilder;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                processBuilder = new ProcessBuilder("cmd.exe", "/c", ffmpegCmd);
            } else {
                processBuilder = new ProcessBuilder("/bin/bash", "-c", ffmpegCmd);
            }
            processBuilder.inheritIO();
            Process process = processBuilder.start();

            int exit = process.waitFor();
            if (exit != 0) {
                throw new RuntimeException("video processing failed!!");
            }

            return videoId;
        } catch (IOException e) {
            throw new RuntimeException("Video Processing Failed");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
