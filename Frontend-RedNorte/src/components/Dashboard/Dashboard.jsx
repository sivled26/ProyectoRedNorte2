import React from "react";
import "./dashboard.css";

function Dashboard({ onBack, onGoToAppointment }) {
  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="dashboard-header-left">
          <h1>Hospital RedNorte</h1>
          <p>Panel de paciente</p>
        </div>
        <button type="button" className="page-back-btn" onClick={onBack}>
          ← Volver atrás
        </button>
      </header>

      <main className="dashboard-main">
        <h2>Bienvenido al sistema</h2>
        <p>Gestiona tus citas médicas desde aquí.</p>
        <button type="button" className="dashboard-appointment-btn" onClick={onGoToAppointment}>
          Agendar cita médica
        </button>
      </main>

      <footer className="dashboard-footer">
        <p>© 2026 Hospital RedNorte — Todos los derechos reservados</p>
      </footer>
    </div>
  );
}

export default Dashboard;
