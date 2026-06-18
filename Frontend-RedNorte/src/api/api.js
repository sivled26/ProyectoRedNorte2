import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/bff", // tu BFF
});

// Interceptor: agrega token automáticamente si existe
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  // No agregar token en registro/login
  if (
    token &&
    !config.url.includes("/pacientes/registro") &&
    !config.url.includes("/pacientes/login")
  ) {
    config.headers.Authorization = `Bearer ${token}`;
    console.log("Token enviado:", config.headers.Authorization);
  }
console.log("URL llamada:", config.url);
  return config;
});

// Login
export const login = async (correo, contrasena) => {
  const response = await api.post("/pacientes/login", { correo, contrasena });
  localStorage.setItem("token", response.data.token); // guardar token
  localStorage.setItem("correo", correo);             // guardar correo del paciente
  return response.data;
};

// Registro
export const registro = async (nuevoPaciente) => {
  const response = await api.post("/pacientes/registro", nuevoPaciente);
  return response.data;
};

// Registro + login automático
export const registroYLogin = async (nuevoPaciente) => {
  localStorage.removeItem("token"); // limpiar token viejo
  await registro(nuevoPaciente);
  const loginResponse = await login(nuevoPaciente.correo, nuevoPaciente.contrasena);
  return loginResponse;
};

// Agendar cita
export const agendarCita = async (cita) => {
  const response = await api.post("/citas/agendar", cita);
  return response.data;
};
