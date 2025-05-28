import React from "react";
import Header from "./components/Header";
import Footer from "./components/Footer";
import { Outlet } from "react-router-dom";

export default function RootLayout() {
  return (
    <section className="flex flex-col min-h-screen">
      <Header></Header>
      <Outlet></Outlet>
      <Footer></Footer>
    </section>
  );
}