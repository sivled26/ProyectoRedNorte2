import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import App from './App';

// --- TRUCO PARA EL 60%+: SIMULAMOS LOS COMPONENTES HIJOS ---
// Esto evita errores y asegura que Jest se enfoque en la lógica de App.jsx
jest.mock('./components/Home/Home', () => ({ onGoToLogin, onGoToAppointment }) => (
  <div data-testid="home-view">
    <h1>Vista Home</h1>
    <button onClick={onGoToLogin}>Ir a Login</button>
    <button onClick={() => onGoToAppointment(1)}>Agendar Cita sin Auth</button>
  </div>
));

jest.mock('./components/Login/Login', () => ({ onLogin, onBack, onRegister, message }) => (
  <div data-testid="login-view">
    <h1>Vista Login</h1>
    {message && <p>{message}</p>}
    <button onClick={onLogin}>Iniciar Sesion Exitoso</button>
    <button onClick={onRegister}>Ir a Registro</button>
    <button onClick={onBack}>Volver a Home</button>
  </div>
));

jest.mock('./components/Register/Register', () => ({ onBack, onLogin }) => (
  <div data-testid="register-view">
    <h1>Vista Registro</h1>
    <button onClick={onBack}>Volver a Login</button>
  </div>
));

jest.mock('./components/Dashboard/Dashboard', () => ({ onBack, onGoToAppointment }) => (
  <div data-testid="dashboard-view">
    <h1>Vista Dashboard</h1>
    <button onClick={onGoToAppointment}>Agendar Cita Autenticado</button>
    <button onClick={onBack}>Cerrar Sesion</button>
  </div>
));

jest.mock('./components/Appointment/Appointment', () => ({ onBack }) => (
  <div data-testid="appointment-view">
    <h1>Vista Citas Médicas</h1>
    <button onClick={onBack}>Volver desde Citas</button>
  </div>
));

// --- EMPIEZAN LAS PRUEBAS ---

describe('Pruebas de Frontend y Carga para App.jsx', () => {

  // 1. PRUEBA DE CARGA DE PÁGINA
  test('Debería cargar la página inicial (Home) por defecto', () => {
    render(<App />);
    
    // Verifica que la carga inicial renderice el componente Home
    expect(screen.getByTestId('home-view')).toBeInTheDocument();
    expect(screen.getByText('Vista Home')).toBeInTheDocument();
  });

  // 2. PRUEBA DE NAVEGACIÓN Y FLUJO (Cosas del Frontend)
  test('Debería navegar al Login y luego al Registro correctamente', () => {
    render(<App />);

    // 1. Clic en "Ir a Login" desde el Home
    fireEvent.click(screen.getByText('Ir a Login'));
    expect(screen.getByTestId('login-view')).toBeInTheDocument();

    // 2. Clic en "Ir a Registro" desde el Login
    fireEvent.click(screen.getByText('Ir a Registro'));
    expect(screen.getByTestId('register-view')).toBeInTheDocument();

    // 3. Volver al Login desde el Registro
    fireEvent.click(screen.getByText('Volver a Login'));
    expect(screen.getByTestId('login-view')).toBeInTheDocument();
  });

  // 3. PRUEBA DE FLUJO COMPLETO (Sube drásticamente el coverage)
  test('Debería permitir iniciar sesión, ver el Dashboard, e ir a Citas', () => {
    render(<App />);

    // Ir a login e iniciar sesión
    fireEvent.click(screen.getByText('Ir a Login'));
    fireEvent.click(screen.getByText('Iniciar Sesion Exitoso'));

    // Debería estar en el Dashboard
    expect(screen.getByTestId('dashboard-view')).toBeInTheDocument();

    // Ir a Agendar Cita
    fireEvent.click(screen.getByText('Agendar Cita Autenticado'));
    expect(screen.getByTestId('appointment-view')).toBeInTheDocument();

    // Volver al Dashboard
    fireEvent.click(screen.getByText('Volver desde Citas'));
    expect(screen.getByTestId('dashboard-view')).toBeInTheDocument();

    // Cerrar Sesión
    fireEvent.click(screen.getByText('Cerrar Sesion'));
    expect(screen.getByTestId('home-view')).toBeInTheDocument();
  });

  // 4. PRUEBA DE INTENTO DE AGENDAR SIN ESTAR AUTENTICADO
  test('Debería exigir login si se intenta agendar una cita sin estar autenticado', () => {
    render(<App />);

    // Intentar agendar desde el Home
    fireEvent.click(screen.getByText('Agendar Cita sin Auth'));

    // Debe mandarlo al Login con el mensaje de advertencia
    expect(screen.getByTestId('login-view')).toBeInTheDocument();
    expect(screen.getByText('Debes iniciar sesión para agendar una cita médica.')).toBeInTheDocument();
  });
});