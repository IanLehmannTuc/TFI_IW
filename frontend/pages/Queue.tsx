import React, { useEffect, useState } from 'react';
import { apiRequest } from '../services/api';
import { Admission } from '../types';
import TriageBadge from '../components/TriageBadge';
import { RefreshCcw, ChevronDown, ChevronUp, Thermometer, Activity, Wind, Heart, FileText, User } from 'lucide-react';

const Queue: React.FC = () => {
  const [admissions, setAdmissions] = useState<Admission[]>([]);
  const [loading, setLoading] = useState(true);
  const [expandedId, setExpandedId] = useState<string | null>(null);

  const fetchQueue = async () => {
    setLoading(true);
    try {
      const data = await apiRequest<Admission[]>('/cola-atencion');
      setAdmissions(data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchQueue();
    // Auto refresh every 30 seconds
    const interval = setInterval(fetchQueue, 30000);
    return () => clearInterval(interval);
  }, []);

  const toggleExpand = (id: string) => {
    setExpandedId(expandedId === id ? null : id);
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-900">Sala de Espera</h1>
        <button 
            onClick={fetchQueue}
            className="flex items-center gap-2 px-3 py-2 bg-white border border-gray-300 rounded-md text-sm text-gray-700 hover:bg-gray-50 transition-colors"
        >
            <RefreshCcw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
            Actualizar
        </button>
      </div>

      <div className="bg-white shadow overflow-hidden sm:rounded-lg border border-gray-200">
        <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
                <tr>
                <th scope="col" className="w-10 px-6 py-3"></th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Prioridad</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Paciente</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Ingreso</th>
                <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Motivo</th>
                </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
                {admissions.length === 0 ? (
                    <tr>
                        <td colSpan={5} className="px-6 py-10 text-center text-gray-500">
                            No hay pacientes en espera.
                        </td>
                    </tr>
                ) : (
                    admissions.map((admission) => (
                    <React.Fragment key={admission.id}>
                        <tr 
                            onClick={() => toggleExpand(admission.id)}
                            className={`cursor-pointer transition-colors ${expandedId === admission.id ? 'bg-blue-50' : 'hover:bg-gray-50'}`}
                        >
                            <td className="px-6 py-4 whitespace-nowrap text-gray-400">
                                {expandedId === admission.id ? <ChevronUp className="w-5 h-5" /> : <ChevronDown className="w-5 h-5" />}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                <TriageBadge level={admission.nivelEmergencia} />
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap">
                                <div className="text-sm font-medium text-gray-900">
                                    {admission.pacienteNombre} {admission.pacienteApellido}
                                </div>
                                <div className="text-xs text-gray-500">
                                    CUIL: {admission.pacienteCuil}
                                </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                {new Date(admission.fechaHoraIngreso).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                            </td>
                            <td className="px-6 py-4">
                                <div className="text-sm text-gray-900 max-w-xs truncate">
                                    {admission.descripcion}
                                </div>
                            </td>
                        </tr>
                        
                        {/* Expanded Row */}
                        {expandedId === admission.id && (
                            <tr className="bg-gray-50">
                                <td colSpan={5} className="px-6 py-4 border-t border-gray-100 shadow-inner">
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                        
                                        {/* Full Description */}
                                        <div className="bg-white p-4 rounded-lg border border-gray-200 shadow-sm">
                                            <h4 className="flex items-center gap-2 text-sm font-bold text-gray-900 mb-2">
                                                <FileText className="w-4 h-4 text-primary-500" />
                                                Motivo de Consulta Completo
                                            </h4>
                                            <p className="text-sm text-gray-700 leading-relaxed whitespace-pre-wrap">
                                                {admission.descripcion}
                                            </p>
                                        </div>

                                        {/* Vitals & Details */}
                                        <div className="space-y-4">
                                            {/* Vitals */}
                                            <div className="bg-white p-4 rounded-lg border border-gray-200 shadow-sm">
                                                <h4 className="flex items-center gap-2 text-sm font-bold text-gray-900 mb-3">
                                                    <Activity className="w-4 h-4 text-primary-500" />
                                                    Signos Vitales
                                                </h4>
                                                <div className="grid grid-cols-2 gap-3">
                                                    <div className="flex items-center gap-2 p-2 bg-gray-50 rounded">
                                                        <Activity className="w-4 h-4 text-red-500" />
                                                        <div>
                                                            <p className="text-xs text-gray-500 uppercase">Presión</p>
                                                            <p className="text-sm font-bold">{admission.tensionSistolica}/{admission.tensionDiastolica}</p>
                                                        </div>
                                                    </div>
                                                    <div className="flex items-center gap-2 p-2 bg-gray-50 rounded">
                                                        <Heart className="w-4 h-4 text-rose-500" />
                                                        <div>
                                                            <p className="text-xs text-gray-500 uppercase">Pulso</p>
                                                            <p className="text-sm font-bold">{admission.frecuenciaCardiaca} bpm</p>
                                                        </div>
                                                    </div>
                                                    <div className="flex items-center gap-2 p-2 bg-gray-50 rounded">
                                                        <Thermometer className="w-4 h-4 text-orange-500" />
                                                        <div>
                                                            <p className="text-xs text-gray-500 uppercase">Temp</p>
                                                            <p className="text-sm font-bold">{admission.temperatura}°C</p>
                                                        </div>
                                                    </div>
                                                    <div className="flex items-center gap-2 p-2 bg-gray-50 rounded">
                                                        <Wind className="w-4 h-4 text-blue-500" />
                                                        <div>
                                                            <p className="text-xs text-gray-500 uppercase">Resp</p>
                                                            <p className="text-sm font-bold">{admission.frecuenciaRespiratoria} rpm</p>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            
                                            {/* Extra Info */}
                                            <div className="flex items-center gap-2 text-xs text-gray-500 px-1">
                                                <User className="w-3 h-3" />
                                                <span>Registrado por: {admission.enfermeroMatricula || 'N/A'}</span>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        )}
                    </React.Fragment>
                    ))
                )}
            </tbody>
            </table>
        </div>
      </div>
    </div>
  );
};

export default Queue;