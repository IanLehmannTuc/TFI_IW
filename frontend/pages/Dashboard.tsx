import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiRequest } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { UserRole, Admission, TriageLevel } from '../types';
import { 
  Users, 
  AlertTriangle, 
  Activity, 
  UserPlus, 
  Stethoscope, 
  ClipboardList, 
  FileText,
  ChevronRight,
  TrendingUp
} from 'lucide-react';

const Dashboard: React.FC = () => {
  const { user } = useAuth();
  const [patientCount, setPatientCount] = useState(0);
  const [criticalCount, setCriticalCount] = useState(0);
  const [admissionsToday, setAdmissionsToday] = useState(0);
  
  useEffect(() => {
    const fetchData = async () => {
      try {
        // 4.1 Ver Cola de Atención Completa
        const queue = await apiRequest<Admission[]>('/cola-atencion');
        setPatientCount(queue.length);
        
        // Count both CRITICA and EMERGENCIA as high priority
        const crit = queue.filter(p => p.nivelEmergencia === TriageLevel.CRITICA || p.nivelEmergencia === TriageLevel.EMERGENCIA).length;
        setCriticalCount(crit);

        // 6.1 Listar Todos los Ingresos
        const history = await apiRequest<Admission[]>('/ingresos');
        
        const now = new Date();
        const todayCount = history.filter(p => {
            const admissionDate = new Date(p.fechaHoraIngreso);
            return admissionDate.getDate() === now.getDate() &&
                   admissionDate.getMonth() === now.getMonth() &&
                   admissionDate.getFullYear() === now.getFullYear();
        }).length;
        
        setAdmissionsToday(todayCount);

      } catch (e) {
        console.error("Error fetching stats", e);
      }
    };
    fetchData();
  }, []);

  const stats = [
    { 
      name: 'Pacientes en Espera', 
      stat: patientCount.toString(), 
      icon: Users, 
      color: 'bg-blue-500', 
      desc: 'En cola de atención' 
    },
    { 
      name: 'Alta Prioridad', 
      stat: criticalCount.toString(), 
      icon: AlertTriangle, 
      color: 'bg-red-500', 
      desc: 'Crítica y Emergencia' 
    },
    { 
      name: 'Ingresos Hoy', 
      stat: admissionsToday.toString(), 
      icon: Activity, 
      color: 'bg-green-500', 
      desc: 'Total registrados' 
    },
  ];

  const ActionCard = ({ 
    to, 
    title, 
    desc, 
    icon: Icon, 
    colorClass, 
    role 
  }: { 
    to: string; 
    title: string; 
    desc: string; 
    icon: any; 
    colorClass: string;
    role?: UserRole;
  }) => {
    if (role && user?.autoridad !== role) return null;

    return (
      <Link 
        to={to} 
        className="group relative bg-white p-6 rounded-2xl shadow-sm border border-gray-200 hover:shadow-md transition-all duration-200 flex flex-col justify-between h-full hover:border-primary-200"
      >
        <div>
          <div className={`w-12 h-12 rounded-xl ${colorClass} bg-opacity-10 flex items-center justify-center mb-4 group-hover:scale-110 transition-transform duration-200`}>
            <Icon className={`w-6 h-6 ${colorClass.replace('bg-', 'text-')}`} />
          </div>
          <h3 className="text-lg font-bold text-gray-900 mb-2">{title}</h3>
          <p className="text-sm text-gray-500">{desc}</p>
        </div>
        <div className="mt-4 flex items-center text-sm font-medium text-primary-600 opacity-0 group-hover:opacity-100 transition-opacity">
          Acceder <ChevronRight className="w-4 h-4 ml-1" />
        </div>
      </Link>
    );
  };

  return (
    <div className="space-y-8 animate-fade-in">
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Inicio</h1>
        <p className="mt-2 text-gray-600">
          Hola, <span className="font-semibold text-primary-700">{user?.nombre}</span>. Aquí tienes un resumen del estado actual de urgencias.
        </p>
      </div>
      
      {/* Stats Row */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3">
        {stats.map((item) => (
          <div key={item.name} className="relative bg-white pt-5 px-4 pb-6 shadow-sm rounded-2xl border border-gray-100 overflow-hidden">
            <dt>
              <div className={`absolute rounded-xl p-3 ${item.color}`}>
                <item.icon className="h-6 w-6 text-white" aria-hidden="true" />
              </div>
              <p className="ml-16 text-sm font-medium text-gray-500 truncate">{item.name}</p>
            </dt>
            <dd className="ml-16 flex items-baseline pb-1 sm:pb-2">
              <p className="text-2xl font-bold text-gray-900">{item.stat}</p>
              <p className="ml-2 flex items-baseline text-xs text-gray-400">
                <span className="sr-only">Detalle</span>
                {item.desc}
              </p>
            </dd>
          </div>
        ))}
      </div>

      {/* Quick Actions Grid */}
      <div>
        <h2 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2">
          <TrendingUp className="w-5 h-5 text-gray-400" />
          Accesos Rápidos
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          
          <ActionCard 
            to="/admission"
            title="Registrar Ingreso"
            desc="Crear nuevo paciente, tomar signos vitales y asignar triage."
            icon={UserPlus}
            colorClass="bg-blue-600"
            role={UserRole.ENFERMERO}
          />

          <ActionCard 
            to="/attend"
            title="Atender Paciente"
            desc="Llamar al siguiente paciente en la cola según prioridad."
            icon={Stethoscope}
            colorClass="bg-emerald-600"
            role={UserRole.MEDICO}
          />

          <ActionCard 
            to="/queue"
            title="Sala de Espera"
            desc="Visualizar la cola de pacientes actual y sus prioridades."
            icon={ClipboardList}
            colorClass="bg-indigo-600"
          />

          <ActionCard 
            to="/admissions"
            title="Historial de Ingresos"
            desc="Consultar el registro histórico de todas las atenciones."
            icon={FileText}
            colorClass="bg-slate-600"
          />

        </div>
      </div>
    </div>
  );
};

export default Dashboard;