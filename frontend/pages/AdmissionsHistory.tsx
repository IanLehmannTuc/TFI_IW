import React, { useEffect, useState } from 'react';
import { apiRequest } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { Admission, AdmissionStatus, Attention, UserRole } from '../types';
import TriageBadge from '../components/TriageBadge';
import { RefreshCcw, Eye, Search, X, Calendar, User as UserIcon, Stethoscope, Activity, FileText } from 'lucide-react';

const AdmissionsHistory: React.FC = () => {
  const { user } = useAuth();
  const [admissions, setAdmissions] = useState<Admission[]>([]);
  const [filteredAdmissions, setFilteredAdmissions] = useState<Admission[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  
  // Modal State
  const [selectedAdmission, setSelectedAdmission] = useState<Admission | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);

  const fetchAdmissions = async () => {
    setLoading(true);
    try {
      // 6.1 Listar Todos los Ingresos
      const data = await apiRequest<Admission[]>('/ingresos');
      
      // Sort by date descending (newest first)
      const sorted = data.sort((a, b) => new Date(b.fechaHoraIngreso).getTime() - new Date(a.fechaHoraIngreso).getTime());
      setAdmissions(sorted);
      setFilteredAdmissions(sorted);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAdmissions();
  }, []);

  useEffect(() => {
    const term = searchTerm.toLowerCase();
    const filtered = admissions.filter(
      (adm) =>
        (adm.paciente?.nombre?.toLowerCase() || '').includes(term) ||
        (adm.paciente?.apellido?.toLowerCase() || '').includes(term) ||
        (adm.paciente?.cuil || '').includes(term)
    );
    setFilteredAdmissions(filtered);
  }, [searchTerm, admissions]);

  const handleOpenDetail = async (admission: Admission) => {
      setSelectedAdmission(admission); // Set initial data
      setDetailLoading(true);
      try {
          // Fetch full detail to ensure we have nurse name (Option 1 as per user request)
          const detail = await apiRequest<Admission>(`/ingresos/${admission.id}`);
          setSelectedAdmission(detail);
      } catch (e) {
          console.error("Error fetching detail", e);
      } finally {
          setDetailLoading(false);
      }
  };

  const getStatusBadge = (status: AdmissionStatus) => {
    switch (status) {
      case AdmissionStatus.PENDIENTE:
        return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">Pendiente</span>;
      case AdmissionStatus.EN_PROCESO:
        return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">En Proceso</span>;
      case AdmissionStatus.FINALIZADO:
        return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">Finalizado</span>;
      default:
        return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">{status}</span>;
    }
  };

  const getBloodPressure = (adm: Admission) => {
    if (adm.signosVitales) {
      return `${adm.signosVitales.tensionSistolica}/${adm.signosVitales.tensionDiastolica}`;
    }
    return '-/-';
  };

  return (
    <div className="space-y-6 relative">
      <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4">
        <div>
            <h1 className="text-2xl font-bold text-gray-900">Historial de Ingresos</h1>
            <p className="text-sm text-gray-500">Listado completo de atenciones registradas</p>
        </div>
        <div className="flex items-center gap-2">
            <button 
                onClick={fetchAdmissions}
                className="flex items-center gap-2 px-3 py-2 bg-white border border-gray-300 rounded-md text-sm text-gray-700 hover:bg-gray-50"
            >
                <RefreshCcw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
            </button>
        </div>
      </div>

      <div className="bg-white p-4 rounded-lg shadow border border-gray-200">
        <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Search className="h-5 w-5 text-gray-400" />
            </div>
            <input
                type="text"
                placeholder="Buscar por nombre, apellido o CUIL..."
                className="pl-10 block w-full sm:text-sm border-gray-300 rounded-md py-2 border bg-gray-50 focus:bg-white focus:ring-primary-500 focus:border-primary-500"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
            />
        </div>
      </div>

      <div className="bg-white shadow overflow-hidden sm:rounded-lg">
        <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
                <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Estado</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Fecha / Hora</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Paciente</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Triage</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Motivo</th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
                </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
                {filteredAdmissions.length === 0 ? (
                    <tr>
                        <td colSpan={6} className="px-6 py-10 text-center text-gray-500">
                            {loading ? 'Cargando registros...' : 'No se encontraron ingresos.'}
                        </td>
                    </tr>
                ) : (
                    filteredAdmissions.map((admission) => (
                    <tr key={admission.id} className="hover:bg-gray-50">
                         <td className="px-6 py-4 whitespace-nowrap">
                            {getStatusBadge(admission.estado)}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                             <div className="flex flex-col">
                                <span>{new Date(admission.fechaHoraIngreso).toLocaleDateString()}</span>
                                <span className="text-xs text-gray-400">{new Date(admission.fechaHoraIngreso).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                             </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                            <div className="text-sm font-medium text-gray-900">
                                {admission.paciente?.nombre ? `${admission.paciente.nombre} ${admission.paciente.apellido}` : 'Cargando...'}
                            </div>
                            <div className="text-xs text-gray-500">
                                {admission.paciente?.cuil || 'S/D'}
                            </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                            <TriageBadge level={admission.nivelEmergencia} />
                        </td>
                        <td className="px-6 py-4">
                            <div className="text-sm text-gray-900 max-w-xs truncate">
                                {admission.descripcion}
                            </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                            <button 
                                onClick={() => handleOpenDetail(admission)}
                                className="text-primary-600 hover:text-primary-900 bg-primary-50 p-2 rounded-full hover:bg-primary-100 transition-colors"
                                title="Ver detalles"
                            >
                                <Eye className="w-5 h-5" />
                            </button>
                        </td>
                    </tr>
                    ))
                )}
            </tbody>
            </table>
        </div>
      </div>

      {/* Detail Modal */}
      {selectedAdmission && (
          <div className="fixed inset-0 z-50 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
            <div className="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
                {/* Background overlay */}
                <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" onClick={() => setSelectedAdmission(null)}></div>

                <span className="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>

                <div className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-2xl w-full">
                    {/* Header */}
                    <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4 border-b border-gray-100">
                        <div className="flex justify-between items-start">
                            <div className="flex items-center gap-3">
                                <div className="bg-primary-100 rounded-full p-2">
                                    <UserIcon className="h-6 w-6 text-primary-600" />
                                </div>
                                <div>
                                    <h3 className="text-lg leading-6 font-medium text-gray-900" id="modal-title">
                                        {selectedAdmission.paciente?.nombre 
                                            ? `${selectedAdmission.paciente.nombre} ${selectedAdmission.paciente.apellido}` 
                                            : 'Cargando Paciente...'}
                                    </h3>
                                    <p className="text-sm text-gray-500">
                                        {selectedAdmission.paciente?.cuil || 'S/D'}
                                    </p>
                                </div>
                            </div>
                            <button 
                                onClick={() => setSelectedAdmission(null)}
                                className="bg-white rounded-md text-gray-400 hover:text-gray-500 focus:outline-none"
                            >
                                <X className="h-6 w-6" />
                            </button>
                        </div>
                    </div>

                    {/* Content */}
                    <div className="px-4 py-5 sm:p-6 bg-gray-50 h-full max-h-[70vh] overflow-y-auto">
                        
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
                            <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-200">
                                <div className="flex items-center gap-2 mb-3 text-gray-900 font-semibold">
                                    <Calendar className="w-4 h-4 text-gray-400" />
                                    <span>Datos de Ingreso</span>
                                </div>
                                <div className="space-y-2 text-sm">
                                    <div className="flex justify-between">
                                        <span className="text-gray-500">Fecha/Hora:</span>
                                        <span className="font-medium">
                                            {new Date(selectedAdmission.fechaHoraIngreso).toLocaleString()}
                                        </span>
                                    </div>
                                    <div className="flex justify-between items-center">
                                        <span className="text-gray-500">Estado:</span>
                                        {getStatusBadge(selectedAdmission.estado)}
                                    </div>
                                    <div className="flex justify-between items-center">
                                        <span className="text-gray-500">Triage:</span>
                                        <TriageBadge level={selectedAdmission.nivelEmergencia} />
                                    </div>
                                </div>
                            </div>

                            <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-200">
                                <div className="flex items-center gap-2 mb-3 text-gray-900 font-semibold">
                                    <Stethoscope className="w-4 h-4 text-gray-400" />
                                    <span>Profesionales</span>
                                </div>
                                <div className="space-y-2 text-sm">
                                    <div className="flex justify-between">
                                        <span className="text-gray-500">Enfermero (Triage):</span>
                                        <span className={`font-medium ${detailLoading ? 'text-gray-400' : ''}`}>
                                            {detailLoading ? 'Cargando...' : 
                                              selectedAdmission.enfermero?.apellido 
                                                ? `${selectedAdmission.enfermero.apellido}, ${selectedAdmission.enfermero.nombre || ''}`
                                                : (selectedAdmission.enfermero?.cuil || 'No registrado')
                                            }
                                        </span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span className="text-gray-500">Médico Atendiente:</span>
                                        <span className="font-medium">
                                            {selectedAdmission.atencion?.medico?.apellido
                                                ? `${selectedAdmission.atencion.medico.apellido}, ${selectedAdmission.atencion.medico.nombre}`
                                                : 'Pendiente de atención'}
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        {selectedAdmission.signosVitales && (
                            <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-200 mb-6">
                                <div className="flex items-center gap-2 mb-3 text-gray-900 font-semibold">
                                    <Activity className="w-4 h-4 text-gray-400" />
                                    <span>Signos Vitales</span>
                                </div>
                                <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
                                    <div className="bg-gray-50 p-2 rounded text-center">
                                        <span className="block text-xs text-gray-500 uppercase">Temp</span>
                                        <span className="block text-lg font-bold text-gray-800">{selectedAdmission.signosVitales.temperatura}°C</span>
                                    </div>
                                    <div className="bg-gray-50 p-2 rounded text-center">
                                        <span className="block text-xs text-gray-500 uppercase">Presión</span>
                                        <span className="block text-lg font-bold text-gray-800">
                                            {getBloodPressure(selectedAdmission)}
                                        </span>
                                    </div>
                                    <div className="bg-gray-50 p-2 rounded text-center">
                                        <span className="block text-xs text-gray-500 uppercase">Pulso</span>
                                        <span className="block text-lg font-bold text-gray-800">{selectedAdmission.signosVitales.frecuenciaCardiaca}</span>
                                    </div>
                                    <div className="bg-gray-50 p-2 rounded text-center">
                                        <span className="block text-xs text-gray-500 uppercase">Resp.</span>
                                        <span className="block text-lg font-bold text-gray-800">{selectedAdmission.signosVitales.frecuenciaRespiratoria}</span>
                                    </div>
                                </div>
                            </div>
                        )}

                        <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-200">
                            <div className="flex items-center gap-2 mb-3 text-gray-900 font-semibold">
                                <FileText className="w-4 h-4 text-gray-400" />
                                <span>Informe Triage / Motivo</span>
                            </div>
                            <div className="p-3 bg-gray-50 rounded-md text-sm text-gray-700 leading-relaxed mb-4">
                                {selectedAdmission.descripcion}
                            </div>
                            
                            {/* Medical Report Section - Only visible to MEDICO or if exists */}
                            {selectedAdmission.atencion && (
                                <div className="mt-4 pt-4 border-t border-gray-100 animate-fade-in">
                                    <div className="flex items-center gap-2 mb-3 text-primary-700 font-semibold">
                                        <FileText className="w-4 h-4" />
                                        <span>Informe Médico (Atención Finalizada)</span>
                                    </div>
                                    
                                    <div className="p-3 bg-blue-50/50 border border-blue-100 rounded-md text-sm text-gray-800 leading-relaxed">
                                        {selectedAdmission.atencion.informeMedico}
                                        <div className="mt-2 text-xs text-gray-400 text-right">
                                            Atendido el: {new Date(selectedAdmission.atencion.fechaAtencion).toLocaleString()}
                                        </div>
                                    </div>
                                    
                                    <p className="mt-4 text-xs text-green-600 font-medium flex items-center gap-1">
                                        <span className="w-2 h-2 bg-green-500 rounded-full"></span>
                                        El paciente ha sido atendido y el proceso ha finalizado.
                                    </p>
                                </div>
                            )}
                        </div>

                    </div>
                    
                    {/* Footer */}
                    <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse border-t border-gray-200">
                        <button
                            type="button"
                            className="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm"
                            onClick={() => setSelectedAdmission(null)}
                        >
                            Cerrar
                        </button>
                    </div>
                </div>
            </div>
        </div>
      )}
    </div>
  );
};

export default AdmissionsHistory;