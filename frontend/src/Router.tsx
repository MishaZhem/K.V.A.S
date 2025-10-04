//Import components
import { RouterProvider, createBrowserRouter } from "react-router-dom";
import Home from "./pages/Home";
import DriverLoginWidget from "./components/DriverLogin";
import { useState } from "react";
import type { UserContextType } from "./types/userContext";
import { Map } from "./pages";

const Router = () => {
  const loading = false;
  const [userContext, setUserContext] = useState<UserContextType | undefined>(undefined);
  // Routing configuration
  const routing = createBrowserRouter(
    [
      {
        path: "/",
        element: <DriverLoginWidget updateUserContext={(c) => setUserContext(c)} />,
      },
      {
        path: "/app",
        element: <Map user={userContext} />
      }
    ],
  );
  if (userContext) {
    return <Map user={userContext} />
  } else {
    return <DriverLoginWidget updateUserContext={(c) => setUserContext(c)} />
  }
  // return loading ? <p></p> : <RouterProvider router={routing} />;
};

export default Router
