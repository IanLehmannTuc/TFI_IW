import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { apiRequest } from '../services/api';
import { UserRole } from '../types';
import { Activity } from 'lucide-react';

const Register: React.FC = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    nombre: '',
    apellido: '',
    cuil: '',
    matricula: '', 
    autoridad: UserRole.ENFERMERO
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // IS2025-005: Registro
      await apiRequest('/auth/registro', {
        method: 'POST',
        body: JSON.stringify(formData),
      });
      // On success (201)
      navigate('/login');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al registrar usuario');
    } finally {
      setLoading(false);
    }
  };

  const inputClass = "mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-primary-500 focus:border-primary-500 sm:text-sm bg-gray-50 focus:bg-white transition-colors duration-200";

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="flex justify-center">
            <div className="bg-primary-600 p-3 rounded-xl shadow-lg">
                <Activity className="h-10 w-10 text-white" />
            </div>
        </div>
        <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
          Crear Cuenta
        </h2>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10 border border-gray-100">
          <form className="space-y-4" onSubmit={handleSubmit}>
            {error && (
              <div className="p-3 bg-red-100 text-red-700 text-sm rounded border border-red-200">
                {error}
              </div>
            )}

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700">Nombre</label>
                <input name="nombre" type="text" required onChange={handleChange} className={inputClass} />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">Apellido</label>
                <input name="apellido" type="text" required onChange={handleChange} className={inputClass} />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">CUIL (XX-XXXXXXXX-X)</label>
              <input name="cuil" type="text" required placeholder="20-12345678-9" onChange={handleChange} className={inputClass} />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">Email</label>
              <input name="email" type="email" required onChange={handleChange} className={inputClass} />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700">Contraseña (min 8 caracteres)</label>
              <input name="password" type="password" required minLength={8} onChange={handleChange} className={inputClass} />
            </div>

            <div className="grid grid-cols-2 gap-4">
                 <div>
                    <label className="block text-sm font-medium text-gray-700">Rol</label>
                    <select name="autoridad" onChange={handleChange} className={inputClass}>
                        <option value={UserRole.ENFERMERO}>Enfermero</option>
                        <option value={UserRole.MEDICO}>Médico</option>
                    </select>
                </div>
                 <div>
                    <label className="block text-sm font-medium text-gray-700">Matrícula</label>
                    <input name="matricula" type="text" required onChange={handleChange} className={inputClass} />
                </div>
            </div>

            <div>
              <button
                type="submit"
                disabled={loading}
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50"
              >
                {loading ? 'Registrando...' : 'Registrar'}
              </button>
            </div>
          </form>

            <div className="mt-4 text-center">
                <Link to="/login" className="text-sm font-medium text-primary-600 hover:text-primary-500">
                    Ya tengo una cuenta
                </Link>
            </div>
        </div>
      </div>
    </div>
  );
};

export default Register;