export const API_BASE_URL = 'http://localhost:8080/api';

export const TRIAGE_COLORS = {
  CRITICA: 'bg-red-100 text-red-800 border-red-200',
  EMERGENCIA: 'bg-orange-100 text-orange-800 border-orange-200',
  URGENCIA: 'bg-yellow-100 text-yellow-800 border-yellow-200',
  URGENCIA_MENOR: 'bg-green-100 text-green-800 border-green-200',
  SIN_URGENCIA: 'bg-blue-100 text-blue-800 border-blue-200',
};

export const TRIAGE_LABELS = {
  CRITICA: 'Crítica (Nivel 1)',
  EMERGENCIA: 'Emergencia (Nivel 2)',
  URGENCIA: 'Urgencia (Nivel 3)',
  URGENCIA_MENOR: 'Urgencia Menor (Nivel 4)',
  SIN_URGENCIA: 'Sin Urgencia (Nivel 5)',
};
