export enum UserRole {
  ENFERMERO = 'ENFERMERO',
  MEDICO = 'MEDICO',
}

export enum TriageLevel {
  CRITICA = 'CRITICA',
  EMERGENCIA = 'EMERGENCIA',
  URGENCIA = 'URGENCIA',
  URGENCIA_MENOR = 'URGENCIA_MENOR',
  SIN_URGENCIA = 'SIN_URGENCIA',
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
}

export interface AuthResponse {
  token: string;
  email: string;
  autoridad: UserRole;
  expiresIn: number;
}

export interface Domicilio {
  calle: string;
  numero: number;
  localidad: string;
}

export interface ObraSocial {
  obraSocial: {
    id: number;
    nombre: string;
  };
  numeroAfiliado: string;
}

export interface Patient {
  id: string;
  cuil: string;
  nombre: string;
  apellido: string;
  domicilio?: Domicilio;
  obraSocial?: ObraSocial;
}

export interface AdmissionRequest {
  pacienteCuil: string;
  pacienteNombre?: string;
  pacienteApellido?: string;
  pacienteEmail?: string;
  pacienteDomicilio?: Domicilio;
  pacienteObraSocial?: ObraSocial;
  enfermeroCuil: string; // Derived from current user
  descripcion: string;
  temperatura: number;
  tensionSistolica: number;
  tensionDiastolica: number;
  frecuenciaCardiaca: number;
  frecuenciaRespiratoria: number;
  nivelEmergencia: TriageLevel;
}

export interface Admission {
  id: string;
  pacienteCuil: string;
  pacienteNombre: string;
  pacienteApellido: string;
  enfermeroCuil: string;
  enfermeroMatricula: string;
  descripcion: string;
  fechaHoraIngreso: string;
  temperatura: number;
  tensionSistolica: number;
  tensionDiastolica: number;
  frecuenciaCardiaca: number;
  frecuenciaRespiratoria: number;
  nivelEmergencia: TriageLevel;
  estado: AdmissionStatus;
}