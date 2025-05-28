import React from 'react';
import { Link } from 'react-router-dom';

export default function Header() {
  return (
    <>
      <Link to="/">
        <img src="src/assets/life-on-logo.jpg" alt="lifeOn logo" className="pt-[2rem] px-[2rem] w-65 md:w-75 lg:w-85 xl:w-100" />
      </Link>
    </>
  );
}