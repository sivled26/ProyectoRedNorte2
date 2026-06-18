import React, { useMemo, useRef, useState } from "react";
import "./appointment.css";
import { agendarCita } from "../../api/api";

const handleAgendar = async () => {
  try {
    const payload = {
      pacienteCorreo: localStorage.getItem("correo"), // o del token
      doctorNombre: doctor.name,
      fecha: selectedDate.toISOString().split("T")[0], // formato YYYY-MM-DD
      hora: selectedTime,
      motivo: specialty.name,
    };

    const result = await agendarCita(payload);
    alert("Cita agendada con éxito");
    console.log(result);
  } catch (error) {
    alert("Error al agendar cita");
    console.error(error);
  }
};


const SPECIALTIES = [
  {
    id: "cardiologia",
    name: "Cardiología",
    doctors: [
      { id: "card-1", name: "Dr. Andrés Morales" },
      { id: "card-2", name: "Dra. Camila Rojas" },
    ],
  },
  {
    id: "pediatria",
    name: "Pediatría",
    doctors: [
      { id: "ped-1", name: "Dra. Valentina Soto" },
      { id: "ped-2", name: "Dr. Felipe Herrera" },
    ],
  },
  {
    id: "dermatologia",
    name: "Dermatología",
    doctors: [
      { id: "der-1", name: "Dra. Francisca Lagos" },
      { id: "der-2", name: "Dr. Tomás Araya" },
    ],
  },
  {
    id: "ginecologia",
    name: "Ginecología",
    doctors: [
      { id: "gin-1", name: "Dra. Marcela Pizarro" },
      { id: "gin-2", name: "Dr. Ignacio Vega" },
    ],
  },
  {
    id: "medicina-general",
    name: "Medicina General",
    doctors: [
      { id: "mg-1", name: "Dr. Pablo Contreras" },
      { id: "mg-2", name: "Dra. Daniela Muñoz" },
    ],
  },
  {
    id: "neurologia",
    name: "Neurología",
    doctors: [
      { id: "neu-1", name: "Dr. Ricardo Fuentes" },
      { id: "neu-2", name: "Dra. Carolina Espinoza" },
    ],
  },
  {
    id: "oftalmologia",
    name: "Oftalmología",
    doctors: [
      { id: "oft-1", name: "Dr. Sebastián Bravo" },
      { id: "oft-2", name: "Dra. Javiera Castillo" },
    ],
  },
  {
    id: "psiquiatria",
    name: "Psiquiatría",
    doctors: [
      { id: "psi-1", name: "Dra. Natalia Sepúlveda" },
      { id: "psi-2", name: "Dr. Matías Olivares" },
    ],
  },
  {
    id: "otorrinolaringologia",
    name: "Otorrinolaringología",
    doctors: [
      { id: "oto-1", name: "Dr. Cristóbal Navarro" },
      { id: "oto-2", name: "Dra. Antonia Reyes" },
    ],
  },
  {
    id: "cirugia-general",
    name: "Cirugía General",
    doctors: [
      { id: "cir-1", name: "Dr. Gonzalo Méndez" },
      { id: "cir-2", name: "Dra. Lorena Tapia" },
    ],
  },
];

const TIME_SLOTS = [
  "08:00",
  "08:30",
  "09:00",
  "09:30",
  "10:00",
  "10:30",
  "11:00",
  "11:30",
  "12:00",
  "15:00",
  "15:30",
  "16:00",
  "16:30",
  "17:00",
];

const WEEKDAYS = ["Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"];
const MONTHS = [
  "Enero",
  "Febrero",
  "Marzo",
  "Abril",
  "Mayo",
  "Junio",
  "Julio",
  "Agosto",
  "Septiembre",
  "Octubre",
  "Noviembre",
  "Diciembre",
];

