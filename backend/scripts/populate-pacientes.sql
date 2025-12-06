-- ============================================
-- SCRIPT PARA POBLAR LA BASE DE DATOS CON 100 PACIENTES VÁLIDOS
-- ============================================
-- Este script inserta 100 pacientes con datos realistas
-- Incluye CUILs válidos, nombres, apellidos, emails, domicilios y obras sociales
-- ============================================

-- Función para generar CUILs válidos con dígito verificador correcto
CREATE OR REPLACE FUNCTION generar_cuil_valido(prefijo TEXT, dni TEXT)
RETURNS TEXT AS $$
DECLARE
    base TEXT;
    multiplicadores INTEGER[] := ARRAY[5, 4, 3, 2, 7, 6, 5, 4, 3, 2];
    suma INTEGER := 0;
    resto INTEGER;
    digito_verificador INTEGER;
    i INTEGER;
    digito INTEGER;
BEGIN
    -- Construir la base (prefijo + DNI, 10 dígitos)
    base := prefijo || LPAD(dni, 8, '0');
    
    -- Calcular la suma ponderada
    FOR i IN 1..10 LOOP
        digito := CAST(SUBSTRING(base FROM i FOR 1) AS INTEGER);
        suma := suma + (digito * multiplicadores[i]);
    END LOOP;
    
    -- Calcular el dígito verificador
    resto := suma % 11;
    digito_verificador := 11 - resto;
    
    -- Ajustes especiales
    IF digito_verificador = 11 THEN
        digito_verificador := 0;
    ELSIF digito_verificador = 10 THEN
        digito_verificador := 9;
    END IF;
    
    -- Retornar CUIL en formato XX-XXXXXXXX-X
    RETURN prefijo || '-' || LPAD(dni, 8, '0') || '-' || digito_verificador::TEXT;
END;
$$ LANGUAGE plpgsql;

-- Asegurarse de que las obras sociales existan
INSERT INTO obras_sociales (id, nombre) VALUES (1, 'OSDE') ON CONFLICT (id) DO NOTHING;
INSERT INTO obras_sociales (id, nombre) VALUES (2, 'Swiss Medical') ON CONFLICT (id) DO NOTHING;
INSERT INTO obras_sociales (id, nombre) VALUES (3, 'IOMA') ON CONFLICT (id) DO NOTHING;
INSERT INTO obras_sociales (id, nombre) VALUES (4, 'PAMI') ON CONFLICT (id) DO NOTHING;
INSERT INTO obras_sociales (id, nombre) VALUES (5, 'Particular') ON CONFLICT (id) DO NOTHING;

-- Limpiar pacientes existentes (opcional - comentar si quieres mantener los existentes)
-- DELETE FROM pacientes;

