package com.app.streaming.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "yt_courses")
public class Course {
    @Id
    private String courseId;
    private String title;
    private String description;

//    @OneToMany(mappedBy = "course")
//    private List<Video> videoList=new ArrayList<>();

}
