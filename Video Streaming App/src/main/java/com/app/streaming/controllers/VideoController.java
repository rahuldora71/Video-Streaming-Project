package com.app.streaming.controllers;

import com.app.streaming.AppConstants;
import com.app.streaming.entities.Video;
import com.app.streaming.payload.CustomMessage;
import com.app.streaming.services.ServiceImple.VideoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/videos")
@CrossOrigin("*")
public class VideoController {

    @Autowired
    private VideoServiceImpl videoService;


    // Upload video
    @PostMapping
    public ResponseEntity<?> create(
            @RequestParam("file")MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description
    ){

        Video video=new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setVideoId(UUID.randomUUID().toString());

        System.out.println(video);
        Video saveVideo = videoService.saveVideo(video, file);

        System.out.println(saveVideo.getFilePath());
        if (saveVideo != null) {
            return ResponseEntity.ok(saveVideo);
        }else {
            return ResponseEntity.ok(new CustomMessage("Video Upload Failed", false));
        }


    }

    // Get All Video
    // http://localhost:8080/api/v1/videos
    @GetMapping()
    public List<Video> getAllVideos() {
        return videoService.getAllVideos();
    }

    //Stream Videos

    // http://localhost:8080/api/v1/videos/stream/64569069-169f-469f-9b32-809324932493
    @GetMapping("/stream/{videoId}")
    public ResponseEntity<Resource> stream(
            @PathVariable("videoId") String videoId
    ){
        Video video = videoService.getVideoById(videoId);

        String contentType = video.getContentType();
        String filePath = video.getFilePath();
        Resource resource = new FileSystemResource(filePath);
        if (contentType==null){
            contentType="application/octet-stream";
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
    
    //Stream Videos in Chunks
    @GetMapping("/stream/range/{videoId}")
    public ResponseEntity<Resource> streamVideoRange(
            @PathVariable("videoId") String videoId,
            @RequestHeader(value="Range",required = false) String range
            
    ){
        System.out.println(range);
        Video  video = videoService.getVideoById(videoId);
        Path path= Paths.get(video.getFilePath());
        Resource resource = new FileSystemResource(path);
        String contentType = video.getContentType();
        if (contentType==null){
            contentType = "application/octet-stream";

        }

        //File length
        long fileLength = path.toFile().length();

        if (range==null){
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        }

        long rangeStart;
        long rangeEnd ;
        String[] ranges = range.replace("bytes=", "").split("-");
        rangeStart = Long.parseLong(ranges[0]);

        rangeEnd=rangeStart+ AppConstants.CHUNK_SIZE;
        if (rangeEnd>=fileLength){
            rangeEnd = fileLength - 1;
        }
//        if (ranges.length > 1) {
//            rangeEnd = Long.parseLong(ranges[1]);
//        } else {
//            rangeEnd = fileLength - 1;
//        }
//
//        if (rangeEnd>fileLength - 1) {
//            rangeEnd = fileLength - 1;
//        }

        System.out.println("Start Range"+rangeStart);
        System.out.println("Start End"+rangeEnd);
        InputStream inputStream;
        try {

            inputStream = Files.newInputStream(path);
            long skip = inputStream.skip(rangeStart);
            long contentLength = rangeEnd - rangeStart + 1;

            byte[]data=new byte[(int)contentLength];
            int read = inputStream.read(data, 0, data.length);
            System.out.println("reade no of bytes "+read);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Range","bytes "+rangeStart+"-"+rangeEnd+"/"+fileLength);
            headers.add("Cache-Control","no-cache , no-store, must-revalidate");
            headers.add("Pragma","no-cache");
            headers.add("Expires","0");
            headers.add("X-Content-Type-Options","nosniff");

            headers.setContentLength(contentLength);
            return ResponseEntity
                    .status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(new ByteArrayResource(data));

        }catch (IOException e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

     }

     // Serve HLS segments Playlist

    @GetMapping("/{videoId}/{segment}.ts")
    public ResponseEntity<Resource> serveSegment(
            @PathVariable("videoId") String videoId,
            @PathVariable("segment") String segment
    ){
        //Create path for segment
        Path path = Paths.get(HSL_DIR, videoId, segment + ".ts");
        if (!Files.exists(path)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Resource resource = new FileSystemResource(path);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, "video/MP2T")
                .body(resource);
        
    }

    //master.m3u8 file

    @Value("${files.video.hls}")
    private String HSL_DIR;
    @GetMapping("/{videoId}/master.m3u8")
    public  ResponseEntity<Resource> serveMasterFile(
            @PathVariable("videoId") String videoId
    ) {

        //Creating path
        Path path = Paths.get(HSL_DIR, videoId, "master.m3u8");
        System.out.println(path);
        if (!Files.exists(path)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        Resource resource=new FileSystemResource(path);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE,"application/vnd.apple.mpegurl")
                .body(resource);

    }

}
