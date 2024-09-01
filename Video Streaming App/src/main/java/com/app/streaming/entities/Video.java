package com.app.streaming.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name="yt_videos")
public class Video {
    @Id
    private String videoId;
    private String title;
    private String description;
    private String filePath;
//    private String thumbnailUrl;
    private String contentType;
//    @ManyToOne
//    private Course course;

}
