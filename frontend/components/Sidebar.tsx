import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { UserRole } from '../types';
import { 
  LayoutDashboard, 
  Stethoscope, 
  ClipboardList, 
  LogOut, 
  UserPlus,
  Activity,
  FileText,
  Users
} from 'lucide-react';

const Sidebar: React.FC = () => {
  const { user, logout } = useAuth();
  const location = useLocation();

  const isActive = (path: string) => location.pathname === path;

  const linkClass = (path: string) => `
    flex items-center gap-3 px-4 py-3 text-sm font-medium rounded-lg transition-colors
    ${isActive(path) 
      ? 'bg-primary-50 text-primary-700' 
      : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'}
  `;

  return (
    <div className="w-64 bg-white h-screen border-r border-gray-200 flex flex-col fixed left-0 top-0">
      <div className="p-6 border-b border-gray-100 flex items-center gap-2">
        <div className="bg-primary-600 p-1.5 rounded-lg flex-shrink-0">
          <Activity className="w-6 h-6 text-white" />
        </div>
        <span className="text-lg font-bold text-gray-900 tracking-tight leading-tight">Servicio de Urgencias</span>
      </div>

      <div className="p-4 flex-1 overflow-y-auto">
        <div className="mb-6">
            <p className="px-4 text-xs font-semibold text-gray-400 uppercase tracking-wider mb-2">
                Menu
            </p>
            <nav className="space-y-1">
            <Link to="/" className={linkClass('/')}>
                <LayoutDashboard className="w-5 h-5" />
                Inicio
            </Link>

            {user?.autoridad === UserRole.ENFERMERO && (
                <Link to="/admission" className={linkClass('/admission')}>
                <UserPlus className="w-5 h-5" />
                Nuevo Ingreso
                </Link>
            )}

            <Link to="/queue" className={linkClass('/queue')}>
                <ClipboardList className="w-5 h-5" />
                Sala de Espera
            </Link>

            <Link to="/admissions" className={linkClass('/admissions')}>
                <FileText className="w-5 h-5" />
                Ingresos
            </Link>
            
            <Link to="/patients" className={linkClass('/patients')}>
                <Users className="w-5 h-5" />
                Pacientes
            </Link>

            {user?.autoridad === UserRole.MEDICO && (
                <Link to="/attend" className={linkClass('/attend')}>
                <Stethoscope className="w-5 h-5" />
                Atender Paciente
                </Link>
            )}
            </nav>
        </div>
      </div>

      <div className="p-4 border-t border-gray-100">
        <div className="flex items-center gap-3 px-4 py-3 mb-2">
          <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-bold text-xs">
            {user?.nombre ? user.nombre.substring(0, 1).toUpperCase() : user?.email.substring(0, 2).toUpperCase()}
          </div>
          <div className="flex-1 min-w-0">
            {user?.nombre ? (
              <>
                <p className="text-sm font-medium text-gray-900 truncate">
                  {user.nombre} {user.apellido}
                </p>
                <p className="text-xs text-gray-500 truncate">{user.email}</p>
              </>
            ) : (
              <p className="text-sm font-medium text-gray-900 truncate">{user?.email}</p>
            )}
            <p className="text-xs text-primary-600 font-medium truncate capitalize mt-0.5">
              {user?.autoridad.toLowerCase()}
            </p>
          </div>
        </div>
        <button 
          onClick={logout}
          className="w-full flex items-center gap-3 px-4 py-2 text-sm font-medium text-red-600 rounded-lg hover:bg-red-50 transition-colors"
        >
          <LogOut className="w-5 h-5" />
          Cerrar Sesión
        </button>
      </div>
    </div>
  );
};

export default Sidebar;