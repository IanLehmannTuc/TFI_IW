#language: en

Feature: Modulo de Urgencias
  Como enfermera
  Quiero poder registrar las admisiones de los pacientes a urgencias
  Para determinar que pacientes tienen mayor prioridad de atención

  Background:
    Given Que el siguiente enfermero esté registrado:
    |CUIL         |Nombre       |Apellido |Email            |Matricula  |
    |20-27272727-9|Mario        |Sarmiento|mario@gmail.com  |123456     |

  Scenario: Ingreso exitoso de paciente existente
    Given Que estan cargados los siguientes pacientes en el sistema:
    |CUIL         |Apellido |Nombre |Obra Social  |
    |20-20304050-5|Lehmann  |Ian    |Sancor Salud |

    When Ingresa a urgencias el siguiente paciente:
    |CUIL         |Informe|Nivel de Emergencia|Temperatura|Frecuencia Cardiaca|Frecuencia Respiratoria|Tension Arterial|
    |20-20304050-5|Dengue |Emergencia         |39         |70                 |15                     |120/80          |

    Then El ingreso se registra correctamente y el paciente entra en la cola de atención
    And La cola de atención se encuentra en el siguiente orden
    |20-20304050-5|

  Scenario: Ingreso de paciente nuevo (no existe en sistema)
    Given Que no existe un paciente registrado con CUIL "23-23344556-8"

    When Ingresa a urgencias el siguiente paciente:
    |CUIL         |Nombre   |Apellido |Informe|Nivel de Emergencia|Temperatura|Frecuencia Cardiaca|Frecuencia Respiratoria|Tension Arterial|
    |23-23344556-8|Juan     |Perez    |Covid  |Emergencia         |38.5       |75                 |18                     |110/65          |

    Then Se crea el paciente con CUIL "23-23344556-8"
    And El ingreso se registra correctamente y el paciente entra en la cola de atención
    And La cola de atención se encuentra en el siguiente orden
    |23-23344556-8|

  Scenario: Priorización por nivel de emergencia (paciente A con mayor prioridad)
    Given Que estan cargados los siguientes pacientes en el sistema:
    |CUIL         |Apellido |Nombre |Obra Social  |
    |20-20304050-5|Lehmann  |Ian    |Sancor Salud |
    |27-14141414-9|Garcia   |Ana    |OSDE         |

    And Hay un paciente con CUIL:"27-14141414-9", Nombre:"Ana", Apellido:"Garcia" en la cola de atención con nivel de emergencia "Urgencia"

    When Ingresa a urgencias el siguiente paciente:
    |CUIL         |Informe|Nivel de Emergencia|Temperatura|Frecuencia Cardiaca|Frecuencia Respiratoria|Tension Arterial|
    |20-20304050-5|Dengue |Emergencia         |39         |70                 |15                     |120/80          |
    
    Then La cola de atención se encuentra en el siguiente orden
    |20-20304050-5| 
    |27-14141414-9| 

  Scenario: Priorización por nivel de emergencia (paciente A con menor prioridad)
    Given Que estan cargados los siguientes pacientes en el sistema:
    |CUIL         |Apellido |Nombre |Obra Social  |
    |20-20304050-5|Lehmann  |Ian    |Sancor Salud |
    |27-14141414-9|Garcia   |Ana    |OSDE         |

    And Hay un paciente con CUIL:"27-14141414-9", Nombre:"Ana", Apellido:"Garcia" en la cola de atención con nivel de emergencia "Emergencia"

    When Ingresa a urgencias el siguiente paciente:
    |CUIL         |Informe|Nivel de Emergencia|Temperatura|Frecuencia Cardiaca|Frecuencia Respiratoria|Tension Arterial|
    |20-20304050-5|Gripe  |Urgencia           |38         |75                 |18                     |110/70          |

    Then La cola de atención se encuentra en el siguiente orden
    |27-14141414-9|
    |20-20304050-5|

  Scenario: Priorización por orden de llegada (mismo nivel de emergencia)
    Given Que estan cargados los siguientes pacientes en el sistema:
    |CUIL         |Apellido |Nombre |Obra Social  |
    |20-20304050-5|Lehmann  |Ian    |Sancor Salud |
    |27-14141414-9|Garcia   |Ana    |OSDE         |

    And Hay un paciente con CUIL:"27-14141414-9", Nombre:"Ana", Apellido:"Garcia" en la cola de atención con nivel de emergencia "Emergencia"

    When Ingresa a urgencias el siguiente paciente:
    |CUIL         |Informe|Nivel de Emergencia|Temperatura|Frecuencia Cardiaca|Frecuencia Respiratoria|Tension Arterial|
    |20-20304050-5|Fiebre |Emergencia         |39.5       |72                 |16                     |125/85          |

    Then La cola de atención se encuentra en el siguiente orden
    |27-14141414-9|
    |20-20304050-5|

  Scenario: Error por datos obligatorios faltantes
    Given Que estan cargados los siguientes pacientes en el sistema:
    |CUIL         |Apellido |Nombre |Obra Social  |
    |20-20304050-5|Lehmann  |Ian    |Sancor Salud |

    When Ingresa a urgencias el siguiente paciente omitiendo datos obligatorios:
    |CUIL         |Informe|Nivel de Emergencia|Temperatura|Frecuencia Cardiaca|Frecuencia Respiratoria|Tension Arterial|
    |20-20304050-5|       |Emergencia         |39         |70                 |15                     |120/80          |

    Then Se emite un mensaje de error indicando que el informe es obligatorio

