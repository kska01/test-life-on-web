import React, { useState } from 'react';

export default function InputFile({ onChangeFile }) {
  const [dragOver, setDragOver] = useState(false);
  const allowedExtensions = ['dwg', 'jpg', 'jpeg', 'pdf'];

  const isValidExtension = (file) => {
    const fileName = file.name;
    const fileNameSplit = fileName.split('.');

    if (fileNameSplit.length < 2) {
      alert('파일을 확인해 주세요, 파일은 "파일명.확장자" 형태여야 합니다.');
      return false;
    }

    const fileExtension = fileNameSplit[fileNameSplit.length - 1].toLowerCase();
    console.log(allowedExtensions.includes(fileExtension));

    return allowedExtensions.includes(fileExtension);
  };

  const handleFileProcessing = (file) => {
    if (file && isValidExtension(file)) {
      onChangeFile(file);
    } else if (file) {
      alert(
        `지원하지 않는 파일 형식입니다. ${allowedExtensions.join(', ').toUpperCase()} 확장자만 업로드 가능합니다.`,
      );
      onChangeFile(null);
    } else {
      onChangeFile(null);
    }
  };

  const handleDragEnter = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragOver(true);
  };

  const handleDragLeave = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragOver(false);
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (!dragOver) setDragOver(true);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragOver(false);

    if (e.dataTransfer && e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      const droppedFile = e.dataTransfer.files[0];
      handleFileProcessing(droppedFile);
      e.dataTransfer.clearData();
    }
  };

  // 클릭해서 파일 선택하는 경우의 이벤트 핸들러
  const handleChange = (e) => {
    const selectedFile = e.target.files ? e.target.files[0] : null;
    handleFileProcessing(selectedFile);

    if (e.target) {
      e.target.value = ''; // input 값 초기화 (동일 파일 재선택 가능하도록)
    }
  };

  return (
    <section className="flex flex-col flex-grow items-center mx-20 md:mx-50 lg:mx-80 xl:mx-95 mt-12 md:mt-20 lg:mt-24 xl:mt-30">
      <p className="text-xl md:text-2xl lg:text-3xl text-center">
        자, 이제 우리 오피스를 만들어볼까요? 가구, 집기들이 모두 포함된 완성형 공간이 준비되어
        있습니다
      </p>
      <hr className="mt-5 mb-8 border-solid border-gray-300 w-full" />
      <label
        className={`${
          dragOver ? 'border-blue-500 bg-blue-100 text-blue-500 font-semibold' : 'border-gray-300'
        } cursor-pointer`}
        htmlFor="fileUpload"
        onDragEnter={handleDragEnter}
        onDragLeave={handleDragLeave}
        onDragOver={handleDragOver}
        onDrop={handleDrop}
      >
        <section
          onDragOver={handleDragOver}
          onDrop={handleDrop}
          className="flex flex-col items-center"
        >
          <img src="/src/assets/cloud_upload.webp" alt="uploading icon" />
          <p className="text-[20px] text-center mt-3 mx-[50px]">
            이곳에 도면 파일을 끌어놓거나, 내 컴퓨터에서 업로드 할 도면 파일을 선택해주세요
          </p>
          <p className="text-[14px] text-gray-300 mt-9">
            업로드 가능한 파일 포맷 : DWG, JPG, JPEG, PDF
          </p>
          <p className="text-[14px] text-gray-300 mb-9">최대 파일 크기 : 10mb</p>
        </section>
      </label>
      <input id="fileUpload" type="file" className="hidden" onChange={handleChange} />
      <hr className="mt-5 mb-8 border-solid border-gray-300 w-full" />
    </section>
  );
}
