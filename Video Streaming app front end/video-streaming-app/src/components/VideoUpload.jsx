import React, { useState } from 'react'
import videoLogo from "../assets/video-posting.png"
import { Button, Progress } from "flowbite-react";
import { Textarea, Label, TextInput } from "flowbite-react";
import { Card, Alert } from 'flowbite-react';
import axios from 'axios';
import toast from 'react-hot-toast';



function VideoUpload() {
  const [selectedFile, setSelectedFile] = useState(null);
  const [progress, setProgress] = useState(0);
  const [uploading, setUploading] = useState(false);
  const [message, setMessage] = useState("");
  const [meta, setMeta] = useState({
    title: '',
    description: '',
  })

  function handleFileChange(event) {
    console.log(event.target.files[0]);
    setSelectedFile(event.target.files[0])

  }


  function formFieldChange(event) {

    // console.log(event.target.name);
    // console.log(event.target.value);


    setMeta({
      ...meta,
      [event.target.name]: event.target.value
    })
  }

  function handleForm(formEvent) {

    formEvent.preventDefault();
    if (!selectedFile) {
      alert("Select file")
    }

    // Submit file ot server
    saveVideoToServer(selectedFile, meta)



  }

function resetForm(){
  setMeta({

    title:"",
    description:"",

  });
  setSelectedFile(null)
  setUploading(false)
  // setMessage("")
}


  async function saveVideoToServer(video, videoMetaData) {

    setUploading(true)
    // api call

    try {

      let formData = new FormData
      formData.append("title", videoMetaData.title)
      formData.append("description", videoMetaData.description)
      formData.append("file", selectedFile)
      const response = await axios.post(`http://localhost:8080/api/v1/videos`, formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          onUploadProgress: (ProgressEvent) => {
            const progress = Math.round((ProgressEvent.loaded * 100) / ProgressEvent.total);
            // console.log(progress)
            setProgress(progress)
          },

        });
      console.log(response);
        setProgress(0)
      setMessage("File Uploaded "+response.data.videoId)
      resetForm();
      toast.success("FileUploaded succesfully")

    } catch (error) {
      console.log(error);
      setUploading(false)
      setMessage("Error in Uploading file")
      toast.error("File not uploaded !")



    }
  }

  return <div className='text-white'>

    <Card className='flex flex-col items-center justify-center    '>
      <h1>
        Upload Video
      </h1>
      <form noValidate className='space-y-7' onSubmit={handleForm} >

        <div>
          <div className="mb-2 block">
            <Label htmlFor="title" value="Video Title" />
          </div>
          <TextInput
          value={meta.title} onChange={formFieldChange} id="titel" name='title' type="text" placeholder='Enter title' required />
        </div>

        <div className="max-w-md">
          <div className="mb-2 block">
            <Label htmlFor="description" value="Video Description" />
          </div>
          <Textarea 
          value={meta.description} onChange={formFieldChange} id="description" name='description' placeholder="Write Video Description..." required rows={4} />
        </div>



        <div className="flex  items-center space-x-6">


          <div className="shrink-0">
            <img className="h-16 w-16 object-cover "
              src={videoLogo} alt="Current profile photo" />
          </div>
          <label className="block">
            <span className="sr-only">Choose video file</span>
            <input  onChange={handleFileChange} name='file' type="file" className="block w-full text-sm text-slate-500
      file:mr-4 file:py-2 file:px-4
      file:rounded-full file:border-0
      file:text-sm file:font-semibold
      file:bg-violet-50 file:text-violet-700
      hover:file:bg-violet-100
      "/>
          </label>

        </div>
        <div className=' '>
        {uploading &&(

          <Progress
            // hidden={uploading}
            progress={progress}
            textLabel="Uploading..."
            size="lg"
            labelProgress
            labelText
          />
        )}
        </div>
        <div className=' '>

          {message &&(
          <Alert
            color="success"
            rounded
            withBorderAccent
            onDismiss={() => setMessage("")}
          >
            <span className="font-medium">Success alert! </span>
            {message}
          </Alert>
          )}
        </div>
        <div className=' flex justify-center'>

          <Button disabled={uploading} type='submit' outline gradientDuoTone="pinkToOrange">
            Upload
          </Button>
        </div>
      </form>
    </Card>
  </div>

}

export default VideoUpload;
