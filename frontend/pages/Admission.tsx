import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { apiRequest, ApiError } from '../services/api';
import { TriageLevel, User, Patient } from '../types';
import { useNavigate } from 'react-router-dom';
import { AlertCircle, CheckCircle2, Clock, Activity, HeartPulse, Search, UserPlus, UserCheck, X, Pencil, Save, CreditCard } from 'lucide-react';

interface AdmissionFormData {
  // Patient Info
  pacienteCuil: string;
  pacienteNombre: string;
  pacienteApellido: string;
  pacienteEmail?: string;
  calle?: string;
  numero?: number;
  localidad?: string;
  
  // Obra Social Info
  obraSocialNombre?: string;
  numeroAfiliado?: string;

  // Triage Info
  descripcion: string;
  temperatura: number;
  tensionSistolica: number;
  tensionDiastolica: number;
  frecuenciaCardiaca: number;
  frecuenciaRespiratoria: number;
  nivelEmergencia: TriageLevel;
}

const TRIAGE_OPTIONS = [
  {
    value: TriageLevel.CRITICA,
    label: 'CRÍTICA (Nivel 1)',
    description: 'Atención Inmediata. Riesgo vital.',
    color: 'red',
    icon: Activity,
    classes: {
      selected: 'bg-red-600 text-white border-red-600 shadow-red-200 ring-2 ring-red-300',
      default: 'bg-white hover:bg-red-50 text-gray-700 border-l-4 border-l-red-500 border-y border-r border-gray-200'
    }
  },
  {
    value: TriageLevel.EMERGENCIA,
    label: 'EMERGENCIA (Nivel 2)',
    description: 'Atención muy urgente (10-15 min).',
    color: 'orange',
    icon: AlertCircle,
    classes: {
      selected: 'bg-orange-500 text-white border-orange-500 shadow-orange-200 ring-2 ring-orange-300',
      default: 'bg-white hover:bg-orange-50 text-gray-700 border-l-4 border-l-orange-500 border-y border-r border-gray-200'
    }
  },
  {
    value: TriageLevel.URGENCIA,
    label: 'URGENCIA (Nivel 3)',
    description: 'Urgencia media. Estabilidad relativa.',
    color: 'yellow',
    icon: Clock,
    classes: {
      selected: 'bg-yellow-500 text-white border-yellow-500 shadow-yellow-200 ring-2 ring-yellow-300',
      default: 'bg-white hover:bg-yellow-50 text-gray-700 border-l-4 border-l-yellow-500 border-y border-r border-gray-200'
    }
  },
  {
    value: TriageLevel.URGENCIA_MENOR,
    label: 'URGENCIA MENOR (Nivel 4)',
    description: 'Sin riesgo vital inmediato.',
    color: 'green',
    icon: HeartPulse,
    classes: {
      selected: 'bg-green-600 text-white border-green-600 shadow-green-200 ring-2 ring-green-300',
      default: 'bg-white hover:bg-green-50 text-gray-700 border-l-4 border-l-green-500 border-y border-r border-gray-200'
    }
  },
  {
    value: TriageLevel.SIN_URGENCIA,
    label: 'SIN URGENCIA (Nivel 5)',
    description: 'Problemas administrativos o clínicos leves.',
    color: 'blue',
    icon: CheckCircle2,
    classes: {
      selected: 'bg-blue-600 text-white border-blue-600 shadow-blue-200 ring-2 ring-blue-300',
      default: 'bg-white hover:bg-blue-50 text-gray-700 border-l-4 border-l-blue-500 border-y border-r border-gray-200'
    }
  }
];

