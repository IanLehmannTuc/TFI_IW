import React, { useState, useEffect } from 'react';
import { apiRequest } from '../services/api';
import { Admission, Attention, AdmissionStatus } from '../types';
import TriageBadge from '../components/TriageBadge';
import { Play, CheckCircle, FileText, AlertCircle, RefreshCcw } from 'lucide-react';

const AttendPatient: React.FC = () => {
  const [currentPatient, setCurrentPatient] = useState<Admission | null>(null);
  const [loading, setLoading] = useState(false);
  const [restoringSession, setRestoringSession] = useState(true);
  const [medicalReport, setMedicalReport] = useState('');
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');


  useEffect(() => {
      const restoreSession = async () => {
          const savedAdmissionId = localStorage.getItem('activePatientId');
          if (!savedAdmissionId) {
              setRestoringSession(false);
              return;
          }

          setRestoringSession(true);
          try {

              const admission = await apiRequest<Admission>(`/ingresos/${savedAdmissionId}`);

              if (admission.estado === AdmissionStatus.EN_PROCESO) {
                  setCurrentPatient(admission);
              } else {
                  localStorage.removeItem('activePatientId');
              }
          } catch (e) {
              console.error("Could not restore session", e);
              localStorage.removeItem('activePatientId');
          } finally {
              setRestoringSession(false);
          }
      };

      restoreSession();
  }, []);

  const handleNextPatient = async () => {
    setLoading(true);
    setError('');
    setSuccessMessage('');
    setMedicalReport('');
    try {


      const patientAdmission = await apiRequest<Admission>('/cola-atencion/atender', {
          method: 'POST'
      });




      localStorage.setItem('activePatientId', patientAdmission.id);
      setCurrentPatient(patientAdmission);

    } catch (e) {

      setError(e instanceof Error ? e.message : 'No hay pacientes en espera.');
      setCurrentPatient(null);
    } finally {
      setLoading(false);
    }
  };

  const handleFinish = async () => {
      if (!currentPatient) return;
      if (!medicalReport.trim()) {
          setError('El informe médico es obligatorio.');
          return;
      }

      setLoading(true);
      setError('');

      try {


          await apiRequest<Attention>('/atenciones', {
              method: 'POST',
              body: JSON.stringify({
                  ingresoId: currentPatient.id,
                  informe: medicalReport
              })
          });

          setSuccessMessage('Atención finalizada correctamente.');


          localStorage.removeItem('activePatientId');

          setCurrentPatient(null);
          setMedicalReport('');

          setTimeout(() => {
             setSuccessMessage('');
          }, 3000);

      } catch (e) {
          setError(e instanceof Error ? e.message : 'Error al finalizar la atención.');
      } finally {
          setLoading(false);
      }
  }

  const getBloodPressure = () => {
    if (currentPatient?.tensionSistolica && currentPatient?.tensionDiastolica) {
        return `${currentPatient.tensionSistolica}/${currentPatient.tensionDiastolica}`;
    }
    return 'No registrado';
  };

  if (restoringSession) {
      return (
          <div className="max-w-4xl mx-auto flex justify-center items-center h-64">
              <div className="flex flex-col items-center gap-3 text-gray-500">
                  <RefreshCcw className="w-8 h-8 animate-spin text-primary-500" />
                  <p>Restaurando sesión de atención...</p>
              </div>
          </div>
      );
  }

  return (
    <div className="max-w-4xl mx-auto">
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
                    className="ml-3 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50"
                >
                    {loading ? (
                        <span className="animate-spin h-5 w-5 border-2 border-white border-t-transparent rounded-full mr-2"></span>
                    ) : (
                        <Play className="mr-2 -ml-1 h-5 w-5" />
                    )}
                    {loading ? 'Procesando...' : 'Llamar Siguiente Paciente'}
                </button>
            )}
        </div>
      </div>

      {error && (
        <div className="rounded-md bg-red-50 p-4 mb-6 border-l-4 border-red-400">
          <div className="flex">
             <AlertCircle className="h-5 w-5 text-red-400" />
            <div className="ml-3">
              <h3 className="text-sm font-medium text-red-800">{error}</h3>
            </div>
          </div>
        </div>
      )}

      {successMessage && (
        <div className="rounded-md bg-green-50 p-4 mb-6 border-l-4 border-green-400">
          <div className="flex">
             <CheckCircle className="h-5 w-5 text-green-400" />
            <div className="ml-3">
              <h3 className="text-sm font-medium text-green-800">{successMessage}</h3>
            </div>
          </div>
        </div>
      )}

      {currentPatient && (
        <div className="bg-white shadow overflow-hidden sm:rounded-lg border-t-4 border-primary-500 animate-fade-in-up">
          <div className="px-4 py-5 sm:px-6 flex justify-between items-start bg-gray-50">
            <div>
                <h3 className="text-xl leading-6 font-bold text-gray-900">
                {currentPatient.pacienteNombre ? `${currentPatient.pacienteNombre} ${currentPatient.pacienteApellido}` : 'Cargando datos...'}
                </h3>
                <p className="mt-1 max-w-2xl text-sm text-gray-500">CUIL: {currentPatient.pacienteCuil || 'S/D'}</p>
                <div className="mt-2 inline-flex items-center px-2.5 py-0.5 rounded-md text-xs font-medium bg-blue-100 text-blue-800">
                    EN PROCESO DE ATENCIÓN
                </div>
            </div>
            <TriageBadge level={currentPatient.nivelEmergencia} />
          </div>

          <div className="border-t border-gray-200">
             <div className="grid grid-cols-1 md:grid-cols-2 gap-0 divide-y md:divide-y-0 md:divide-x divide-gray-200">
                 <div className="p-4 sm:p-6">
                    <h4 className="text-sm font-bold text-gray-500 uppercase tracking-wider mb-4">Información de Ingreso</h4>
                    <dl className="space-y-4">
                        <div>
                            <dt className="text-sm font-medium text-gray-500">Motivo de consulta</dt>
                            <dd className="mt-1 text-sm text-gray-900 bg-gray-50 p-3 rounded-md border border-gray-100">{currentPatient.descripcion}</dd>
                        </div>
                        {currentPatient.temperatura && (
                            <div>
                                <dt className="text-sm font-medium text-gray-500 mb-2">Signos Vitales</dt>
                                <dd className="grid grid-cols-2 gap-3">
                                    <div className="bg-gray-50 p-2 rounded border border-gray-100">
                                        <span className="block text-xs text-gray-500">Presión Arterial</span>
                                        <span className="font-semibold">{getBloodPressure()} mmHg</span>
                                    </div>
                                    <div className="bg-gray-50 p-2 rounded border border-gray-100">
                                        <span className="block text-xs text-gray-500">Frec. Cardíaca</span>
                                        <span className="font-semibold">{currentPatient.frecuenciaCardiaca} bpm</span>
                                    </div>
                                    <div className="bg-gray-50 p-2 rounded border border-gray-100">
                                        <span className="block text-xs text-gray-500">Temperatura</span>
                                        <span className="font-semibold">{currentPatient.temperatura} °C</span>
                                    </div>
                                    <div className="bg-gray-50 p-2 rounded border border-gray-100">
                                        <span className="block text-xs text-gray-500">Frec. Resp.</span>
                                        <span className="font-semibold">{currentPatient.frecuenciaRespiratoria} rpm</span>
                                    </div>
                                </dd>
                            </div>
                        )}
                         <div>
                            <dt className="text-sm font-medium text-gray-500">Ingresado por</dt>
                            <dd className="mt-1 text-xs text-gray-400">
                                Enfermero CUIL: {currentPatient.enfermeroCuil || 'N/A'} (Mat: {currentPatient.enfermeroMatricula || 'N/A'})
                            </dd>
                        </div>
                    </dl>
                 </div>

                 <div className="p-4 sm:p-6 bg-blue-50/30">
                     <h4 className="text-sm font-bold text-primary-700 uppercase tracking-wider mb-4 flex items-center gap-2">
                         <FileText className="w-4 h-4" />
                         Informe Médico
                     </h4>

                     <div className="space-y-4">
                         <div>
                             <label htmlFor="report" className="block text-sm font-medium text-gray-700 mb-1">
                                 Diagnóstico y Evolución
                             </label>
                             <textarea
                                id="report"
                                rows={12}
                                className="shadow-sm focus:ring-primary-500 focus:border-primary-500 block w-full sm:text-sm border-gray-300 rounded-md p-3"
                                placeholder="Describa el diagnóstico, tratamiento aplicado y evolución del paciente..."
                                value={medicalReport}
                                onChange={(e) => setMedicalReport(e.target.value)}
                             ></textarea>
                         </div>

                         <div className="flex items-center justify-end pt-4">
                             <button
                                onClick={handleFinish}
                                disabled={loading}
                                className="w-full sm:w-auto inline-flex items-center justify-center px-6 py-3 border border-transparent rounded-md shadow-sm text-base font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 transition-colors disabled:opacity-70"
                             >
                                 {loading ? 'Guardando...' : 'Finalizar Atención'}
                                 {!loading && <CheckCircle className="ml-2 -mr-1 h-5 w-5" />}
                             </button>
                         </div>
                     </div>
                 </div>
             </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AttendPatient;