-- Insertar 100 pacientes válidos usando la función generadora de CUILs
-- Usamos ON CONFLICT para evitar errores si el script se ejecuta múltiples veces
INSERT INTO pacientes (cuil, nombre, apellido, email, domicilio_calle, domicilio_numero, domicilio_localidad, obra_social_id, numero_afiliado) VALUES
-- Pacientes 1-20 (Hombres - prefijo 20)
(generar_cuil_valido('20', '12345678'), 'Juan', 'García', 'juan.garcia@email.com', 'Av. Corrientes', 1234, 'Buenos Aires', 1, '12345678'),
(generar_cuil_valido('20', '23456789'), 'Carlos', 'Rodríguez', 'carlos.rodriguez@email.com', 'Av. Santa Fe', 5678, 'Buenos Aires', 2, '23456789'),
(generar_cuil_valido('20', '34567890'), 'Luis', 'Fernández', 'luis.fernandez@email.com', 'Calle Mitre', 901, 'Rosario', 3, '34567890'),
(generar_cuil_valido('20', '45678901'), 'Miguel', 'López', 'miguel.lopez@email.com', 'Av. Libertador', 2500, 'Buenos Aires', 1, '45678901'),
(generar_cuil_valido('20', '56789012'), 'Roberto', 'Martínez', 'roberto.martinez@email.com', 'Av. Córdoba', 789, 'Córdoba', 2, '56789012'),
(generar_cuil_valido('20', '67890123'), 'Jorge', 'González', 'jorge.gonzalez@email.com', 'Calle San Martín', 123, 'Mendoza', 3, '67890123'),
(generar_cuil_valido('20', '78901234'), 'Fernando', 'Pérez', 'fernando.perez@email.com', 'Av. 9 de Julio', 456, 'Buenos Aires', 4, '78901234'),
(generar_cuil_valido('20', '89012345'), 'Diego', 'Sánchez', 'diego.sanchez@email.com', 'Av. Belgrano', 321, 'Tucumán', 1, '89012345'),
(generar_cuil_valido('20', '90123456'), 'Alejandro', 'Ramírez', 'alejandro.ramirez@email.com', 'Calle Rivadavia', 654, 'La Plata', 2, '90123456'),
(generar_cuil_valido('20', '98765432'), 'Pablo', 'Torres', 'pablo.torres@email.com', 'Av. San Martín', 987, 'Mar del Plata', 3, '98765432'),
(generar_cuil_valido('20', '11223344'), 'Ricardo', 'Flores', 'ricardo.flores@email.com', 'Calle Alvear', 147, 'Salta', 1, '11223344'),
(generar_cuil_valido('20', '22334455'), 'Mario', 'Díaz', 'mario.diaz@email.com', 'Av. Colón', 258, 'Córdoba', 2, '22334455'),
(generar_cuil_valido('20', '33445566'), 'Héctor', 'Morales', 'hector.morales@email.com', 'Calle Urquiza', 369, 'Rosario', 3, '33445566'),
(generar_cuil_valido('20', '44556677'), 'Sergio', 'Ortiz', 'sergio.ortiz@email.com', 'Av. Alem', 741, 'Buenos Aires', 4, '44556677'),
(generar_cuil_valido('20', '55667788'), 'Daniel', 'Vargas', 'daniel.vargas@email.com', 'Calle Moreno', 852, 'La Plata', 1, '55667788'),
(generar_cuil_valido('20', '66778899'), 'Andrés', 'Castro', 'andres.castro@email.com', 'Av. Independencia', 963, 'Mendoza', 2, '66778899'),
(generar_cuil_valido('20', '77889900'), 'Gustavo', 'Ramos', 'gustavo.ramos@email.com', 'Calle España', 159, 'Tucumán', 3, '77889900'),
(generar_cuil_valido('20', '88990011'), 'Eduardo', 'Jiménez', 'eduardo.jimenez@email.com', 'Av. España', 357, 'Salta', 1, '88990011'),
(generar_cuil_valido('20', '99001122'), 'Raúl', 'Herrera', 'raul.herrera@email.com', 'Calle Italia', 468, 'Córdoba', 2, '99001122'),
(generar_cuil_valido('20', '10111213'), 'Víctor', 'Mendoza', 'victor.mendoza@email.com', 'Av. Francia', 579, 'Rosario', 3, '10111213'),

