
# Video Streaming App

This project is a robust video streaming platform built using Spring Boot for the backend and Vite React for the frontend. It allows users to upload, stream, and watch videos efficiently. The backend handles video processing, storage, and retrieval, ensuring smooth playback by implementing HLS (HTTP Live Streaming) for video delivery. Videos are stored in multiple quality formats, enabling adaptive streaming based on the user's network conditions. 


## Tech Stack

**Backend:**
 
 **Spring Boot:** For building the RESTful APIs and handling the backend logic.
- **Java:** The primary programming language for the backend development.
- **Hibernate/JPA:** For ORM (Object-Relational Mapping) and database interactions.
- **MySQL/PostgreSQL:** As the relational database for storing video metadata and user information.
- **ffmpeg:** For video processing, including transcoding and segmenting videos into HLS format.
- **Maven:** For managing project dependencies and building the application.

**Frontend:** 
- **Vite:** A modern frontend build tool for fast development and optimized production builds.
- **React:** For building the interactive user interface.
- **Tailwind CSS:** For styling the frontend with a utility-first approach.
- **JavaScript:** For scripting and adding interactivity to the frontend.

**Other Tools and Technologies:**
- **HLS (HTTP Live Streaming):** For adaptive bitrate streaming and efficient video delivery.
- **RESTful APIs:** For communication between the frontend and backend.
- **Git/GitHub:** For version control and collaboration.
## API Reference

#### Upload Video

```http
  POST /api/v1/videos
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `video_file, title, description ` | `Multipart, String, String` | **Required** All Fields |

#### Get All Videos

```http
  GET /api/v1/videos
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `none`      | `none` | It returns all Videos in the videos present  with title , description , file Path, etc |

#### Stream Videos

```http
  GET /api/v1/videos/stream/{videoId}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `videoId`      | `String` | It return a video resource which load at once  |

#### Stream Videos

```http
  GET /api/v1/videos/stream/range/{videoId}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `videoId`      | `String` | It return a video resource but it sent video in chunks not at once  |

#### Stream Videos

```http
  GET /api/v1/videos/{videoId}/master.m3u8
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `videoId`      | `String` | It return a HLS master.m3u8 file which has information about video segments  |

#### Fetch Video Segments

```http
  GET /api/v1/videos/{videoId}/{segment}.ts
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `videoId, segment`      | `String,String` | It return a video segment|



## Key features include:

- Video Uploading and Management: Users can upload videos, which are then processed and stored in a structured directory. Each video is assigned a unique identifier and saved with metadata, including title, description, and content type.
- HLS Streaming: The platform uses ffmpeg to transcode uploaded videos into HLS format, segmenting them into smaller chunks for efficient delivery. It also serves the master playlist and individual segments to ensure smooth streaming.
- Range Requests: The application supports byte-range requests, allowing videos to be streamed in chunks. This feature is crucial for fast-forwarding, rewinding, and buffering large videos.
- Cross-Origin Resource Sharing (CORS): Configured to allow seamless interaction between the frontend and backend, ensuring smooth API calls and resource access.

The frontend, built with Vite React, provides a responsive and intuitive user interface, enabling users to interact with the platform smoothly. Tailwind CSS is used for styling, ensuring a modern and consistent look across different devices.

This project demonstrates a full-stack approach to building scalable video streaming applications, integrating video processing, storage, and real-time streaming capabilities.


![Screenshot 2024-09-01 192501](https://github.com/user-attachments/assets/4dc41378-361a-4a5e-8de7-4d9e01a36d81)
![Screenshot (339)](https://github.com/user-attachments/assets/47831679-ea84-455c-9469-3d35179df3e6)
