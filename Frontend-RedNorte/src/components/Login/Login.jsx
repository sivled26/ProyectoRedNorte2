import React, { useState } from "react";
import "./login.css";
import { login } from "../../api/api";

function Login({ onLogin, onBack, onForgotPassword, onRegister, message = "" }) {
  const [user, setUser] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!user.trim() || !password.trim()) {
      alert("Debes ingresar usuario y contraseña para iniciar sesión.");
      return;
    }

    try {
      const data = await login(user, password); // 👈 usamos la función importada
      console.log("Token recibido:", data.token);
      onLogin();
    } catch (error) {
      alert("Credenciales incorrectas o error en el servidor.");
    }
  };

  return (
    <div className="login-container">
      <button type="button" className="page-back-btn" onClick={onBack}>
        ← Volver atrás
      </button>
      <div className="login-box">
        <h2>Hospital RedNorte</h2>
        {message ? <p className="login-message">{message}</p> : null}
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Correo electrónico"
            value={user}
            onChange={(e) => setUser(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Contraseña"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button type="submit" className="login-btn">
            Iniciar sesión
          </button>
        </form>

        <p className="forgot-password" onClick={onForgotPassword}>
          ¿Olvidaste tu contraseña?
        </p>

        <button type="button" className="register-btn" onClick={onRegister}>
          Crear cuenta nueva
        </button>
      </div>
    </div>
  );
}

export default Login;