-- Pacientes 21-40 (Mujeres - prefijo 27)
(generar_cuil_valido('27', '12345678'), 'María', 'García', 'maria.garcia@email.com', 'Av. Corrientes', 1235, 'Buenos Aires', 1, '87654321'),
(generar_cuil_valido('27', '23456789'), 'Ana', 'Rodríguez', 'ana.rodriguez@email.com', 'Av. Santa Fe', 5679, 'Buenos Aires', 2, '76543210'),
(generar_cuil_valido('27', '34567890'), 'Laura', 'Fernández', 'laura.fernandez@email.com', 'Calle Mitre', 902, 'Rosario', 3, '65432109'),
(generar_cuil_valido('27', '45678901'), 'Carmen', 'López', 'carmen.lopez@email.com', 'Av. Libertador', 2501, 'Buenos Aires', 1, '54321098'),
(generar_cuil_valido('27', '56789012'), 'Patricia', 'Martínez', 'patricia.martinez@email.com', 'Av. Córdoba', 790, 'Córdoba', 2, '43210987'),
(generar_cuil_valido('27', '67890123'), 'Sandra', 'González', 'sandra.gonzalez@email.com', 'Calle San Martín', 124, 'Mendoza', 3, '32109876'),
(generar_cuil_valido('27', '78901234'), 'Claudia', 'Pérez', 'claudia.perez@email.com', 'Av. 9 de Julio', 457, 'Buenos Aires', 4, '21098765'),
(generar_cuil_valido('27', '89012345'), 'Silvia', 'Sánchez', 'silvia.sanchez@email.com', 'Av. Belgrano', 322, 'Tucumán', 1, '10987654'),
(generar_cuil_valido('27', '90123456'), 'Monica', 'Ramírez', 'monica.ramirez@email.com', 'Calle Rivadavia', 655, 'La Plata', 2, '09876543'),
(generar_cuil_valido('27', '98765432'), 'Verónica', 'Torres', 'veronica.torres@email.com', 'Av. San Martín', 988, 'Mar del Plata', 3, '88765432'),
(generar_cuil_valido('27', '11223344'), 'Natalia', 'Flores', 'natalia.flores@email.com', 'Calle Alvear', 148, 'Salta', 1, '87654321'),
(generar_cuil_valido('27', '22334455'), 'Gabriela', 'Díaz', 'gabriela.diaz@email.com', 'Av. Colón', 259, 'Córdoba', 2, '76543210'),
(generar_cuil_valido('27', '33445566'), 'Andrea', 'Morales', 'andrea.morales@email.com', 'Calle Urquiza', 370, 'Rosario', 3, '65432109'),
(generar_cuil_valido('27', '44556677'), 'Valeria', 'Ortiz', 'valeria.ortiz@email.com', 'Av. Alem', 742, 'Buenos Aires', 4, '54321098'),
(generar_cuil_valido('27', '55667788'), 'Florencia', 'Vargas', 'florencia.vargas@email.com', 'Calle Moreno', 853, 'La Plata', 1, '43210987'),
(generar_cuil_valido('27', '66778899'), 'Lucía', 'Castro', 'lucia.castro@email.com', 'Av. Independencia', 964, 'Mendoza', 2, '32109876'),
(generar_cuil_valido('27', '77889900'), 'Paula', 'Ramos', 'paula.ramos@email.com', 'Calle España', 160, 'Tucumán', 3, '21098765'),
(generar_cuil_valido('27', '88990011'), 'Carolina', 'Jiménez', 'carolina.jimenez@email.com', 'Av. España', 358, 'Salta', 1, '10987654'),
(generar_cuil_valido('27', '99001122'), 'Daniela', 'Herrera', 'daniela.herrera@email.com', 'Calle Italia', 469, 'Córdoba', 2, '09876543'),
(generar_cuil_valido('27', '10111213'), 'Romina', 'Mendoza', 'romina.mendoza@email.com', 'Av. Francia', 580, 'Rosario', 3, '98765432'),

