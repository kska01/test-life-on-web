import React from 'react';
import { Link } from 'react-router-dom';

export default function Header() {
  return (
    <>
      <Link to="/">
        <img src="src/assets/lifeon-logo.jpg" alt="lifeOn logo" className="pt-[2rem] px-[2rem]" />
      </Link>
    </>
  );
}
