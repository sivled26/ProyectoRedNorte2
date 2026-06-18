import React, { useState } from "react";
import "./register.css";
import { registroYLogin } from "../../api/api";

function Register({ onBack, onLogin }) {
  const [formData, setFormData] = useState({
    email: "",
    name: "",
    lastname: "",
    password: "",
    run: "",
    birthdate: "",
    address: "",
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // 👇 mapeamos los nombres del frontend a los del backend
      const payload = {
        correo: formData.email,
        nombre: formData.name,
        apellido: formData.lastname,
        contrasena: formData.password,
        run: formData.run,
        fecha_nacimiento: formData.birthdate,
        direccion: formData.address,
      };

      const result = await registroYLogin(payload); // axios al BFF
      alert("Cuenta creada y sesión iniciada con éxito");
      console.log("Token:", result.token);
      onLogin();
    } catch (error) {
      alert("Error en el registro");
      console.error(error);
    }
  };

  return (
    <div className="register-container">
      <button type="button" className="page-back-btn" onClick={onBack}>
        ← Volver atrás
      </button>
      <div className="register-box">
        <h2>Crear cuenta nueva</h2>
        <form onSubmit={handleSubmit}>
          <input type="email" name="email" placeholder="Correo electrónico" onChange={handleChange} required />
          <input type="text" name="name" placeholder="Nombre" onChange={handleChange} required />
          <input type="text" name="lastname" placeholder="Apellido" onChange={handleChange} required />
          <input type="password" name="password" placeholder="Contraseña" onChange={handleChange} required />
          <input type="text" name="run" placeholder="RUN" onChange={handleChange} required />
          <input type="date" name="birthdate" onChange={handleChange} required />
          <input type="text" name="address" placeholder="Dirección" onChange={handleChange} required />
          <button type="submit" className="primary-btn">Registrar</button>
        </form>
        <button type="button" className="secondary-btn" onClick={onBack}>
          Volver al login
        </button>
      </div>
    </div>
  );
}

export default Register;
