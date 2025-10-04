/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,js,ts,jsx,tsx}", // adjust to your project structure
  ],
  theme: {
    extend: {
      fontFamily: {
        mono: ['"JetBrains Mono"', 'monospace'],
      },
      colors: {
        primary_dark: "#0B0B0B",
        secondary: "#2E2E2E",
        secondary_dark: "#262626",
        secondary_light: "#454545",
      },
    },
  },
  plugins: [],
}
