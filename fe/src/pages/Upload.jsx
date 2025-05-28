import React from 'react';

export default function Upload() {
  return (
    <section className="flex flex-col flex-grow items-center mx-20 md:mx-50 lg:mx-80 xl:mx-95 mt-12 md:mt-20 lg:mt-24 xl:mt-30">
      <p className="text-xl md:text-2xl lg:text-3xl text-center">
        자, 이제 우리 오피스를 만들어볼까요? 가구, 집기들이 모두 포함된 완성형 공간이 준비되어
        있습니다
      </p>
      <hr className="mt-5 mb-8 border-solid border-gray-300 w-full" />
      <img src="/src/assets/cloud_upload.webp" alt="uploading icon" />
      <p className="text-[20px] text-center mt-3 mx-[50px]">
        이곳에 도면 파일을 끌어놓거나, 내 컴퓨터에서 업로드 할 도면 파일을 선택해주세요
      </p>
      <p className="text-[14px] text-gray-300 mt-9">
        업로드 가능한 파일 포맷 : DWG, JPG, JPEG, PDF
      </p>
      <p className="text-[14px] text-gray-300 mb-9">최대 파일 크기 : 10mb</p>
      <hr className="mt-5 mb-8 border-solid border-gray-300 w-full" />
    </section>
  );
}
