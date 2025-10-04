//Import components
import { RouterProvider, createBrowserRouter } from "react-router-dom";
import { Home, Map } from "./pages";

const Router = () => {
    const loading = false;

    // Routing configuration
    const routing = createBrowserRouter(
        [
            {
                path: "/",
                element: <Home />,
            },
            {
                path: "/map",
                element: <Map />,
            },
        ],
    );

    return loading ? <p></p> : <RouterProvider router={routing} />;
};

export default Router