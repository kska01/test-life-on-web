import React, { useEffect, useState } from 'react';
import InputFile from '../components/InputFile';
import fileUploadApi from '../api/fileUploadApi';

export default function Upload() {
  const [uploadFile, setUploadFile] = useState(null);
  const [isLoading, setIsLoading] = useState(null);
  const [result, setResult] = useState(null);

  const handleFileSelect = (selectedFile) => {
    setUploadFile(selectedFile);
  };

  useEffect(() => {
    if (uploadFile) {
      console.log('업로드된 파일:', uploadFile);
      handleUpload();
    }
  }, [uploadFile]);

  const handleUpload = async () => {
    const formData = new FormData();
    formData.append('planFile', uploadFile);
    setIsLoading(true);

    try {
      const response = await fileUploadApi.uploadFile(formData);
      console.log(response.data);
    } catch (error) {
      console.log(error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      {isLoading ? (
        <section className="flex flex-col flex-grow items-center mx-20 md:mx-50 lg:mx-80 xl:mx-95 mt-50">
          <img className="h-64 w-auto" src="/src/assets/image_68.webp" alt="loading"></img>
          <p>공간을 분석하고 있어요</p>
          <p className="text-[14px] text-gray-300 mt-9">
            업로드된 파일이름 : {uploadFile.name}
          </p>
        </section>
      ) : (
        <InputFile onChangeFile={handleFileSelect}></InputFile>
      )}
    </>
  );
}