-- Pacientes 41-60 (Hombres - prefijo 20)
(generar_cuil_valido('20', '20212223'), 'Martín', 'Suárez', 'martin.suarez@email.com', 'Av. Cabildo', 1236, 'Buenos Aires', 1, '20212223'),
(generar_cuil_valido('20', '31323334'), 'Nicolás', 'Gutiérrez', 'nicolas.gutierrez@email.com', 'Calle Lavalle', 5680, 'Buenos Aires', 2, '31323334'),
(generar_cuil_valido('20', '41424344'), 'Facundo', 'Navarro', 'facundo.navarro@email.com', 'Av. Sarmiento', 903, 'Rosario', 3, '41424344'),
(generar_cuil_valido('20', '53545556'), 'Tomás', 'Ruiz', 'tomas.ruiz@email.com', 'Calle Pellegrini', 2502, 'Buenos Aires', 1, '53545556'),
(generar_cuil_valido('20', '64656667'), 'Sebastián', 'Delgado', 'sebastian.delgado@email.com', 'Av. Vélez Sarsfield', 791, 'Córdoba', 2, '64656667'),
(generar_cuil_valido('20', '75767778'), 'Matías', 'Molina', 'matias.molina@email.com', 'Calle Güemes', 125, 'Mendoza', 3, '75767778'),
(generar_cuil_valido('20', '86878889'), 'Agustín', 'Herrera', 'agustin.herrera@email.com', 'Av. Callao', 458, 'Buenos Aires', 4, '86878889'),
(generar_cuil_valido('20', '97989900'), 'Franco', 'Cruz', 'franco.cruz@email.com', 'Calle Reconquista', 323, 'Tucumán', 1, '97989900'),
(generar_cuil_valido('20', '11121314'), 'Lucas', 'Ortega', 'lucas.ortega@email.com', 'Av. Pueyrredón', 656, 'La Plata', 2, '11121314'),
(generar_cuil_valido('20', '12131415'), 'Bruno', 'Medina', 'bruno.medina@email.com', 'Calle Alsina', 989, 'Mar del Plata', 3, '12131415'),
(generar_cuil_valido('20', '13141516'), 'Emiliano', 'Guerrero', 'emiliano.guerrero@email.com', 'Av. Jujuy', 149, 'Salta', 1, '13141516'),
(generar_cuil_valido('20', '14151617'), 'Joaquín', 'Rojas', 'joaquin.rojas@email.com', 'Calle Entre Ríos', 260, 'Córdoba', 2, '14151617'),
(generar_cuil_valido('20', '15161718'), 'Bautista', 'Campos', 'bautista.campos@email.com', 'Av. Chacabuco', 371, 'Rosario', 3, '15161718'),
(generar_cuil_valido('20', '16171819'), 'Thiago', 'Vega', 'thiago.vega@email.com', 'Calle Maipú', 743, 'Buenos Aires', 4, '16171819'),
(generar_cuil_valido('20', '17181920'), 'Benjamín', 'Fuentes', 'benjamin.fuentes@email.com', 'Av. Rawson', 854, 'La Plata', 1, '17181920'),
(generar_cuil_valido('20', '18192021'), 'Dante', 'Peña', 'dante.pena@email.com', 'Calle Godoy Cruz', 965, 'Mendoza', 2, '18192021'),
(generar_cuil_valido('20', '19202122'), 'Santino', 'León', 'santino.leon@email.com', 'Av. Yrigoyen', 161, 'Tucumán', 3, '19202122'),
(generar_cuil_valido('20', '21222324'), 'Valentino', 'Contreras', 'valentino.contreras@email.com', 'Calle Güemes', 359, 'Salta', 1, '21222324'),
(generar_cuil_valido('20', '22232425'), 'Dylan', 'Parra', 'dylan.parra@email.com', 'Av. San Juan', 470, 'Córdoba', 2, '22232425'),
(generar_cuil_valido('20', '23242526'), 'Ian', 'Soto', 'ian.soto@email.com', 'Calle Tucumán', 581, 'Rosario', 3, '23242526'),