const Admission: React.FC = () => {
  const { register, handleSubmit, setValue, watch, getValues, reset, formState: { errors } } = useForm<AdmissionFormData>();
  const [loading, setLoading] = useState(false);
  const [searching, setSearching] = useState(false);
  const [searchPerformed, setSearchPerformed] = useState(false);
  const [isNewPatient, setIsNewPatient] = useState(false);
  const [isEditingPatient, setIsEditingPatient] = useState(false);
  const [submitError, setSubmitError] = useState('');
  const navigate = useNavigate();

  const currentTriageLevel = watch('nivelEmergencia');

  // Register the field manually since we use custom buttons
  useEffect(() => {
    register('nivelEmergencia', { required: true });
  }, [register]);

  const inputClass = "mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-primary-500 focus:border-primary-500 sm:text-sm transition-colors duration-200 disabled:bg-gray-100 disabled:text-gray-500";
  
  // Dynamic class for fields that can be locked
  const getEditableInputClass = (isLocked: boolean) => 
    `${inputClass} ${isLocked ? 'bg-gray-100 text-gray-600 cursor-not-allowed focus:ring-0 focus:border-gray-300' : 'bg-gray-50 focus:bg-white'}`;

  const handleSearchPatient = async () => {
    const cuil = getValues('pacienteCuil');
    if (!cuil || cuil.length < 7) {
        setSubmitError("Por favor ingrese un CUIL válido para buscar.");
        return;
    }
    
    setSearching(true);
    setSubmitError('');
    
    try {
        const patient = await apiRequest<Patient>(`/pacientes/${cuil}`);
        
        // Patient found
        setValue('pacienteNombre', patient.nombre);
        setValue('pacienteApellido', patient.apellido);
        if (patient.domicilio) {
            setValue('calle', patient.domicilio.calle);
            setValue('numero', patient.domicilio.numero);
            setValue('localidad', patient.domicilio.localidad);
        }
        
        // Populate Obra Social
        if (patient.obraSocial) {
            setValue('obraSocialNombre', patient.obraSocial.obraSocial.nombre);
            setValue('numeroAfiliado', patient.obraSocial.numeroAfiliado);
        } else {
            setValue('obraSocialNombre', '');
            setValue('numeroAfiliado', '');
        }
        
        setIsNewPatient(false);
        setIsEditingPatient(false); // Default to locked for existing patients
        setSearchPerformed(true);
    } catch (err) {
        if (err instanceof ApiError && err.status === 404) {
            // Patient not found
            setIsNewPatient(true);
            setIsEditingPatient(true); // Allow editing for new patients
            setSearchPerformed(true);
            // Clear fields just in case
            setValue('pacienteNombre', '');
            setValue('pacienteApellido', '');
            setValue('calle', '');
            setValue('numero', undefined);
            setValue('localidad', '');
            setValue('obraSocialNombre', '');
            setValue('numeroAfiliado', '');
        } else {
            setSubmitError(err instanceof Error ? err.message : 'Error al buscar paciente');
        }
    } finally {
        setSearching(false);
    }
  };

  const handleResetSearch = () => {
    reset();
    setSearchPerformed(false);
    setIsNewPatient(false);
    setIsEditingPatient(false);
    setSubmitError('');
  };

  const onSubmit = async (data: AdmissionFormData) => {
    setLoading(true);
    setSubmitError('');

    const payload = {
      pacienteCuil: data.pacienteCuil,
      pacienteNombre: data.pacienteNombre,
      pacienteApellido: data.pacienteApellido,
      pacienteEmail: data.pacienteEmail || undefined,
      pacienteDomicilio: (data.calle && data.numero) ? {
        calle: data.calle,
        numero: Number(data.numero),
        localidad: data.localidad || ''
      } : undefined,
      pacienteObraSocial: (data.obraSocialNombre) ? {
          obraSocial: { id: 0, nombre: data.obraSocialNombre }, // ID 0 placeholder/mock, backend should handle by name or we assume valid entry
          numeroAfiliado: data.numeroAfiliado || ''
      } : undefined,
      enfermeroCuil: 'CURRENT_USER', 
      descripcion: data.descripcion,
      temperatura: Number(data.temperatura),
      tensionSistolica: Number(data.tensionSistolica),
      tensionDiastolica: Number(data.tensionDiastolica),
      frecuenciaCardiaca: Number(data.frecuenciaCardiaca),
      frecuenciaRespiratoria: Number(data.frecuenciaRespiratoria),
      nivelEmergencia: data.nivelEmergencia
    };

    try {
        const profile = await apiRequest<User>('/auth/perfil');
        const finalPayload = {
            ...payload,
            enfermeroCuil: profile.cuil || payload.enfermeroCuil 
        }

        await apiRequest('/urgencias', {
            method: 'POST',
            body: JSON.stringify(finalPayload)
        });
        navigate('/queue');
    } catch (err) {
      setSubmitError(err instanceof Error ? err.message : 'Error al registrar ingreso');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white shadow sm:rounded-lg overflow-hidden">
      <div className="px-4 py-5 sm:px-6 bg-gray-50 border-b border-gray-200">
        <h3 className="text-lg leading-6 font-medium text-gray-900">Nuevo Ingreso de Urgencia</h3>
        <p className="mt-1 max-w-2xl text-sm text-gray-500">
            {!searchPerformed 
             ? 'Ingrese el CUIL del paciente para comenzar.' 
             : 'Complete los detalles del triage y signos vitales.'}
        </p>
      </div>
      
      <div className="px-4 py-5 sm:p-6">
        {submitError && (
            <div className="mb-6 bg-red-50 border-l-4 border-red-400 p-4">
                <div className="flex">
                    <div className="flex-shrink-0">
                        <AlertCircle className="h-5 w-5 text-red-400" />
                    </div>
                    <div className="ml-3">
                        <p className="text-sm text-red-700">{submitError}</p>
                    </div>
                </div>
            </div>
        )}
        
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-8">
          
          {/* Patient Identification Section */}
          <div>
            <div className="flex justify-between items-center mb-4 pb-2 border-b">
                 <h4 className="text-md font-bold text-gray-900">Identificación del Paciente</h4>
                 {searchPerformed && (
                     <button 
                        type="button" 
                        onClick={handleResetSearch}
                        className="text-sm text-gray-500 hover:text-red-600 flex items-center gap-1"
                     >
                         <X className="w-4 h-4" /> Cambiar Paciente
                     </button>
                 )}
            </div>

            <div className="grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-6 items-end">
              <div className="sm:col-span-3">
                <label className="block text-sm font-medium text-gray-700">CUIL del Paciente</label>
                <div className="mt-1 flex rounded-md shadow-sm">
                    <input 
                        {...register('pacienteCuil', { required: true })} 
                        placeholder="20-12345678-9" 
                        className={`${inputClass.replace('mt-1', '')} rounded-r-none ${searchPerformed ? 'bg-gray-100 text-gray-500 cursor-not-allowed' : 'bg-white text-gray-900'}`}
                        readOnly={searchPerformed}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter') {
                                e.preventDefault();
                                handleSearchPatient();
                            }
                        }}
                    />
                    <button
                        type="button"
                        onClick={handleSearchPatient}
                        disabled={searching || searchPerformed}
                        className={`-ml-px relative inline-flex items-center space-x-2 px-4 py-2 border border-gray-300 text-sm font-medium rounded-r-md text-gray-700 bg-gray-50 hover:bg-gray-100 focus:outline-none focus:ring-1 focus:ring-primary-500 focus:border-primary-500 ${searchPerformed ? 'cursor-not-allowed opacity-50' : ''}`}
                    >
                        {searching ? (
                            <span className="animate-spin h-5 w-5 border-2 border-gray-500 border-t-transparent rounded-full"></span>
                        ) : (
                            <>
                                <Search className="h-5 w-5 text-gray-400" />
                                <span>Buscar</span>
                            </>
                        )}
                    </button>
                </div>
                {errors.pacienteCuil && <span className="text-red-500 text-xs">El CUIL es requerido</span>}
              </div>
            </div>

            {/* Status Messages after Search */}
            {searchPerformed && (
                <div className={`mt-6 p-4 rounded-md ${isNewPatient ? 'bg-yellow-50 border border-yellow-200' : 'bg-blue-50 border border-blue-200'}`}>
                    <div className="flex justify-between items-center">
                        <div className="flex">
                            <div className="flex-shrink-0">
                                {isNewPatient ? (
                                    <UserPlus className="h-5 w-5 text-yellow-400" />
                                ) : (
                                    <UserCheck className="h-5 w-5 text-blue-400" />
                                )}
                            </div>
                            <div className="ml-3">
                                <h3 className={`text-sm font-medium ${isNewPatient ? 'text-yellow-800' : 'text-blue-800'}`}>
                                    {isNewPatient ? 'Paciente no encontrado' : 'Paciente encontrado'}
                                </h3>
                                <div className={`mt-2 text-sm ${isNewPatient ? 'text-yellow-700' : 'text-blue-700'}`}>
                                    <p>
                                        {isNewPatient 
                                            ? 'El paciente no existe en el sistema. Por favor complete los datos personales para registrarlo.' 
                                            : 'Verifique los datos. Si necesita actualizar el domicilio u Obra Social, presione Editar.'}
                                    </p>
                                </div>
                            </div>
                        </div>
                        
                        {/* Edit Button for existing patients */}
                        {!isNewPatient && !isEditingPatient && (
                            <button
                                type="button"
                                onClick={() => setIsEditingPatient(true)}
                                className="ml-4 flex-shrink-0 inline-flex items-center px-3 py-1.5 border border-blue-600 text-xs font-medium rounded text-blue-600 bg-white hover:bg-blue-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
                            >
                                <Pencil className="w-3 h-3 mr-1" />
                                Editar Datos
                            </button>
                        )}
                         {!isNewPatient && isEditingPatient && (
                            <span className="ml-4 flex-shrink-0 inline-flex items-center px-3 py-1.5 text-xs font-medium text-blue-800 bg-blue-100 rounded">
                                <Pencil className="w-3 h-3 mr-1" />
                                Editando...
                            </span>
                        )}
                    </div>
                </div>
            )}

            {/* Rest of Patient Info - Only visible after search */}
            {searchPerformed && (
                <div className="mt-6 animate-fade-in-down">
                    <div className="grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-6">
                        <div className="sm:col-span-3">
                            <label className="block text-sm font-medium text-gray-700">Nombre</label>
                            <input 
                                {...register('pacienteNombre', { required: true })} 
                                className={getEditableInputClass(!isEditingPatient)} 
                                readOnly={!isEditingPatient}
                            />
                            {errors.pacienteNombre && <span className="text-red-500 text-xs">Requerido</span>}
                        </div>
                        <div className="sm:col-span-3">
                            <label className="block text-sm font-medium text-gray-700">Apellido</label>
                            <input 
                                {...register('pacienteApellido', { required: true })} 
                                className={getEditableInputClass(!isEditingPatient)} 
                                readOnly={!isEditingPatient}
                            />
                            {errors.pacienteApellido && <span className="text-red-500 text-xs">Requerido</span>}
                        </div>
                    </div>
                    <div className="mt-4 grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-6">
                        <div className="sm:col-span-3">
                            <label className="block text-sm font-medium text-gray-700">Calle</label>
                            <input 
                                {...register('calle')} 
                                className={getEditableInputClass(!isEditingPatient)} 
                                readOnly={!isEditingPatient}
                            />
                        </div>
                        <div className="sm:col-span-1">
                            <label className="block text-sm font-medium text-gray-700">Número</label>
                            <input 
                                type="number" 
                                {...register('numero')} 
                                className={getEditableInputClass(!isEditingPatient)} 
                                readOnly={!isEditingPatient}
                            />
                        </div>
                        <div className="sm:col-span-2">
                            <label className="block text-sm font-medium text-gray-700">Localidad</label>
                            <input 
                                {...register('localidad')} 
                                className={getEditableInputClass(!isEditingPatient)} 
                                readOnly={!isEditingPatient}
                            />
                        </div>
                    </div>
                    
                    {/* Obra Social Section */}
                    <div className="mt-6 border-t border-gray-100 pt-4">
                        <h5 className="text-sm font-medium text-gray-900 mb-3 flex items-center gap-2">
                            <CreditCard className="w-4 h-4 text-gray-400" />
                            Obra Social / Cobertura
                        </h5>
                        <div className="grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-6">
                            <div className="sm:col-span-3">
                                <label className="block text-sm font-medium text-gray-700">Nombre Obra Social</label>
                                <input 
                                    {...register('obraSocialNombre')} 
                                    className={getEditableInputClass(!isEditingPatient)} 
                                    readOnly={!isEditingPatient}
                                    placeholder="Ej: OSDE, IOMA, PAMI"
                                />
                            </div>
                            <div className="sm:col-span-3">
                                <label className="block text-sm font-medium text-gray-700">Nro. Afiliado</label>
                                <input 
                                    {...register('numeroAfiliado')} 
                                    className={getEditableInputClass(!isEditingPatient)} 
                                    readOnly={!isEditingPatient}
                                />
                            </div>
                        </div>
                    </div>
                </div>
            )}
          </div>

          {/* Vitals & Triage Section - Only visible after search */}
          {searchPerformed && (
          <>
            <div>
                <h4 className="text-md font-bold text-gray-900 mb-4 pb-2 border-b">Signos Vitales</h4>
                
                <div className="grid grid-cols-2 gap-4 sm:grid-cols-5 mb-6">
                    <div>
                        <label className="block text-xs font-medium text-gray-500 uppercase">Temp (°C)</label>
                        <input type="number" step="0.1" {...register('temperatura', { required: true })} className={`${inputClass} bg-gray-50`} />
                    </div>
                    <div>
                        <label className="block text-xs font-medium text-gray-500 uppercase">T.A. Sistólica</label>
                        <input type="number" {...register('tensionSistolica', { required: true })} className={`${inputClass} bg-gray-50`} />
                    </div>
                    <div>
                        <label className="block text-xs font-medium text-gray-500 uppercase">T.A. Diastólica</label>
                        <input type="number" {...register('tensionDiastolica', { required: true })} className={`${inputClass} bg-gray-50`} />
                    </div>
                    <div>
                        <label className="block text-xs font-medium text-gray-500 uppercase">Frec. Cardíaca</label>
                        <input type="number" {...register('frecuenciaCardiaca', { required: true })} className={`${inputClass} bg-gray-50`} />
                    </div>
                    <div>
                        <label className="block text-xs font-medium text-gray-500 uppercase">Frec. Resp.</label>
                        <input type="number" {...register('frecuenciaRespiratoria', { required: true })} className={`${inputClass} bg-gray-50`} />
                    </div>
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700">Descripción / Síntomas</label>
                    <textarea rows={3} {...register('descripcion', { required: true })} className={`${inputClass} bg-gray-50`} placeholder="Describa el motivo de la consulta..." />
                    {errors.descripcion && <span className="text-red-500 text-xs">Requerido</span>}
                </div>
            </div>

            {/* Triage Section */}
            <div>
                <h4 className="text-md font-bold text-gray-900 mb-4 pb-2 border-b flex justify-between items-center">
                    <span>Nivel de Emergencia (Triage)</span>
                    {errors.nivelEmergencia && <span className="text-red-500 text-xs font-normal">Seleccione una opción</span>}
                </h4>
                
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
                {TRIAGE_OPTIONS.map((option) => {
                    const isSelected = currentTriageLevel === option.value;
                    return (
                    <button
                        key={option.value}
                        type="button"
                        onClick={() => setValue('nivelEmergencia', option.value, { shouldValidate: true })}
                        className={`
                        relative p-4 rounded-lg text-left transition-all duration-200 shadow-sm
                        flex flex-col h-full
                        ${isSelected ? option.classes.selected : option.classes.default}
                        `}
                    >
                        <div className="flex items-center justify-between mb-2">
                            <option.icon className={`w-6 h-6 ${isSelected ? 'text-white' : `text-${option.color}-500`}`} />
                            {isSelected && <div className="w-2 h-2 rounded-full bg-white animate-pulse" />}
                        </div>
                        <span className={`block font-bold text-sm mb-1 ${isSelected ? 'text-white' : 'text-gray-900'}`}>
                            {option.label}
                        </span>
                        <span className={`block text-xs leading-tight opacity-90 ${isSelected ? 'text-blue-50' : 'text-gray-500'}`}>
                            {option.description}
                        </span>
                    </button>
                    );
                })}
                </div>
            </div>

            <div className="pt-5 border-t border-gray-200">
                <div className="flex justify-end">
                <button
                    type="submit"
                    disabled={loading}
                    className="ml-3 inline-flex justify-center py-2 px-6 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 transition-all disabled:opacity-50"
                >
                    {loading ? 'Registrando...' : 'Confirmar Ingreso'}
                </button>
                </div>
            </div>
          </>
          )}
        </form>
      </div>
    </div>
  );
};

export default Admission;