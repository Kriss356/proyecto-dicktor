-- =====================================================================
--  Base de datos del Sistema de Gestión de Hotel (proyecto_hotel)
--  Para XAMPP / phpMyAdmin (MySQL o MariaDB)
--
--  Cómo usarlo:
--    1. Abre XAMPP y enciende Apache y MySQL.
--    2. Entra a http://localhost/phpmyadmin
--    3. Pestaña "Importar" -> selecciona este archivo -> "Importar".
--  El script crea la base "hotel_db" desde cero (si ya existía la borra).
--
--  Usuario de prueba para entrar al sistema:
--    usuario: admin      contraseña: admin123
-- =====================================================================

DROP DATABASE IF EXISTS hotel_db;
CREATE DATABASE hotel_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE hotel_db;

-- ---------------------------------------------------------------------
-- Administradores (pantalla administrador.fxml)
-- ---------------------------------------------------------------------
CREATE TABLE administradores (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    telefono      VARCHAR(15),
    email         VARCHAR(100) NOT NULL,
    direccion     VARCHAR(200),
    usuario       VARCHAR(50)  NOT NULL UNIQUE,
    clave_acceso  VARCHAR(100) NOT NULL,
    ruta_imagen   VARCHAR(255),
    rol           VARCHAR(30)  NOT NULL DEFAULT 'ADMINISTRADOR',
    estado        VARCHAR(15)  NOT NULL DEFAULT 'ACTIVO'
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Empleados (GestionEmpleados.fxml)
-- ---------------------------------------------------------------------
CREATE TABLE empleados (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    nombre            VARCHAR(60)  NOT NULL,
    primer_apellido   VARCHAR(60)  NOT NULL,
    segundo_apellido  VARCHAR(60),
    nacimiento        DATE,
    email             VARCHAR(100),
    telefono          VARCHAR(15),
    rfc               VARCHAR(13)  NOT NULL UNIQUE,
    usuario           VARCHAR(50)  NOT NULL UNIQUE,
    contrasena        VARCHAR(100) NOT NULL,
    rol               VARCHAR(30)  NOT NULL,
    ruta_imagen       VARCHAR(255)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Clientes (GestionUsuarios.fxml)
-- ---------------------------------------------------------------------
CREATE TABLE clientes (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    rfc          VARCHAR(13)  NOT NULL UNIQUE,
    nombre       VARCHAR(120) NOT NULL,
    email        VARCHAR(100),
    telefono     VARCHAR(15),
    ruta_imagen  VARCHAR(255)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Habitaciones (GestionHabitaciones.fxml)
-- tipo = 'Sencilla' o 'Suite'. Los extras que no aplican quedan en 0.
-- ---------------------------------------------------------------------
CREATE TABLE habitaciones (
    numero            VARCHAR(4)    PRIMARY KEY,
    tipo              VARCHAR(20)   NOT NULL,
    piso              INT           NOT NULL,
    precio_noche      DECIMAL(10,2) NOT NULL,
    capacidad         INT           NOT NULL,
    estado            VARCHAR(20)   NOT NULL DEFAULT 'Disponible',
    numero_camas      INT           NOT NULL DEFAULT 1,
    balcon            TINYINT(1)    NOT NULL DEFAULT 0,
    vista_al_mar      TINYINT(1)    NOT NULL DEFAULT 0,
    jacuzzi           TINYINT(1)    NOT NULL DEFAULT 0,
    mini_bar          TINYINT(1)    NOT NULL DEFAULT 0,
    incluye_desayuno  TINYINT(1)    NOT NULL DEFAULT 0,
    ruta_imagen       VARCHAR(255)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Reservaciones (GestionReservas.fxml)
-- ---------------------------------------------------------------------
CREATE TABLE reservaciones (
    folio               VARCHAR(20)   PRIMARY KEY,
    nombre_cliente      VARCHAR(120)  NOT NULL,
    telefono_cliente    VARCHAR(15)   NOT NULL,
    numero_habitacion   VARCHAR(4)    NOT NULL,
    fecha_entrada       DATE          NOT NULL,
    fecha_salida        DATE          NOT NULL,
    costo_total         DECIMAL(10,2) NOT NULL,
    estado              VARCHAR(20)   NOT NULL DEFAULT 'CONFIRMADA',
    fecha_registro      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reserva_habitacion FOREIGN KEY (numero_habitacion)
        REFERENCES habitaciones(numero) ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Bitácora de actividad (tabla del Panel de inicio)
-- ---------------------------------------------------------------------
CREATE TABLE actividad (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    fecha_hora  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario     VARCHAR(50)  NOT NULL,
    accion      VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- =====================================================================
-- Datos de prueba
-- =====================================================================
INSERT INTO administradores (nombre, telefono, email, direccion, usuario, clave_acceso, ruta_imagen, rol, estado) VALUES
('Administrador General', '3311223344', 'admin@grandhotel.com', 'Guadalajara, Jal.', 'admin', 'admin123', '', 'ADMINISTRADOR', 'ACTIVO');

INSERT INTO empleados (nombre, primer_apellido, segundo_apellido, nacimiento, email, telefono, rfc, usuario, contrasena, rol, ruta_imagen) VALUES
('Laura', 'Gomez', 'Ruiz', '1995-04-12', 'laura@grandhotel.com', '3312345678', 'GORL950412AB1', 'recepcion', 'recep123', 'Recepción', ''),
('Pedro', 'Martinez', 'Lopez', '1990-09-30', 'pedro@grandhotel.com', '3398765432', 'MALP900930CD2', 'mantenimiento', 'mant123', 'Mantenimiento', '');

INSERT INTO clientes (rfc, nombre, email, telefono, ruta_imagen) VALUES
('HEGC010101XY1', 'Carlos Hernandez', 'carlos@correo.com', '3311112222', ''),
('PERA020202ZZ2', 'Ana Perez', 'ana@correo.com', '3322223333', '');

INSERT INTO habitaciones (numero, tipo, piso, precio_noche, capacidad, estado, numero_camas, balcon, vista_al_mar, jacuzzi, mini_bar, incluye_desayuno, ruta_imagen) VALUES
('101', 'Sencilla', 1,  500.00, 2, 'Disponible',    1, 0, 0, 0, 0, 0, ''),
('102', 'Sencilla', 1,  650.00, 3, 'Disponible',    2, 1, 0, 0, 0, 0, ''),
('103', 'Sencilla', 1,  700.00, 2, 'Mantenimiento', 1, 1, 1, 0, 0, 0, ''),
('201', 'Suite',    2, 1500.00, 4, 'Disponible',    2, 0, 0, 1, 1, 1, ''),
('202', 'Suite',    2, 1800.00, 4, 'Ocupada',       2, 0, 0, 1, 1, 0, '');

INSERT INTO reservaciones (folio, nombre_cliente, telefono_cliente, numero_habitacion, fecha_entrada, fecha_salida, costo_total, estado) VALUES
('RES-0001', 'Carlos Hernandez', '3311112222', '202', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 3600.00, 'CONFIRMADA');

INSERT INTO actividad (usuario, accion) VALUES
('sistema', 'Base de datos creada');
