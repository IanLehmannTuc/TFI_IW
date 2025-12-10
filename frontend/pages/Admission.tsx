import React, { useState, useEffect, useRef } from 'react';
import { useForm } from 'react-hook-form';
import { apiRequest, ApiError } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { TriageLevel, Patient, ObraSocialRef, AdmissionRequest } from '../types';
import { useNavigate } from 'react-router-dom';
import { AlertCircle, CheckCircle2, Clock, AlertTriangle, Search, UserPlus, UserCheck, X, Zap, ThumbsUp, ChevronDown } from 'lucide-react';

interface AdmissionFormData {

  pacienteCuil: string;
  pacienteNombre: string;
  pacienteApellido: string;
  calle?: string;
  numero?: number;
  localidad?: string;


  obraSocialId?: number; 
  obraSocialNombre?: string;
  numeroAfiliado?: string;


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
    label: 'CRÍTICA',
    description: 'Riesgo vital inmediato. Resucitación.',
    color: 'red',
    icon: Zap,
    classes: {
      selected: 'bg-red-600 text-white border-red-600 shadow-red-200 ring-2 ring-red-300',
      default: 'bg-white hover:bg-red-50 text-gray-700 border-l-4 border-l-red-500 border-y border-r border-gray-200'
    }
  },
  {
    value: TriageLevel.EMERGENCIA,
    label: 'EMERGENCIA',
    description: 'Atención Inmediata. Situación muy grave.',
    color: 'orange',
    icon: AlertTriangle,
    classes: {
      selected: 'bg-orange-600 text-white border-orange-600 shadow-orange-200 ring-2 ring-orange-300',
      default: 'bg-white hover:bg-orange-50 text-gray-700 border-l-4 border-l-orange-500 border-y border-r border-gray-200'
    }
  },
  {
    value: TriageLevel.URGENCIA,
    label: 'URGENCIA',
    description: 'Muy Urgente (30-60 min).',
    color: 'yellow',
    icon: Clock,
    classes: {
      selected: 'bg-yellow-500 text-white border-yellow-500 shadow-yellow-200 ring-2 ring-yellow-300',
      default: 'bg-white hover:bg-yellow-50 text-gray-700 border-l-4 border-l-yellow-500 border-y border-r border-gray-200'
    }
  },
  {
    value: TriageLevel.URGENCIA_MENOR,
    label: 'URGENCIA MENOR',
    description: 'Semi-urgente. Consulta estándar.',
    color: 'green',
    icon: CheckCircle2,
    classes: {
      selected: 'bg-green-600 text-white border-green-600 shadow-green-200 ring-2 ring-green-300',
      default: 'bg-white hover:bg-green-50 text-gray-700 border-l-4 border-l-green-500 border-y border-r border-gray-200'
    }
  },
  {
    value: TriageLevel.SIN_URGENCIA,
    label: 'SIN URGENCIA',
    description: 'No urgente. Consulta programada.',
    color: 'blue',
    icon: ThumbsUp,
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
  const [submitError, setSubmitError] = useState('');
  const { user } = useAuth();
  const navigate = useNavigate();


  const [obrasSocialesList, setObrasSocialesList] = useState<ObraSocialRef[]>([]);
  const [osQuery, setOsQuery] = useState('');
  const [isOsDropdownOpen, setIsOsDropdownOpen] = useState(false);
  const [_osFetchError, setOsFetchError] = useState('');
  const [osLoading, setOsLoading] = useState(false);
  const osDropdownRef = useRef<HTMLDivElement>(null);

  const currentTriageLevel = watch('nivelEmergencia');

  useEffect(() => {
    register('nivelEmergencia', { required: true });
  }, [register]);

  const fetchOS = async () => {
    setOsLoading(true);
    setOsFetchError('');
    try {
      const data = await apiRequest<ObraSocialRef[]>('/obras-sociales');
      if (Array.isArray(data)) {
        setObrasSocialesList(data);
      } else {
        setObrasSocialesList([]);
      }
    } catch (e) {
      console.error("Error cargando obras sociales", e);
      setOsFetchError('Error de conexión');
      setObrasSocialesList([]);
    } finally {
      setOsLoading(false);
    }
  };

  useEffect(() => {
    fetchOS();
  }, []);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (osDropdownRef.current && !osDropdownRef.current.contains(event.target as Node)) {
        setIsOsDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const inputClass = "mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-primary-500 focus:border-primary-500 sm:text-sm transition-colors duration-200 disabled:bg-gray-100 disabled:text-gray-500";


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


        setValue('pacienteNombre', patient.nombre);
        setValue('pacienteApellido', patient.apellido);

        if (patient.domicilio) {
            setValue('calle', patient.domicilio.calle);
            setValue('numero', patient.domicilio.numero);
            setValue('localidad', patient.domicilio.localidad);
        }

        if (patient.obraSocial) {
            setOsQuery(patient.obraSocial.obraSocial.nombre);
            setValue('obraSocialId', patient.obraSocial.obraSocial.id);
            setValue('obraSocialNombre', patient.obraSocial.obraSocial.nombre);
            setValue('numeroAfiliado', patient.obraSocial.numeroAfiliado);
        } else {
            setOsQuery('');
            setValue('obraSocialId', undefined);
            setValue('obraSocialNombre', '');
            setValue('numeroAfiliado', '');
        }

        setIsNewPatient(false);
        setSearchPerformed(true);
    } catch (err) {
        if (err instanceof ApiError && err.status === 404) {

            setIsNewPatient(true);
            setSearchPerformed(true);


            setValue('pacienteNombre', '');
            setValue('pacienteApellido', '');
            setValue('calle', '');
            setValue('numero', undefined);
            setValue('localidad', '');
            setOsQuery('');
            setValue('obraSocialId', undefined);
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

    const currentValues = getValues();
    reset({
        ...currentValues,
        pacienteCuil: currentValues.pacienteCuil, 
        pacienteNombre: '',
        pacienteApellido: '',
        calle: '',
        numero: undefined,
        localidad: '',
        numeroAfiliado: '',
        obraSocialId: undefined
    });
    setOsQuery('');
    setSearchPerformed(false);
    setIsNewPatient(false);
    setSubmitError('');
  };

  const onSubmit = async (data: AdmissionFormData) => {
    setLoading(true);
    setSubmitError('');

    if (!user?.cuil) {
        setSubmitError("No se ha podido identificar su CUIL de enfermero. Por favor, reinicie sesión.");
        setLoading(false);
        return;
    }

    try {


        let osPayload = undefined;

        const finalOsName = osQuery;
        const finalOsId = data.obraSocialId || obrasSocialesList.find(o => o.nombre === finalOsName)?.id;

        if (finalOsName && data.numeroAfiliado) {
             osPayload = {
                 obraSocial: { id: finalOsId, nombre: finalOsName },
                 numeroAfiliado: data.numeroAfiliado
             };
        }

        const admissionPayload: AdmissionRequest = {
            pacienteCuil: data.pacienteCuil,

            pacienteNombre: data.pacienteNombre,
            pacienteApellido: data.pacienteApellido,
            pacienteDomicilio: (data.calle && data.numero) ? {
                calle: data.calle,
                numero: Math.abs(Number(data.numero)),
                localidad: data.localidad || ''
            } : undefined,
            pacienteObraSocial: osPayload,

            enfermeroCuil: user.cuil, 

            descripcion: data.descripcion,
            temperatura: Math.abs(Number(data.temperatura)),
            tensionSistolica: Math.abs(Number(data.tensionSistolica)),
            tensionDiastolica: Math.abs(Number(data.tensionDiastolica)),
            frecuenciaCardiaca: Math.abs(Number(data.frecuenciaCardiaca)),
            frecuenciaRespiratoria: Math.abs(Number(data.frecuenciaRespiratoria)),
            nivelEmergencia: data.nivelEmergencia
        };

        await apiRequest('/ingresos', {
            method: 'POST',
            body: JSON.stringify(admissionPayload)
        });

        navigate('/queue');

    } catch (err) {
      setSubmitError(err instanceof Error ? err.message : 'Error al registrar ingreso');
    } finally {
      setLoading(false);
    }
  };


  const filteredOS = obrasSocialesList.filter(os => 
    os.nombre.toLowerCase().includes(osQuery.toLowerCase())
  );

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
                                            ? 'Complete los datos a continuación para registrar al paciente junto con su ingreso.' 
                                            : 'Los datos del paciente han sido cargados automáticamente.'}
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {searchPerformed && (
                <div className="mt-6 animate-fade-in-down">
                    <div className="grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-6">
                        <div className="sm:col-span-3">
                            <label className="block text-sm font-medium text-gray-700">Nombre {isNewPatient && '*'}</label>
                            <input 
                                {...register('pacienteNombre', { required: isNewPatient })} 
                                className={getEditableInputClass(!isNewPatient)} 
                                readOnly={!isNewPatient}
                            />
                             {errors.pacienteNombre && <span className="text-red-500 text-xs">Requerido</span>}
                        </div>
                        <div className="sm:col-span-3">
                            <label className="block text-sm font-medium text-gray-700">Apellido {isNewPatient && '*'}</label>
                            <input 
                                {...register('pacienteApellido', { required: isNewPatient })} 
                                className={getEditableInputClass(!isNewPatient)} 
                                readOnly={!isNewPatient}
                            />
                             {errors.pacienteApellido && <span className="text-red-500 text-xs">Requerido</span>}
                        </div>
                    </div>

                    <div className="mt-4 pt-4 border-t border-gray-100 grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-6">
                        <div className="sm:col-span-3">
                            <label className="block text-sm font-medium text-gray-700">Calle {isNewPatient && '*'}</label>
                            <input 
                                {...register('calle', { required: isNewPatient })} 
                                className={getEditableInputClass(!isNewPatient)} 
                                readOnly={!isNewPatient}
                            />
                            {errors.calle && <span className="text-red-500 text-xs">Requerido</span>}
                        </div>
                        <div className="sm:col-span-1">
                            <label className="block text-sm font-medium text-gray-700">Número {isNewPatient && '*'}</label>
                            <input 
                                type="number"
                                min="0"
                                {...register('numero', { required: isNewPatient })} 
                                className={getEditableInputClass(!isNewPatient)} 
                                readOnly={!isNewPatient}
                            />
                            {errors.numero && <span className="text-red-500 text-xs">Requerido</span>}
                        </div>
                        <div className="sm:col-span-2">
                            <label className="block text-sm font-medium text-gray-700">Localidad {isNewPatient && '*'}</label>
                            <input 
                                {...register('localidad', { required: isNewPatient })} 
                                className={getEditableInputClass(!isNewPatient)} 
                                readOnly={!isNewPatient}
                            />
                            {errors.localidad && <span className="text-red-500 text-xs">Requerido</span>}
                        </div>
                    </div>

                    <div className="mt-4 pt-4 border-t border-gray-100 grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-6">
                         <div className="sm:col-span-3 relative" ref={osDropdownRef}>
                             <label className="block text-sm font-medium text-gray-700">Obra Social</label>
                             <input type="hidden" {...register('obraSocialId')} />
                             <div className="relative mt-1">
                                 <input
                                     type="text"
                                     className={`${getEditableInputClass(!isNewPatient)} pr-10`}
                                     placeholder="Buscar..."
                                     value={osQuery}
                                     readOnly={!isNewPatient}
                                     onChange={(e) => {
                                         setOsQuery(e.target.value);
                                         setIsOsDropdownOpen(true);
                                         setValue('obraSocialId', undefined);
                                     }}
                                     onFocus={() => {
                                         if (isNewPatient) {
                                             if (obrasSocialesList.length === 0 && !osLoading) fetchOS();
                                             setIsOsDropdownOpen(true);
                                         }
                                     }}
                                 />
                                 {isNewPatient && (
                                    <div className="absolute inset-y-0 right-0 flex items-center pr-2 pointer-events-none">
                                        <ChevronDown className="h-4 w-4 text-gray-400" />
                                    </div>
                                 )}
                             </div>
                             {isOsDropdownOpen && isNewPatient && (
                                <div className="absolute z-10 mt-1 w-full bg-white shadow-lg max-h-60 rounded-md py-1 text-base ring-1 ring-black ring-opacity-5 overflow-auto focus:outline-none sm:text-sm">
                                    {filteredOS.length === 0 ? (
                                        <div className="cursor-default select-none relative py-2 px-4 text-gray-700">
                                            {osLoading ? 'Cargando...' : 'No encontrado'}
                                        </div>
                                    ) : (
                                        filteredOS.map((os) => (
                                            <div
                                                key={os.id}
                                                className="cursor-pointer select-none relative py-2 pl-3 pr-9 hover:bg-primary-50 text-gray-900"
                                                onClick={() => {
                                                    setOsQuery(os.nombre);
                                                    setValue('obraSocialId', os.id);
                                                    setIsOsDropdownOpen(false);
                                                }}
                                            >
                                                <span className="block truncate">{os.nombre}</span>
                                            </div>
                                        ))
                                    )}
                                </div>
                             )}
                         </div>
                         <div className="sm:col-span-3">
                             <label className="block text-sm font-medium text-gray-700">Nro Afiliado</label>
                             <input 
                                {...register('numeroAfiliado')} 
                                className={getEditableInputClass(!isNewPatient)} 
                                readOnly={!isNewPatient}
                            />
                         </div>
                    </div>
                </div>
            )}
          </div>

          {searchPerformed && (
          <>
            <div>
                <h4 className="text-md font-bold text-gray-900 mb-4 pb-2 border-b">Signos Vitales</h4>

                <div className="grid grid-cols-2 gap-4 sm:grid-cols-5 mb-6">
                    <div>
                        <label className="block text-xs font-medium text-gray-500 uppercase">Temp (°C)</label>
                        <input 
                            type="number" 
                            step="0.1" 
                            min="0"
                            {...register('temperatura', { required: 'Requerido' })} 
                            className={`${inputClass} bg-gray-50 ${errors.temperatura ? 'border-red-500' : ''}`} 
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-medium text-gray-500 uppercase">Sistólica (mmHg)</label>
                        <input 
                            type="number" 
                            min="0"
                            placeholder="120"
                            {...register('tensionSistolica', { required: 'Requerido' })} 
                            className={`${inputClass} bg-gray-50 ${errors.tensionSistolica ? 'border-red-500' : ''}`} 
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-medium text-gray-500 uppercase">Diastólica (mmHg)</label>
                        <input 
                            type="number" 
                            min="0"
                            placeholder="80"
                            {...register('tensionDiastolica', { required: 'Requerido' })} 
                            className={`${inputClass} bg-gray-50 ${errors.tensionDiastolica ? 'border-red-500' : ''}`} 
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-medium text-gray-500 uppercase">Frec. Cardíaca</label>
                        <input 
                            type="number" 
                            min="0"
                            {...register('frecuenciaCardiaca', { required: 'Requerido' })} 
                            className={`${inputClass} bg-gray-50 ${errors.frecuenciaCardiaca ? 'border-red-500' : ''}`} 
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-medium text-gray-500 uppercase">Frec. Resp.</label>
                        <input 
                            type="number" 
                            min="0"
                            {...register('frecuenciaRespiratoria', { required: 'Requerido' })} 
                            className={`${inputClass} bg-gray-50 ${errors.frecuenciaRespiratoria ? 'border-red-500' : ''}`} 
                        />
                    </div>
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700">Descripción / Síntomas</label>
                    <textarea rows={3} {...register('descripcion', { required: true })} className={`${inputClass} bg-gray-50`} placeholder="Describa el motivo de la consulta..." />
                    {errors.descripcion && <span className="text-red-500 text-xs">Requerido</span>}
                </div>
            </div>

            <div>
                <h4 className="text-md font-bold text-gray-900 mb-4 pb-2 border-b flex justify-between items-center">
                    <span>Nivel de Emergencia (Triage)</span>
                    {errors.nivelEmergencia && <span className="text-red-500 text-xs font-normal">Seleccione una opción</span>}
                </h4>

                <div className="grid grid-cols-1 sm:grid-cols-3 lg:grid-cols-5 gap-3">
                {TRIAGE_OPTIONS.map((option) => {
                    const isSelected = currentTriageLevel === option.value;
                    return (
                    <button
                        key={option.value}
                        type="button"
                        onClick={() => setValue('nivelEmergencia', option.value, { shouldValidate: true })}
                        className={`
                        relative p-3 rounded-lg text-left transition-all duration-200 shadow-sm
                        flex flex-col h-full
                        ${isSelected ? option.classes.selected : option.classes.default}
                        `}
                    >
                        <div className="flex items-center justify-between mb-2">
                            <option.icon className={`w-5 h-5 ${isSelected ? 'text-white' : `text-${option.color}-500`}`} />
                            {isSelected && <div className="w-2 h-2 rounded-full bg-white animate-pulse" />}
                        </div>
                        <span className={`block font-bold text-xs mb-1 uppercase ${isSelected ? 'text-white' : 'text-gray-900'}`}>
                            {option.label}
                        </span>
                        <span className={`block text-[10px] leading-tight opacity-90 ${isSelected ? 'text-blue-50' : 'text-gray-500'}`}>
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