#  Scenario Outline: Error por datos obligatorios faltantes
#     Given Que estan cargados los siguientes pacientes en el sistema:
#       |CUIL         |Apellido |Nombre |Obra Social  |
#       |20-20304050-5|Lehmann  |Ian    |Sancor Salud |

#     When Ingresa a urgencias el siguiente paciente omitiendo datos obligatorios:
#       |CUIL         |Informe     |Nivel de Emergencia     |Temperatura     |Frecuencia Cardiaca     |Frecuencia Respiratoria     |Tension Arterial  |
#       |20-20304050-5|<informe>   |<nivelEmergencia>       |<temperatura>   |<frecuenciaCardiaca>    |<frecuenciaRespiratoria>    |<tensionArterial> |

#     Then Se emite un mensaje de error indicando que <campoFaltante> faltante es obligatorio

#     Examples:
#       | informe | nivelEmergencia | temperatura | frecuenciaCardiaca | frecuenciaRespiratoria | tensionArterial | campoFaltante                |
#       |         | Emergencia      | 39         | 70                 | 15                     | 120/80          | informe                      |
#       | Dengue  |                 | 39         | 70                 | 15                     | 120/80          | nivel de emergencia          |
#       | Dengue  | Emergencia      |            | 70                 | 15                     | 120/80          | temperatura                  |
#       | Dengue  | Emergencia      | 39         |                    | 15                     | 120/80          | frecuencia cardiaca          |
#       | Dengue  | Emergencia      | 39         | 70                 |                        | 120/80          | frecuencia respiratoria      |
#       | Dengue  | Emergencia      | 39         | 70                 | 15                     |                 | tensión arterial             |

  Scenario: Error por frecuencia cardíaca negativa
    Given Que estan cargados los siguientes pacientes en el sistema:
    |CUIL         |Apellido |Nombre |Obra Social  |
    |20-20304050-5|Lehmann  |Ian    |Sancor Salud |

    When Ingresa a urgencias el siguiente paciente:
    |CUIL         |Informe|Nivel de Emergencia|Temperatura|Frecuencia Cardiaca|Frecuencia Respiratoria|Tension Arterial|
    |20-20304050-5|Dengue |Emergencia         |39         |-70                |15                     |120/80          |

    Then Se emite un mensaje de error indicando que la frecuencia cardíaca no puede ser negativa

  Scenario: Error por frecuencia respiratoria negativa
    Given Que estan cargados los siguientes pacientes en el sistema:
    |CUIL         |Apellido |Nombre |Obra Social  |
    |20-20304050-5|Lehmann  |Ian    |Sancor Salud |

    When Ingresa a urgencias el siguiente paciente:
    |CUIL         |Informe|Nivel de Emergencia|Temperatura|Frecuencia Cardiaca|Frecuencia Respiratoria|Tension Arterial|
    |20-20304050-5|Dengue |Emergencia         |39         |70                 |-15                    |120/80          |

    Then Se emite un mensaje de error indicando que la frecuencia respiratoria no puede ser negativa

  Scenario: Error por tensión arterial sistólica negativa
    Given Que estan cargados los siguientes pacientes en el sistema:
    |CUIL         |Apellido |Nombre |Obra Social  |
    |20-20304050-5|Lehmann  |Ian    |Sancor Salud |

    When Ingresa a urgencias el siguiente paciente:
    |CUIL         |Informe|Nivel de Emergencia|Temperatura|Frecuencia Cardiaca|Frecuencia Respiratoria|Tension Arterial|
    |20-20304050-5|Dengue |Emergencia         |39         |70                 |15                     |-120/80         |

    Then Se emite un mensaje de error indicando que la tensión arterial sistólica no puede ser negativa

  Scenario: Error por tensión arterial diastólica negativa
    Given Que estan cargados los siguientes pacientes en el sistema:
    |CUIL         |Apellido |Nombre |Obra Social  |
    |20-20304050-5|Lehmann  |Ian    |Sancor Salud |

    When Ingresa a urgencias el siguiente paciente:
    |CUIL         |Informe|Nivel de Emergencia|Temperatura|Frecuencia Cardiaca|Frecuencia Respiratoria|Tension Arterial|
    |20-20304050-5|Dengue |Emergencia         |39         |70                 |15                     |120/-80         |

    Then Se emite un mensaje de error indicando que la tensión arterial diastólica no puede ser negativa