-- Pacientes 61-80 (Mujeres - prefijo 27)
(generar_cuil_valido('27', '20212223'), 'Agustina', 'Suárez', 'agustina.suarez@email.com', 'Av. Cabildo', 1237, 'Buenos Aires', 1, '30212223'),
(generar_cuil_valido('27', '31323334'), 'Sofía', 'Gutiérrez', 'sofia.gutierrez@email.com', 'Calle Lavalle', 5681, 'Buenos Aires', 2, '41323334'),
(generar_cuil_valido('27', '41424344'), 'Isabella', 'Navarro', 'isabella.navarro@email.com', 'Av. Sarmiento', 904, 'Rosario', 3, '51424344'),
(generar_cuil_valido('27', '53545556'), 'Emma', 'Ruiz', 'emma.ruiz@email.com', 'Calle Pellegrini', 2503, 'Buenos Aires', 1, '63545556'),
(generar_cuil_valido('27', '64656667'), 'Olivia', 'Delgado', 'olivia.delgado@email.com', 'Av. Vélez Sarsfield', 792, 'Córdoba', 2, '74656667'),
(generar_cuil_valido('27', '75767778'), 'Martina', 'Molina', 'martina.molina@email.com', 'Calle Güemes', 126, 'Mendoza', 3, '85767778'),
(generar_cuil_valido('27', '86878889'), 'Catalina', 'Herrera', 'catalina.herrera@email.com', 'Av. Callao', 459, 'Buenos Aires', 4, '96878889'),
(generar_cuil_valido('27', '97989900'), 'Victoria', 'Cruz', 'victoria.cruz@email.com', 'Calle Reconquista', 324, 'Tucumán', 1, '17989900'),
(generar_cuil_valido('27', '11121314'), 'Antonella', 'Ortega', 'antonella.ortega@email.com', 'Av. Pueyrredón', 657, 'La Plata', 2, '21121314'),
(generar_cuil_valido('27', '12131415'), 'Francesca', 'Medina', 'francesca.medina@email.com', 'Calle Alsina', 990, 'Mar del Plata', 3, '32131415'),
(generar_cuil_valido('27', '13141516'), 'Jazmín', 'Guerrero', 'jazmin.guerrero@email.com', 'Av. Jujuy', 150, 'Salta', 1, '43141516'),
(generar_cuil_valido('27', '14151617'), 'Milagros', 'Rojas', 'milagros.rojas@email.com', 'Calle Entre Ríos', 261, 'Córdoba', 2, '54151617'),
(generar_cuil_valido('27', '15161718'), 'Bianca', 'Campos', 'bianca.campos@email.com', 'Av. Chacabuco', 372, 'Rosario', 3, '65161718'),
(generar_cuil_valido('27', '16171819'), 'Ambar', 'Vega', 'ambar.vega@email.com', 'Calle Maipú', 744, 'Buenos Aires', 4, '76171819'),
(generar_cuil_valido('27', '17181920'), 'Delfina', 'Fuentes', 'delfina.fuentes@email.com', 'Av. Rawson', 855, 'La Plata', 1, '87181920'),
(generar_cuil_valido('27', '18192021'), 'Jazmín', 'Peña', 'jazmin.pena@email.com', 'Calle Godoy Cruz', 966, 'Mendoza', 2, '98192021'),
(generar_cuil_valido('27', '19202122'), 'Alma', 'León', 'alma.leon@email.com', 'Av. Yrigoyen', 162, 'Tucumán', 3, '09202122'),
(generar_cuil_valido('27', '21222324'), 'Helena', 'Contreras', 'helena.contreras@email.com', 'Calle Güemes', 360, 'Salta', 1, '11222324'),
(generar_cuil_valido('27', '22232425'), 'Lola', 'Parra', 'lola.parra@email.com', 'Av. San Juan', 471, 'Córdoba', 2, '22232425'),
(generar_cuil_valido('27', '23242526'), 'Mía', 'Soto', 'mia.soto@email.com', 'Calle Tucumán', 582, 'Rosario', 3, '33242526'),