function Appointment({ onBack, initialSpecialtyId = null }) {
  const [specialtyId, setSpecialtyId] = useState(initialSpecialtyId);
  const [doctorId, setDoctorId] = useState(null);
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedTime, setSelectedTime] = useState(null);
  const [timeConfirmed, setTimeConfirmed] = useState(false);
  const [viewDate, setViewDate] = useState(() => new Date());
  const scheduleRef = useRef(null);
  const patientRef = useRef(null);
  const [patient, setPatient] = useState({
    name: "",
    run: "",
    phone: "",
    email: "",
  });

  const specialty = SPECIALTIES.find((s) => s.id === specialtyId);
  const doctor = specialty?.doctors.find((d) => d.id === doctorId);

  const today = useMemo(() => {
    const d = new Date();
    d.setHours(0, 0, 0, 0);
    return d;
  }, []);

  const calendarDays = useMemo(() => {
    const year = viewDate.getFullYear();
    const month = viewDate.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const startPad = firstDay.getDay();
    const days = [];

    for (let i = 0; i < startPad; i++) {
      days.push(null);
    }
    for (let day = 1; day <= lastDay.getDate(); day++) {
      days.push(new Date(year, month, day));
    }
    return days;
  }, [viewDate]);

  const isWeekend = (date) => {
    const day = date.getDay();
    return day === 0 || day === 6;
  };

  const isPast = (date) => date < today;

  const isSelectable = (date) => !isPast(date) && !isWeekend(date);

  const formatDate = (date) =>
    date.toLocaleDateString("es-CL", {
      weekday: "long",
      day: "numeric",
      month: "long",
      year: "numeric",
    });

  const scrollToRef = (ref) => {
    ref.current?.scrollIntoView({ behavior: "smooth", block: "start" });
  };

  const handleSpecialtyChange = (id) => {
    setSpecialtyId(id);
    setDoctorId(null);
    setSelectedDate(null);
    setSelectedTime(null);
    setTimeConfirmed(false);
  };

  const handleConfirmTime = () => {
    if (!selectedDate || !selectedTime) {
      alert("Selecciona un día y un horario antes de confirmar.");
      return;
    }
    setTimeConfirmed(true);
    setTimeout(() => scrollToRef(patientRef), 100);
  };

  const handlePrevMonth = () => {
    setViewDate((prev) => new Date(prev.getFullYear(), prev.getMonth() - 1, 1));
  };

  const handleNextMonth = () => {
    setViewDate((prev) => new Date(prev.getFullYear(), prev.getMonth() + 1, 1));
  };

  const handlePatientChange = (e) => {
    setPatient({ ...patient, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!specialty || !doctor || !selectedDate || !selectedTime || !timeConfirmed) {
      alert("Completa especialidad, médico, fecha y confirma el horario.");
      return;
    }
    if (!patient.name || !patient.run || !patient.phone || !patient.email) {
      alert("Completa tus datos de registro.");
      return;
    }
    
    try {
    const payload = {
      pacienteCorreo: patient.email,
      pacienteNombre: patient.name,
      pacienteRun: patient.run,
      pacienteTelefono: patient.phone,
      doctorNombre: doctor.name,
      especialidad: specialty.name,
      fecha: selectedDate.toISOString().split("T")[0], // YYYY-MM-DD
      hora: selectedTime
    };

    const result = await agendarCita(payload);

    alert(
      `Cita agendada con éxito\n\n` +
        `Especialidad: ${specialty.name}\n` +
        `Médico: ${doctor.name}\n` +
        `Fecha: ${formatDate(selectedDate)}\n` +
        `Horario: ${selectedTime}\n` +
        `Paciente: ${patient.name} (${patient.run})`
    );

    console.log("Respuesta del backend:", result);
    onBack(); // volver atrás o redirigir
  } catch (error) {
    alert("Error al agendar cita");
    console.error(error);
  }
};

  return (
    <div className="appointment-page">
      <header className="appointment-header">
        <div className="appointment-header-left">
          <h1>Hospital RedNorte</h1>
          <p>Agendar cita médica</p>
        </div>
        <button type="button" className="appointment-back-btn" onClick={onBack}>
          ← Volver atrás
        </button>
      </header>

      <main className="appointment-main">
        <section className="appointment-section">
          <h2>1. Especialidad</h2>
          <p className="section-hint">Selecciona el área médica que necesitas.</p>
          <div className="specialty-grid">
            {SPECIALTIES.map((item) => (
              <button
                key={item.id}
                type="button"
                className={`specialty-chip ${specialtyId === item.id ? "active" : ""}`}
                onClick={() => handleSpecialtyChange(item.id)}
              >
                {item.name}
              </button>
            ))}
          </div>
        </section>

        {specialty && (
          <section className="appointment-section">
            <h2>2. Médico</h2>
            <p className="section-hint">
              Profesionales disponibles en {specialty.name}.
            </p>
            <div className="doctor-grid">
              {specialty.doctors.map((doc) => (
                <button
                  key={doc.id}
                  type="button"
                  className={`doctor-card ${doctorId === doc.id ? "active" : ""}`}
                  onClick={() => {
                    setDoctorId(doc.id);
                    setSelectedDate(null);
                    setSelectedTime(null);
                    setTimeConfirmed(false);
                    setTimeout(() => scrollToRef(scheduleRef), 100);
                  }}
                >
                  <span className="doctor-icon">👨‍⚕️</span>
                  <span className="doctor-name">{doc.name}</span>
                  <span className="doctor-area">{specialty.name}</span>
                </button>
              ))}
            </div>
          </section>
        )}

        {doctor && (
          <section className="schedule-row" ref={scheduleRef}>
            <section className="appointment-section calendar-section">
              <h2>3. Día</h2>
              <p className="section-hint">Lunes a viernes. Sin fines de semana.</p>

              <div className="calendar">
                <div className="calendar-nav">
                  <button type="button" onClick={handlePrevMonth} aria-label="Mes anterior">
                    ‹
                  </button>
                  <span>
                    {MONTHS[viewDate.getMonth()]} {viewDate.getFullYear()}
                  </span>
                  <button type="button" onClick={handleNextMonth} aria-label="Mes siguiente">
                    ›
                  </button>
                </div>

                <div className="calendar-weekdays">
                  {WEEKDAYS.map((day) => (
                    <span key={day}>{day}</span>
                  ))}
                </div>

                <div className="calendar-days">
                  {calendarDays.map((date, index) => {
                    if (!date) {
                      return <span key={`empty-${index}`} className="calendar-day empty" />;
                    }

                    const selectable = isSelectable(date);
                    const isSelected =
                      selectedDate &&
                      date.toDateString() === selectedDate.toDateString();

                    return (
                      <button
                        key={date.toISOString()}
                        type="button"
                        className={`calendar-day ${!selectable ? "disabled" : ""} ${
                          isSelected ? "selected" : ""
                        }`}
                        disabled={!selectable}
                        onClick={() => {
                          setSelectedDate(date);
                          setSelectedTime(null);
                          setTimeConfirmed(false);
                        }}
                      >
                        {date.getDate()}
                      </button>
                    );
                  })}
                </div>
              </div>

              {selectedDate && (
                <p className="selected-date-label">
                  Fecha elegida: <strong>{formatDate(selectedDate)}</strong>
                </p>
              )}
            </section>

            <section className="appointment-section time-section">
              <h2>4. Horario</h2>
              <p className="section-hint">
                {selectedDate
                  ? "Elige un horario y confirma con el botón."
                  : "Primero selecciona un día en el calendario."}
              </p>
              <div className="time-grid">
                {TIME_SLOTS.map((slot) => (
                  <button
                    key={slot}
                    type="button"
                    className={`time-slot ${selectedTime === slot ? "active" : ""}`}
                    disabled={!selectedDate}
                    onClick={() => {
                      setSelectedTime(slot);
                      setTimeConfirmed(false);
                    }}
                  >
                    {slot}
                  </button>
                ))}
              </div>
              <section className="time-section-actions">
                {timeConfirmed && selectedTime ? (
                  <p className="time-confirmed-msg">
                    Horario confirmado: {selectedTime}
                  </p>
                ) : null}
                <button
                  type="button"
                  className="confirm-time-btn"
                  disabled={!selectedDate || !selectedTime}
                  onClick={handleConfirmTime}
                >
                  Confirmar horario
                </button>
              </section>

            </section>
          </section>
        )}

        {doctor && selectedDate && timeConfirmed && (
          <section className="appointment-section patient-section" ref={patientRef}>
            <h2>5. Datos del paciente</h2>
            <p className="section-hint">Registra tus datos para confirmar la cita.</p>
            <form className="patient-form" onSubmit={handleSubmit}>
              <input
                type="text"
                name="name"
                placeholder="Nombre completo"
                value={patient.name}
                onChange={handlePatientChange}
                required
              />
              <input
                type="text"
                name="run"
                placeholder="RUN (ej. 12.345.678-9)"
                value={patient.run}
                onChange={handlePatientChange}
                required
              />
              <input
                type="tel"
                name="phone"
                placeholder="Teléfono"
                value={patient.phone}
                onChange={handlePatientChange}
                required
              />
              <input
                type="email"
                name="email"
                placeholder="Correo electrónico"
                value={localStorage.getItem("correo")}
                onChange={handlePatientChange}
                required
              />

              <div className="appointment-summary">
                <h3>Resumen de la cita</h3>
                <ul>
                  <li>
                    <span>Especialidad</span>
                    <strong>{specialty.name}</strong>
                  </li>
                  <li>
                    <span>Médico</span>
                    <strong>{doctor.name}</strong>
                  </li>
                  <li>
                    <span>Fecha</span>
                    <strong>{formatDate(selectedDate)}</strong>
                  </li>
                  <li>
                    <span>Horario</span>
                    <strong>{selectedTime}</strong>
                  </li>
                </ul>
              </div>

              <button type="submit" className="confirm-btn">
                Confirmar cita
              </button>
            </form>
          </section>
        )}
      </main>

      <footer className="appointment-footer">
        <p>© 2026 Hospital RedNorte — Todos los derechos reservados</p>
      </footer>
    </div>
  );
}

export default Appointment;
