//Import components
import { RouterProvider, createBrowserRouter } from "react-router-dom";
import Home from "./pages/home/Home";

const Router = () => {
    const loading = false;

    // Routing configuration
    const routing = createBrowserRouter(
        [
            {
                path: "/",
                element: <Home />,
            },
        ],
    );

    return loading ? <p></p> : <RouterProvider router={routing} />;
};

export default Router