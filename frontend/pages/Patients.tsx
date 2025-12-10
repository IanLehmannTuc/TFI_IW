import React, { useEffect, useState, useRef } from 'react';
import { useForm } from 'react-hook-form';
import { apiRequest } from '../services/api';
import { Patient, Page, ObraSocialRef } from '../types';
import { 
  Search, 
  Plus, 
  Pencil, 
  X, 
  MapPin, 
  CreditCard, 
  AlertCircle,
  RefreshCcw,
  ChevronLeft,
  ChevronRight,
  CheckCircle,
  ChevronDown
} from 'lucide-react';

interface PatientFormData {
  nombre: string;
  apellido: string;
  cuil: string;

  calle?: string;
  numero?: number;
  localidad?: string;


  obraSocialId?: number; 
  numeroAfiliado?: string;
}

const Patients: React.FC = () => {
  const [patients, setPatients] = useState<Patient[]>([]);
  const [loading, setLoading] = useState(true);


  const [page, setPage] = useState(0);
  const [pageSize] = useState(10); 
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [sortBy, setSortBy] = useState('cuil');
  const [sortDirection, setSortDirection] = useState<'ASC' | 'DESC'>('ASC');


  const [searchTerm, setSearchTerm] = useState('');


  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentPatient, setCurrentPatient] = useState<Patient | null>(null);
  const [operationLoading, setOperationLoading] = useState(false);
  const [operationError, setOperationError] = useState('');


  const [obrasSocialesList, setObrasSocialesList] = useState<ObraSocialRef[]>([]);
  const [osQuery, setOsQuery] = useState('');
  const [isOsDropdownOpen, setIsOsDropdownOpen] = useState(false);
  const [_osFetchError, setOsFetchError] = useState('');
  const [osLoading, setOsLoading] = useState(false);
  const osDropdownRef = useRef<HTMLDivElement>(null);

  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const { register, handleSubmit, reset, setValue } = useForm<PatientFormData>();

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
      setOsFetchError('Error de conexión con obras sociales.');
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

  const showSuccessNotification = (message: string) => {
      setSuccessMessage(message);
      setTimeout(() => {
          setSuccessMessage(null);
      }, 3000);
  };

  const fetchPatients = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams({
        page: page.toString(),
        size: pageSize.toString(),
        sortBy: sortBy,  
        direction: sortDirection
      });


      const response = await apiRequest<Page<Patient>>(`/pacientes?${params.toString()}`);

      setPatients(response.content);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (e) {
      console.error("Error fetching patients", e);
      setPatients([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPatients();
  }, [page, sortBy, sortDirection]);


  const filteredPatients = patients.filter(
    (p) =>
      p.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.apellido.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.cuil.includes(searchTerm)
  );

  const handleSort = (field: string) => {
    if (sortBy === field) {
        setSortDirection(prev => prev === 'ASC' ? 'DESC' : 'ASC');
    } else {
        setSortBy(field);
        setSortDirection('ASC');
    }
    setPage(0);
  };

  const handlePrevPage = () => { if (page > 0) setPage(p => p - 1); };
  const handleNextPage = () => { if (page < totalPages - 1) setPage(p => p + 1); };

  const handleOpenCreate = () => {
    setCurrentPatient(null);
    reset({});
    setOsQuery('');
    setValue('obraSocialId', undefined);
    setOperationError('');
    setIsModalOpen(true);
    if (obrasSocialesList.length === 0 && !osLoading) fetchOS();
  };

  const handleOpenEdit = (patient: Patient) => {
    setCurrentPatient(patient);
    setOperationError('');

    setValue('nombre', patient.nombre);
    setValue('apellido', patient.apellido);
    setValue('cuil', patient.cuil);

    if (patient.domicilio) {
      setValue('calle', patient.domicilio.calle);
      setValue('numero', patient.domicilio.numero);
      setValue('localidad', patient.domicilio.localidad);
    } else {
        setValue('calle', '');
        setValue('numero', undefined);
        setValue('localidad', '');
    }

    if (patient.obraSocial) {
      setOsQuery(patient.obraSocial.obraSocial?.nombre || '');
      setValue('obraSocialId', patient.obraSocial.obraSocial?.id);
      setValue('numeroAfiliado', patient.obraSocial.numeroAfiliado);
    } else {
        setOsQuery('');
        setValue('obraSocialId', undefined);
        setValue('numeroAfiliado', '');
    }

    setIsModalOpen(true);
  };

  const onSubmit = async (data: PatientFormData) => {
    setOperationLoading(true);
    setOperationError('');

    let finalOsId = data.obraSocialId;
    let finalOsName = osQuery;

    if (osQuery.trim() !== '') {
        if (!finalOsId) {
            const match = obrasSocialesList.find(os => os.nombre.toLowerCase() === osQuery.toLowerCase());
            if (match) {
                finalOsId = match.id;
                finalOsName = match.nombre;
            } else {
                setOperationError("Debe seleccionar una Obra Social válida de la lista o dejar el campo vacío.");
                setOperationLoading(false);
                return;
            }
        }
        if (finalOsId && !data.numeroAfiliado) {
            setOperationError("El número de afiliado es obligatorio para verificar la cobertura.");
            setOperationLoading(false);
            return;
        }
    } else {
        finalOsId = undefined;
    }

    const payload = {
      nombre: data.nombre,
      apellido: data.apellido,
      cuil: data.cuil,
      domicilio: (data.calle && data.numero) ? {
        calle: data.calle,
        numero: Number(data.numero),
        localidad: data.localidad || ''
      } : null,
      obraSocial: (finalOsId) ? {
        obraSocial: { 
            id: Number(finalOsId), 
            nombre: finalOsName
        },
        numeroAfiliado: data.numeroAfiliado || ''
      } : null
    };

    try {
      if (currentPatient) {

         await apiRequest(`/pacientes/${currentPatient.cuil}`, {
          method: 'PUT',
          body: JSON.stringify(payload)
        });
        showSuccessNotification('Paciente actualizado exitosamente');
      } else {

        await apiRequest('/pacientes', {
          method: 'POST',
          body: JSON.stringify(payload)
        });
        showSuccessNotification('Paciente creado exitosamente');
      }

      await fetchPatients();
      setIsModalOpen(false);
    } catch (err) {
      setOperationError(err instanceof Error ? err.message : 'Error al guardar el paciente');
    } finally {
      setOperationLoading(false);
    }
  };

  const inputClass = "mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-primary-500 focus:border-primary-500 sm:text-sm bg-gray-50 focus:bg-white transition-colors duration-200 disabled:bg-gray-100 disabled:text-gray-500";
  const filteredOS = obrasSocialesList.filter(os => os.nombre.toLowerCase().includes(osQuery.toLowerCase()));

  return (
    <div className="space-y-6 relative h-full flex flex-col">
      <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4">
        <div>
            <h1 className="text-2xl font-bold text-gray-900">Gestión de Pacientes</h1>
            <p className="text-sm text-gray-500">Base de datos unificada del hospital</p>
        </div>
        <div className="flex items-center gap-2">
            <button 
                onClick={fetchPatients}
                className="p-2 bg-white border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 transition-colors"
                title="Actualizar lista"
            >
                <RefreshCcw className={`w-5 h-5 ${loading ? 'animate-spin' : ''}`} />
            </button>
            <button 
                onClick={handleOpenCreate}
                className="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 shadow-sm transition-colors"
            >
                <Plus className="w-5 h-5" />
                <span>Nuevo Paciente</span>
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
                placeholder="Filtrar en esta página (Nombre, Apellido, CUIL)..."
                className="pl-10 block w-full sm:text-sm border-gray-300 rounded-md py-2 border bg-gray-50 focus:bg-white focus:ring-primary-500 focus:border-primary-500 transition-colors"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
            />
        </div>
      </div>

      <div className="bg-white shadow overflow-hidden sm:rounded-lg border border-gray-200 flex-1 flex flex-col">
        <div className="overflow-x-auto flex-1">
            <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
                <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100" onClick={() => handleSort('nombre')}>
                        Paciente {sortBy === 'nombre' && (sortDirection === 'ASC' ? '↑' : '↓')}
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100" onClick={() => handleSort('cuil')}>
                        CUIL {sortBy === 'cuil' && (sortDirection === 'ASC' ? '↑' : '↓')}
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Domicilio</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Obra Social</th>
                    <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Acciones</th>
                </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
                {loading ? (
                    <tr><td colSpan={5} className="px-6 py-10 text-center text-gray-500">Cargando datos...</td></tr>
                ) : filteredPatients.length === 0 ? (
                    <tr><td colSpan={5} className="px-6 py-10 text-center text-gray-500">No se encontraron pacientes.</td></tr>
                ) : (
                    filteredPatients.map((patient) => (
                    <tr key={patient.id || patient.cuil} className="hover:bg-gray-50 transition-colors">
                        <td className="px-6 py-4 whitespace-nowrap">
                            <div className="flex items-center">
                                <div className="h-8 w-8 rounded-full bg-primary-100 flex items-center justify-center text-primary-600 font-bold text-xs mr-3">
                                    {patient.nombre.charAt(0)}{patient.apellido.charAt(0)}
                                </div>
                                <div>
                                    <div className="text-sm font-medium text-gray-900">{patient.nombre} {patient.apellido}</div>
                                </div>
                            </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{patient.cuil}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                            {patient.domicilio ? `${patient.domicilio.calle} ${patient.domicilio.numero}` : <span className="text-gray-400 italic">No registrado</span>}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                             {patient.obraSocial ? (
                                <div className="flex flex-col">
                                    <span className="font-medium text-gray-700">{patient.obraSocial.obraSocial?.nombre || 'S/D'}</span>
                                    <span className="text-xs text-gray-400">{patient.obraSocial.numeroAfiliado}</span>
                                </div>
                            ) : <span className="text-gray-400 italic">Particular</span>}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                            <button onClick={() => handleOpenEdit(patient)} className="p-1.5 bg-blue-50 text-blue-600 rounded hover:bg-blue-100 transition-colors">
                                <Pencil className="w-4 h-4" />
                            </button>
                        </td>
                    </tr>
                    ))
                )}
            </tbody>
            </table>
        </div>
        <div className="bg-gray-50 px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6">
            <div className="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
                <div>
                    <p className="text-sm text-gray-700">
                        Mostrando <span className="font-medium">{Math.min((page * pageSize) + 1, totalElements)}</span> a <span className="font-medium">{Math.min((page + 1) * pageSize, totalElements)}</span> de <span className="font-medium">{totalElements}</span>
                    </p>
                </div>
                <div>
                    <nav className="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
                        <button onClick={handlePrevPage} disabled={page === 0} className={`relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium ${page === 0 ? 'text-gray-300 cursor-not-allowed' : 'text-gray-500 hover:bg-gray-50'}`}>
                            <ChevronLeft className="h-5 w-5" />
                        </button>
                        <span className="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700">Página {page + 1} de {totalPages || 1}</span>
                        <button onClick={handleNextPage} disabled={page >= totalPages - 1} className={`relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium ${page >= totalPages - 1 ? 'text-gray-300 cursor-not-allowed' : 'text-gray-500 hover:bg-gray-50'}`}>
                            <ChevronRight className="h-5 w-5" />
                        </button>
                    </nav>
                </div>
            </div>
        </div>
      </div>

      {successMessage && (
          <div className="fixed bottom-4 right-4 z-50 animate-fade-in-up">
              <div className="bg-green-600 text-white px-6 py-4 rounded-lg shadow-lg flex items-center gap-3">
                  <CheckCircle className="w-6 h-6" />
                  <span className="font-medium">{successMessage}</span>
                  <button onClick={() => setSuccessMessage(null)} className="ml-4 text-green-200 hover:text-white"><X className="w-4 h-4" /></button>
              </div>
          </div>
      )}

      {isModalOpen && (
          <div className="fixed inset-0 z-50 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
            <div className="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
                <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" onClick={() => setIsModalOpen(false)}></div>
                <div className="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-2xl w-full">
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <div className="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4 border-b border-gray-100">
                            <div className="flex justify-between items-center">
                                <h3 className="text-lg leading-6 font-medium text-gray-900">
                                    {currentPatient ? 'Editar Paciente' : 'Nuevo Paciente'}
                                </h3>
                                <button type="button" onClick={() => setIsModalOpen(false)} className="text-gray-400 hover:text-gray-500"><X className="h-6 w-6" /></button>
                            </div>
                        </div>

                        <div className="px-4 py-5 sm:p-6 bg-gray-50 max-h-[70vh] overflow-y-auto">
                            {operationError && (
                                <div className="mb-4 bg-red-50 p-4 rounded-md flex items-start gap-3">
                                    <AlertCircle className="w-5 h-5 text-red-400 mt-0.5" />
                                    <span className="text-sm text-red-800">{operationError}</span>
                                </div>
                            )}

                            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
                                <div className="sm:col-span-2">
                                    <h4 className="text-sm font-bold text-gray-900 uppercase tracking-wider mb-3">Datos Personales</h4>
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700">Nombre</label>
                                    <input {...register('nombre', { required: true })} className={inputClass} />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-700">Apellido</label>
                                    <input {...register('apellido', { required: true })} className={inputClass} />
                                </div>
                                <div className="sm:col-span-2">
                                    <label className="block text-sm font-medium text-gray-700">CUIL</label>
                                    <input {...register('cuil', { required: true })} className={inputClass} placeholder="20-12345678-9" readOnly={!!currentPatient} />
                                </div>

                                <div className="sm:col-span-2 pt-4 border-t border-gray-200">
                                    <h4 className="text-sm font-bold text-gray-900 uppercase tracking-wider mb-3 flex items-center gap-2"><MapPin className="w-4 h-4 text-gray-400" /> Domicilio</h4>
                                </div>
                                <div className="sm:col-span-1">
                                    <label className="block text-sm font-medium text-gray-700">Calle</label>
                                    <input {...register('calle')} className={inputClass} />
                                </div>
                                <div className="sm:col-span-1">
                                    <label className="block text-sm font-medium text-gray-700">Número</label>
                                    <input type="number" {...register('numero')} className={inputClass} />
                                </div>
                                <div className="sm:col-span-2">
                                    <label className="block text-sm font-medium text-gray-700">Localidad</label>
                                    <input {...register('localidad')} className={inputClass} />
                                </div>

                                <div className="sm:col-span-2 pt-4 border-t border-gray-200">
                                    <h4 className="text-sm font-bold text-gray-900 uppercase tracking-wider mb-3 flex items-center gap-2"><CreditCard className="w-4 h-4 text-gray-400" /> Obra Social</h4>
                                </div>
                                <div className="sm:col-span-1 relative" ref={osDropdownRef}>
                                    <label className="block text-sm font-medium text-gray-700">Obra Social</label>
                                    <input type="hidden" {...register('obraSocialId')} />
                                    <div className="relative mt-1">
                                        <input
                                            type="text"
                                            className={`${inputClass} mt-0 pr-10`}
                                            placeholder="Buscar..."
                                            value={osQuery}
                                            onChange={(e) => {
                                                setOsQuery(e.target.value);
                                                setIsOsDropdownOpen(true);
                                                setValue('obraSocialId', undefined);
                                            }}
                                            onFocus={() => {
                                                if (obrasSocialesList.length === 0 && !osLoading) fetchOS();
                                                setIsOsDropdownOpen(true);
                                            }}
                                        />
                                        <div className="absolute inset-y-0 right-0 flex items-center pr-2 pointer-events-none"><ChevronDown className="h-4 w-4 text-gray-400" /></div>
                                    </div>
                                    {isOsDropdownOpen && (
                                        <div className="absolute z-10 mt-1 w-full bg-white shadow-lg max-h-60 rounded-md py-1 text-base ring-1 ring-black ring-opacity-5 overflow-auto focus:outline-none sm:text-sm">
                                            {filteredOS.length === 0 ? <div className="py-2 px-4 text-gray-700">{osLoading ? 'Cargando...' : 'No encontrado'}</div> : filteredOS.map((os) => (
                                                <div key={os.id} className="cursor-pointer select-none relative py-2 pl-3 pr-9 hover:bg-primary-50 text-gray-900" onClick={() => {
                                                    setOsQuery(os.nombre);
                                                    setValue('obraSocialId', os.id);
                                                    setIsOsDropdownOpen(false);
                                                }}><span className="block truncate">{os.nombre}</span></div>
                                            ))}
                                        </div>
                                    )}
                                </div>
                                <div className="sm:col-span-1">
                                    <label className="block text-sm font-medium text-gray-700">Nro Afiliado</label>
                                    <input {...register('numeroAfiliado')} className={inputClass} />
                                </div>
                            </div>
                        </div>

                        <div className="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse border-t border-gray-200">
                            <button type="submit" disabled={operationLoading} className="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-primary-600 text-base font-medium text-white hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 sm:ml-3 sm:w-auto sm:text-sm disabled:opacity-50">
                                {operationLoading ? 'Guardando...' : 'Guardar'}
                            </button>
                            <button type="button" className="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm" onClick={() => setIsModalOpen(false)}>
                                Cancelar
                            </button>
                        </div>
                    </form>
                </div>
            </div>
          </div>
      )}
    </div>
  );
};

export default Patients;