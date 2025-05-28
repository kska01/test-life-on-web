import React, { useEffect, useState } from 'react';
import InputFile from '../components/InputFile';
import fileUploadApi from '../api/fileUploadApi';

export default function Upload() {
  const [uploadFile, setUploadFile] = useState(null);

  const handleFileSelect = (selectedFile) => {
    setUploadFile(selectedFile);
    console.log('업로드된 파일:', uploadFile);
  };

  useEffect(() => {
    if (uploadFile) {
      handleUpload();
    }
  }, [uploadFile]);

  const handleUpload = async () => {
    const formData = new FormData();
    formData.append('planFile', uploadFile);

    try {
      const response = await fileUploadApi.uploadFile(formData);
      console.log(response.data);
    } catch (error) {
      console.log(error);
      
    }
  };

  return (
    <>
      <InputFile onChangeFile={handleFileSelect}></InputFile>
    </>
  );
}
