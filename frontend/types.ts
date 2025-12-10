export enum UserRole {
  ENFERMERO = 'ENFERMERO',
  MEDICO = 'MEDICO',
}

export enum TriageLevel {
  CRITICA = 'CRITICA', // Nivel 5
  EMERGENCIA = 'EMERGENCIA', // Nivel 4
  URGENCIA = 'URGENCIA', // Nivel 3
  URGENCIA_MENOR = 'URGENCIA_MENOR', // Nivel 2
  SIN_URGENCIA = 'SIN_URGENCIA', // Nivel 1
}

export enum AdmissionStatus {
  PENDIENTE = 'PENDIENTE',
  EN_PROCESO = 'EN_PROCESO',
  FINALIZADO = 'FINALIZADO',
}

export interface User {
  id: string;
  email: string;
  autoridad: UserRole;
  nombre?: string;
  apellido?: string;
  cuil?: string;
  matricula?: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  autoridad: UserRole;
  expiresIn: number;
}

export interface ErrorResponse {
  mensaje: string;
  timestamp: string;
  status: number;
}

export interface Domicilio {
  calle: string;
  numero: number;
  localidad: string;
}

export interface ObraSocialRef {
  id: number;
  nombre: string;
}

export interface ObraSocial {
  obraSocial: ObraSocialRef;
  numeroAfiliado: string;
}

export interface Patient {
  id?: string;
  cuil: string;
  nombre: string;
  apellido: string;
  telefono?: string;
  fechaNacimiento?: string; // YYYY-MM-DD
  sexo?: string;
  edad?: number;
  domicilio?: Domicilio;
  obraSocial?: ObraSocial;
  email?: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// IS2025-001: Estructura plana para el Request de Ingreso (creación on-the-fly)
export interface AdmissionRequest {
  // Paciente
  pacienteCuil: string;
  pacienteNombre?: string;
  pacienteApellido?: string;
  pacienteEmail?: string;
  pacienteTelefono?: string;
  pacienteFechaNacimiento?: string;
  pacienteSexo?: string;
  pacienteDomicilio?: {
    calle: string;
    numero: number;
    localidad: string;
  };
  pacienteObraSocial?: {
    obraSocial: { id?: number; nombre?: string };
    numeroAfiliado: string;
  };

  // Enfermero
  enfermeroCuil: string;

  // Triage
  descripcion: string;
  temperatura: number;
  tensionSistolica: number;
  tensionDiastolica: number;
  frecuenciaCardiaca: number;
  frecuenciaRespiratoria: number;
  nivelEmergencia: TriageLevel;
}

export interface SignosVitales {
  temperatura: number;
  tensionSistolica: number;
  tensionDiastolica: number;
  frecuenciaCardiaca: number;
  frecuenciaRespiratoria: number;
}

export interface Admission {
  id: string;
  paciente: Patient;
  enfermero?: User;
  descripcion: string;
  fechaHoraIngreso: string;
  signosVitales?: SignosVitales;
  nivelEmergencia: TriageLevel;
  estado: AdmissionStatus;
  atencion?: Attention;
}

export interface AttentionRequest {
  ingresoId: string;
  informe: string;
}

export interface Attention {
  id: string;
  ingresoId?: string;
  medico: User;
  informeMedico: string; 
  fechaAtencion: string;
}