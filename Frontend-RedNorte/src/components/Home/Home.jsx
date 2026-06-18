import React from "react";
import "./home.css";

function Home({ onGoToLogin, onGoToAppointment }) {
  return (
    <div className="home-container">
      <header className="home-header">
        <div className="logo">
          <h1>Hospital RedNorte</h1>
          <p>Tu salud en buenas manos</p>
        </div>
        <div className="header-actions">
          <button
            type="button"
            className="appointment-header-btn"
            onClick={() => onGoToAppointment?.()}
          >
            Agendar cita
          </button>
          <button type="button" className="login-btn" onClick={onGoToLogin}>
            Iniciar sesión
          </button>
        </div>
      </header>

      <section className="hero-section">
        <h2>Nuestros Servicios</h2>
        <p>Conoce las especialidades médicas que ofrecemos para cuidar tu salud.</p>
      </section>

      <section className="services-section">
        <div className="service-grid">
          <button
            type="button"
            className="service-btn"
            onClick={() => onGoToAppointment?.("cardiologia")}
          >
            Cardiología
          </button>
          <button
            type="button"
            className="service-btn"
            onClick={() => onGoToAppointment?.("pediatria")}
          >
            Pediatría
          </button>
          <button
            type="button"
            className="service-btn"
            onClick={() => onGoToAppointment?.("dermatologia")}
          >
            Dermatología
          </button>
          <button
            type="button"
            className="service-btn"
            onClick={() => onGoToAppointment?.("ginecologia")}
          >
            Ginecología
          </button>
          <button
            type="button"
            className="service-btn"
            onClick={() => onGoToAppointment?.("medicina-general")}
          >
            Medicina General
          </button>
          <button
            type="button"
            className="service-btn"
            onClick={() => onGoToAppointment?.("neurologia")}
          >
            Neurología
          </button>
          <button
            type="button"
            className="service-btn"
            onClick={() => onGoToAppointment?.("oftalmologia")}
          >
            Oftalmología
          </button>
          <button
            type="button"
            className="service-btn"
            onClick={() => onGoToAppointment?.("psiquiatria")}
          >
            Psiquiatría
          </button>
          <button
            type="button"
            className="service-btn"
            onClick={() => onGoToAppointment?.("otorrinolaringologia")}
          >
            Otorrinolaringología
          </button>
          <button
            type="button"
            className="service-btn"
            onClick={() => onGoToAppointment?.("cirugia-general")}
          >
            Cirugía General
          </button>
        </div>
      </section>

      <footer className="home-footer">
        <p>© 2026 Hospital RedNorte — Todos los derechos reservados</p>
      </footer>
    </div>
  );
}

export default Home;
