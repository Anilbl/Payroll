import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App.jsx'; // This imports the App component
import './index.css'; // Global CSS if you have any

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App /> {/* The App component is rendered here */}
  </React.StrictMode>,
);