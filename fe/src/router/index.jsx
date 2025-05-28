import { createBrowserRouter } from "react-router-dom";
import Home from "../pages/Home";
import Upload from "../pages/Upload";
import RootLayout from "../RootLayout";

const router = createBrowserRouter([
  {
    path: "/",
    element: <RootLayout />,
    children: [
      {
        index: true,
        element: <Home />,
      },
      {
        path: "/upload",
        element: <Upload />,
      },
    ],
  },
]);

export default router;