-- Pacientes 81-100 (Mezcla)
(generar_cuil_valido('20', '24252627'), 'Ignacio', 'Aguilar', 'ignacio.aguilar@email.com', 'Av. Rivadavia', 1238, 'Buenos Aires', 1, '24252627'),
(generar_cuil_valido('27', '25262728'), 'Renata', 'Aguilar', 'renata.aguilar@email.com', 'Av. Rivadavia', 1239, 'Buenos Aires', 2, '35262728'),
(generar_cuil_valido('20', '26272829'), 'Maximiliano', 'Blanco', 'maximiliano.blanco@email.com', 'Calle Viamonte', 5682, 'Buenos Aires', 3, '26272829'),
(generar_cuil_valido('27', '27282930'), 'Olivia', 'Blanco', 'olivia.blanco2@email.com', 'Calle Viamonte', 5683, 'Buenos Aires', 4, '37282930'),
(generar_cuil_valido('20', '28293031'), 'Rodrigo', 'Cárdenas', 'rodrigo.cardenas@email.com', 'Av. Las Heras', 905, 'Rosario', 1, '28293031'),
(generar_cuil_valido('27', '29303132'), 'Camila', 'Cárdenas', 'camila.cardenas@email.com', 'Av. Las Heras', 906, 'Rosario', 2, '39303132'),
(generar_cuil_valido('20', '30313233'), 'Federico', 'Espinoza', 'federico.espinoza@email.com', 'Calle Junín', 2504, 'Buenos Aires', 3, '30313233'),
(generar_cuil_valido('27', '31323334'), 'Luna', 'Espinoza', 'luna.espinoza@email.com', 'Calle Junín', 2505, 'Buenos Aires', 4, '41323334'),
(generar_cuil_valido('20', '32333435'), 'Gonzalo', 'Figueroa', 'gonzalo.figueroa@email.com', 'Av. Dorrego', 793, 'Córdoba', 1, '32333435'),
(generar_cuil_valido('27', '33343536'), 'Zoe', 'Figueroa', 'zoe.figueroa@email.com', 'Av. Dorrego', 794, 'Córdoba', 2, '43343536'),
(generar_cuil_valido('20', '34353637'), 'Leandro', 'Gallardo', 'leandro.gallardo@email.com', 'Calle Laprida', 127, 'Mendoza', 3, '34353637'),
(generar_cuil_valido('27', '35363738'), 'Abril', 'Gallardo', 'abril.gallardo@email.com', 'Calle Laprida', 128, 'Mendoza', 4, '45363738'),
(generar_cuil_valido('20', '36373839'), 'Ramiro', 'Ibarra', 'ramiro.ibarra@email.com', 'Av. Scalabrini Ortiz', 460, 'Buenos Aires', 1, '36373839'),
(generar_cuil_valido('27', '37383940'), 'Maia', 'Ibarra', 'maia.ibarra@email.com', 'Av. Scalabrini Ortiz', 461, 'Buenos Aires', 2, '47383940'),
(generar_cuil_valido('20', '38394041'), 'Axel', 'Juárez', 'axel.juarez@email.com', 'Calle Montevideo', 325, 'Tucumán', 3, '38394041'),
(generar_cuil_valido('27', '39404142'), 'Emma', 'Juárez', 'emma.juarez@email.com', 'Calle Montevideo', 326, 'Tucumán', 4, '49404142'),
(generar_cuil_valido('20', '40414243'), 'Kevin', 'Lara', 'kevin.lara@email.com', 'Av. Monroe', 658, 'La Plata', 1, '40414243'),
(generar_cuil_valido('27', '41424344'), 'Alma', 'Lara', 'alma.lara@email.com', 'Av. Monroe', 659, 'La Plata', 2, '51424344'),
(generar_cuil_valido('20', '42434445'), 'Brian', 'Miranda', 'brian.miranda@email.com', 'Calle Paraguay', 991, 'Mar del Plata', 3, '42434445'),
(generar_cuil_valido('27', '43444546'), 'Noa', 'Miranda', 'noa.miranda@email.com', 'Calle Paraguay', 992, 'Mar del Plata', 4, '53444546')
ON CONFLICT (cuil) DO NOTHING;

-- Verificar la inserción
SELECT COUNT(*) as total_pacientes FROM pacientes;
