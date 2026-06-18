import React, { useState } from "react";
import Home from "./components/Home/Home";
import Login from "./components/Login/Login";
import Register from "./components/Register/Register";
import Dashboard from "./components/Dashboard/Dashboard";
import Appointment from "./components/Appointment/Appointment";

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [showLogin, setShowLogin] = useState(false);
  const [showRegister, setShowRegister] = useState(false);
  const [showAppointment, setShowAppointment] = useState(false);
  const [appointmentSpecialtyId, setAppointmentSpecialtyId] = useState(null);
  const [appointmentIntent, setAppointmentIntent] = useState(false);
  const [loginMessage, setLoginMessage] = useState("");

  const goHome = () => {
    setShowLogin(false);
    setShowRegister(false);
    setShowAppointment(false);
    setAppointmentSpecialtyId(null);
    setAppointmentIntent(false);
    setLoginMessage("");
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    goHome();
  };

  const handleForgotPassword = () => {
    alert("Funcionalidad de recuperación de contraseña en construcción.");
  };

  const handleGoToLogin = () => {
    setAppointmentIntent(false);
    setLoginMessage("");
    setShowRegister(false);
    setShowLogin(true);
  };

  const handleGoToAppointment = (specialtyId = null) => {
    if (!isAuthenticated) {
      setAppointmentSpecialtyId(specialtyId);
      setAppointmentIntent(true);
      setLoginMessage("Debes iniciar sesión para agendar una cita médica.");
      setShowRegister(false);
      setShowLogin(true);
      setShowAppointment(false);
      return;
    }
    setAppointmentSpecialtyId(specialtyId);
    setShowAppointment(true);
  };

  const handleLoginSuccess = () => {
    setIsAuthenticated(true);
    setShowLogin(false);
    setLoginMessage("");

    if (appointmentIntent) {
      setAppointmentIntent(false);
      setShowAppointment(true);
    } else {
      setShowAppointment(false);
    }
  };

  return (
    <div className="app-container">
      {isAuthenticated && showAppointment ? (
        <Appointment
          initialSpecialtyId={appointmentSpecialtyId}
          onBack={() => {
            setShowAppointment(false);
          }}
        />
      ) : isAuthenticated ? (
        <Dashboard
          onBack={handleLogout}
          onGoToAppointment={() => handleGoToAppointment(null)}
        />
      ) : showRegister ? (
        <Register
          onBack={() => {
            setShowRegister(false);
            setShowLogin(true);
          }}
           onLogin={handleLoginSuccess}
        />
      ) : showLogin ? (
        <Login
          message={loginMessage}
          onLogin={handleLoginSuccess}
          onBack={goHome}
          onForgotPassword={handleForgotPassword}
          onRegister={() => {
            setShowLogin(false);
            setShowRegister(true);
          }}
        />
      ) : (
        <Home
          onGoToLogin={handleGoToLogin}
          onGoToAppointment={handleGoToAppointment}
        />
      )}
    </div>
  );
}

export default App;
