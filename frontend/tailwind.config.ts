/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,js,ts,jsx,tsx}", // adjust to your project structure
  ],
  theme: {
    extend: {
      fontFamily: {
        mono: ['"JetBrains Mono"', "monospace"],
      },
      colors: {
        primary_dark: "#0B0B0B",
        white_primary: "#c4c4c4",
        white_secondary: "#9c9c9c",
        black_primary: "#0B0B0B",
        secondary: "#2E2E2E",
        secondary_dark: "#262626",
        secondary_light: "#454545",
      },
      screens: {
        small: "500px",
      },
    },
  },
  plugins: [],
};
