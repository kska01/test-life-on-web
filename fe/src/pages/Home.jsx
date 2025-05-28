import React from 'react';
import { useNavigate } from 'react-router-dom';

export default function Home() {
  const navigate = useNavigate();

  const handleButtonClick = (e) => {
    console.log('click');
    navigate('/upload');
  };

  return (
      <main className="flex flex-col flex-grow gap-30 mt-50 items-center text-center">
        <h1 className="text-6xl">turn on your office life</h1>
        <button onClick={handleButtonClick} 
        className="text-4xl underline underline-offset-8 hover:text-yellow-500 p-2 cursor-pointer">
          upload file
        </button>
      </main>
  );
}
