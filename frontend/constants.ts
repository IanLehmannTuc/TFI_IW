export const API_BASE_URL = 'http://localhost:8080/api';

export const TRIAGE_COLORS = {
  CRITICA: 'bg-red-600 text-white border-red-700', 
  EMERGENCIA: 'bg-orange-100 text-orange-800 border-orange-200',
  URGENCIA: 'bg-yellow-100 text-yellow-800 border-yellow-200',
  URGENCIA_MENOR: 'bg-green-100 text-green-800 border-green-200',
  SIN_URGENCIA: 'bg-blue-100 text-blue-800 border-blue-200',
};

export const TRIAGE_LABELS = {
  CRITICA: 'CRÍTICA (Resucitación)',
  EMERGENCIA: 'Emergencia (10-15 min)',
  URGENCIA: 'Urgencia (30-60 min)',
  URGENCIA_MENOR: 'Urgencia Menor (2h)',
  SIN_URGENCIA: 'Sin Urgencia (4h)',
};