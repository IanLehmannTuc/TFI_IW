import React, { useState } from 'react';
import { apiRequest } from '../services/api';
import { Admission } from '../types';
import TriageBadge from '../components/TriageBadge';
import { Play, CheckCircle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const AttendPatient: React.FC = () => {
  const [currentPatient, setCurrentPatient] = useState<Admission | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleNextPatient = async () => {
    setLoading(true);
    setError('');
    try {
        // API Endpoint: /api/cola-atencion/atender
        // Method: POST
        // Response: Admission object or 204
      const patient = await apiRequest<Admission>('/cola-atencion/atender', {
          method: 'POST'
      });
      
      // Check if object is empty (simple check for 204 -> {} conversion in api.ts)
      if (!patient || Object.keys(patient).length === 0) {
        setError('No hay pacientes en espera.');
        setCurrentPatient(null);
      } else {
        setCurrentPatient(patient);
      }
    } catch (e) {
      setError('Error al obtener el siguiente paciente.');
    } finally {
      setLoading(false);
    }
  };

  const handleFinish = () => {
      // In a real flow, we might send clinical notes here.
      // For now, we just clear the view to get the next one.
      setCurrentPatient(null);
      navigate('/queue');
  }

  return (
    <div className="max-w-3xl mx-auto">
      <div className="md:flex md:items-center md:justify-between mb-8">
        <div className="flex-1 min-w-0">
          <h2 className="text-2xl font-bold leading-7 text-gray-900 sm:text-3xl sm:truncate">
            Consultorio Médico
          </h2>
        </div>
        <div className="mt-4 flex md:mt-0 md:ml-4">
            {!currentPatient && (
                <button
                    onClick={handleNextPatient}
                    disabled={loading}
                    className="ml-3 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
                >
                    <Play className="mr-2 -ml-1 h-5 w-5" />
                    {loading ? 'Buscando...' : 'Llamar Siguiente Paciente'}
                </button>
            )}
        </div>
      </div>

      {error && (
        <div className="rounded-md bg-yellow-50 p-4 mb-6">
          <div className="flex">
            <div className="ml-3">
              <h3 className="text-sm font-medium text-yellow-800">{error}</h3>
            </div>
          </div>
        </div>
      )}

      {currentPatient && (
        <div className="bg-white shadow overflow-hidden sm:rounded-lg border-t-4 border-primary-500">
          <div className="px-4 py-5 sm:px-6 flex justify-between items-start">
            <div>
                <h3 className="text-xl leading-6 font-medium text-gray-900">
                {currentPatient.pacienteNombre} {currentPatient.pacienteApellido}
                </h3>
                <p className="mt-1 max-w-2xl text-sm text-gray-500">CUIL: {currentPatient.pacienteCuil}</p>
            </div>
            <TriageBadge level={currentPatient.nivelEmergencia} />
          </div>
          <div className="border-t border-gray-200 px-4 py-5 sm:p-0">
            <dl className="sm:divide-y sm:divide-gray-200">
              <div className="py-4 sm:py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                <dt className="text-sm font-medium text-gray-500">Motivo de consulta</dt>
                <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{currentPatient.descripcion}</dd>
              </div>
              <div className="py-4 sm:py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                <dt className="text-sm font-medium text-gray-500">Signos Vitales</dt>
                <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                    <div className="grid grid-cols-2 gap-4">
                        <div className="bg-gray-50 p-2 rounded">
                            <span className="block text-xs text-gray-500">Presión Arterial</span>
                            <span className="font-semibold">{currentPatient.tensionSistolica}/{currentPatient.tensionDiastolica} mmHg</span>
                        </div>
                        <div className="bg-gray-50 p-2 rounded">
                            <span className="block text-xs text-gray-500">Frec. Cardíaca</span>
                            <span className="font-semibold">{currentPatient.frecuenciaCardiaca} bpm</span>
                        </div>
                        <div className="bg-gray-50 p-2 rounded">
                            <span className="block text-xs text-gray-500">Temperatura</span>
                            <span className="font-semibold">{currentPatient.temperatura} °C</span>
                        </div>
                         <div className="bg-gray-50 p-2 rounded">
                            <span className="block text-xs text-gray-500">Frec. Resp.</span>
                            <span className="font-semibold">{currentPatient.frecuenciaRespiratoria} rpm</span>
                        </div>
                    </div>
                </dd>
              </div>
              <div className="py-4 sm:py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                <dt className="text-sm font-medium text-gray-500">Registrado por</dt>
                <dd className="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">Enf. Matrícula: {currentPatient.enfermeroMatricula}</dd>
              </div>
            </dl>
          </div>
          <div className="bg-gray-50 px-4 py-4 sm:px-6 flex justify-end">
             <button
                onClick={handleFinish}
                className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none"
             >
                 <CheckCircle className="mr-2 -ml-1 h-5 w-5" />
                 Finalizar Atención
             </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default AttendPatient;
