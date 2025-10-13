#language: en

Feature: Modulo de Urgencias
  Esta feature esta relacionada al registro de ingresos de pacientes en la sala de urgencias
  respetando su nivel de prioridad y el horario.

  Background:
    Given Que la siguiente enfermera esté registrada:
    |Nombre       |Apellido |
    |Maria Celeste|Sarmiento|

    Scenario: Infreso del primer paciente a la lista de espera de urgencias
      Given Dado que estan cargados los siguientes pacientes en el sistema:
      |CUIT         |Apellido |Nombre |Obra Social  |
      |20-40274295-0|Lehmann  |Ian    |Sancor Salud |

      When Ingresa a urgencias el siguiente paciente:
      |CUIT         |Informe|Nivel de Emergencia|Temperatura|Frecuencia Cardiaca|Frecuencia Respiratoria|Tension Arterial|
      |20-40274295-0|Dengue |Emergencia         |39         |70                 |15                     |120/80          |

      Then La lista de espera se encuentra en el siguiente orden
      |20-40274295-0|