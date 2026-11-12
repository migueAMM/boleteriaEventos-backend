# Requisitos funcionales pendientes del backend

## Cambios por requisito

| RF | Implementación |
|---|---|
| RF1 | Actualización de municipios, tipos de prestador y prestadores. Las eliminaciones usan las claves foráneas para proteger los registros relacionados y responden con 409 ante un conflicto. |
| RF2 | Registro con BCrypt e inicio de sesión para usuarios activos. Las credenciales incorrectas, las cuentas inactivas y las claves antiguas en texto plano reciben 401. El resultado incluye el rol. |
| RF3 | Actualización de nombre, documento y teléfono. La asociación con el usuario se conserva y un documento duplicado produce 409. |
| RF7 | Actualización de nombre, descuento y requisitos de acompañamiento. Se mantienen las validaciones del alta de tipos de entrada. |
| RF8 | Consulta de capacidad por zona para una función. El conteo reutiliza el criterio de aforo de la emisión y excluye las boletas anuladas. |
| RF12 | Actualización de datos y reparto de combos. La composición debe sumar 100 %. Si hay boletas asociadas, se permiten cambios de datos generales y se rechazan la eliminación y los cambios de composición. |
| RF16 | Notificaciones internas de confirmación, expiración y cancelación de función. Se guarda una notificación por reserva afectada por el evento. El backend carece de una operación de cancelación de reserva independiente. |
| RF18 | Reportes de aforo, ingresos por atracción y tipo, y ventas de combos. El aforo refleja las boletas no anuladas. Los ingresos cuentan solo boletas ACTIVA o USADA de reservas CONFIRMADA y suman el precio pagado registrado. |

Para el reporte de combos, cada combinación de reserva y combo cuenta como una compra. El ingreso suma el importe registrado de sus boletas elegibles. El reporte por atracción requiere que la boleta esté asociada a una zona. Las boletas históricas sin zona pueden aparecer en el reporte de combos, pero carecen de una atracción atribuible.

## Endpoints nuevos

Las rutas se agregan al prefijo existente `/api/v1`. Los identificadores de ruta son numéricos. Los siguientes ejemplos incluyen el cuerpo de la solicitud para PUT y POST, y el de la respuesta para GET.

| Método y ruta | Parámetro | Ejemplo JSON |
|---|---|---|
| `PUT /municipios/{id}` | ID del municipio | `{"nombre":"Medellín","departamento":"Antioquia"}` |
| `PUT /tipos-prestador/{id}` | ID del tipo de prestador | `{"nombre":"Parque temático"}` |
| `PUT /prestadores/{id}` | ID del prestador | `{"nit":"900123456","nombre":"Parque Central","correo":"info@ejemplo.com","idMunicipio":1,"idTipoPrestador":2}` |
| `POST /usuarios-sistema/login` | Correo y clave en el cuerpo | `{"correo":"cliente@ejemplo.com","clave":"mi-clave"}` |
| `PUT /perfiles-cliente/{id}` | ID del perfil | `{"documento":"123456","nombre":"Ana Gómez","telefono":"3001234567"}` |
| `PUT /tipos-entrada/{id}` | ID del tipo de entrada | `{"nombre":"Infantil","porcentajeDescuento":20,"requiereAdulto":true,"esAdulto":false}` |
| `PUT /combos/{id}` | ID del combo. El campo atracciones es opcional. | `{"nombre":"Doble visita","precio":50000,"diasVigencia":15,"estado":"ACTIVO","atracciones":[{"idAtraccion":1,"porcentajePrecio":60},{"idAtraccion":2,"porcentajePrecio":40}]}` |
| `GET /zonas/funcion/{idFuncion}/disponibilidad` | ID de la función | `{"idFuncion":2,"zonas":[{"idZona":7,"nombre":"General","aforoTotal":100,"ocupadas":35,"disponibles":65}]}` |
| `GET /reportes/aforo-por-funcion` | Ninguno | `[{"idFuncion":2,"atraccion":"Planetario","fecha":"2026-11-15","horaApertura":"10:00:00","horaCierre":"12:00:00","aforoTotal":100,"ocupadas":35,"disponibles":65,"porcentajeOcupacion":35.00}]` |
| `GET /reportes/ingresos-por-atraccion-y-tipo` | Ninguno | `[{"idAtraccion":1,"atraccion":"Planetario","idTipoEntrada":3,"tipoEntrada":"General Adulto","boletasVendidas":5,"ingresos":125000.00}]` |
| `GET /reportes/combos-mas-vendidos` | Ninguno | `[{"idCombo":1,"nombre":"Doble visita","compras":4,"ingresos":200000.00}]` |

El login devuelve HTTP 200 con un cuerpo como `{"idUsuario":7,"correo":"cliente@ejemplo.com","estado":"ACTIVO","idRol":3,"nombreRol":"CLIENTE"}`. Para credenciales inválidas responde HTTP 401 con cuerpo vacío. El login verifica credenciales y devuelve identidad y rol. Los permisos existentes se conservan.

Las eliminaciones reciben únicamente el ID en la ruta. Devuelven HTTP 204 con cuerpo vacío o HTTP 409 si existen dependencias. Un ID inexistente devuelve HTTP 404.

| Método y ruta | Parámetro | JSON |
|---|---|---|
| `DELETE /municipios/{id}` | ID del municipio | Sin cuerpo |
| `DELETE /tipos-prestador/{id}` | ID del tipo de prestador | Sin cuerpo |
| `DELETE /prestadores/{id}` | ID del prestador | Sin cuerpo |
| `DELETE /combos/{id}` | ID del combo | Sin cuerpo |

Las notificaciones automáticas se consultan en la ruta existente `GET /notificaciones/usuario/{idUsuario}`.

## Migración de contraseñas antiguas

1. Identificar y desactivar las cuentas con valores sin un hash compatible, sin imprimir ni exportar las contraseñas.
2. Verificar la identidad del titular y solicitar una contraseña nueva mediante un proceso administrativo de restablecimiento.
3. Guardar únicamente el hash de la nueva contraseña. Usar BCrypt directo hasta 72 bytes UTF-8. Para claves más largas, aplicar la misma codificación versionada SHA-384 y BCrypt del registro antes de almacenar el hash.
4. Reactivar la cuenta después de guardar el hash. Las cuentas que ya tienen un hash BCrypt compatible pueden usar el login con su contraseña vigente.

## Archivos creados

- `dto/PerfilClienteUpdateDTO.java` define los campos que se pueden editar del perfil.
- `dto/ComboUpdateDTO.java` define los datos del combo y su reparto opcional.
- `dto/DisponibilidadFuncionDTO.java` y `dto/DisponibilidadZonaDTO.java` definen la consulta por función y zona.
- `dto/AforoFuncionDTO.java`, `dto/IngresosAtraccionDTO.java` y `dto/ComboVentasDTO.java` definen las proyecciones de reportes.
- `controller/ReporteController.java` y `service/ReporteService.java` exponen las consultas y aplican el criterio de ventas efectivas.
- `RequisitosPendientesTest.java` cubre los cambios de servicios y el contrato HTTP de usuarios.
- `RF_PENDIENTES.md` documenta los cambios y sus contratos.

## Archivos modificados

| Archivos | Cambio |
|---|---|
| `MunicipioController.java`, `TipoPrestadorController.java`, `PrestadorController.java` | Rutas administrativas PUT y DELETE. |
| `MunicipioService.java`, `TipoPrestadorService.java`, `PrestadorService.java` | Actualizaciones y respuesta controlada por conflictos de integridad. |
| `MunicipioRepository.java`, `TipoPrestadorRepository.java`, `PrestadorRepository.java` | Eliminaciones directas que evitan las cascadas de JPA. |
| `UsuarioSistemaController.java`, `UsuarioSistemaService.java` | Login y codificación BCrypt del registro. |
| `PerfilClienteController.java`, `PerfilClienteService.java` | Actualización del perfil y control de documento único. |
| `TipoEntradaController.java`, `TipoEntradaService.java` | Actualización de tipos de entrada y sus descuentos. |
| `ZonaController.java`, `ZonaService.java` | Disponibilidad de solo lectura por función. |
| `ComboController.java`, `ComboService.java`, `ComboRepository.java`, `ComboAtraccionRepository.java` | Actualización de combos y del reparto. Eliminación protegida frente a compras existentes. |
| `NotificacionService.java`, `ReservaService.java`, `FuncionService.java` | Registro interno de notificaciones al ejecutar las transiciones existentes. |
| `FuncionRepository.java`, `BoletaRepository.java` | Consultas de reportes. El repositorio de boletas también comprueba si un combo tiene compras asociadas. |
| `ReglasBoleteriaTest.java` | Ajuste de las dependencias de prueba y verificación de las notificaciones al expirar o cancelar. Se conservan las aserciones de las reglas existentes. |

## Verificación

- `gradlew.bat test --offline --console=plain` compiló el proyecto y ejecutó 17 pruebas con Java 21. Pasaron la prueba de arranque, las seis pruebas de reglas y las diez pruebas de los requisitos añadidos.
- Las pruebas HTTP verificaron el login y las rutas existentes de registro y consulta de usuarios. Hibernate validó las consultas HQL durante el arranque.
- Se ejecutaron las pruebas con la actualización de esquema desactivada. La conexión de solo lectura a Oracle funcionó, pero el esquema configurado carecía de las seis tablas del flujo inspeccionadas. Queda pendiente ejecutar los reportes y verificar la integridad referencial con un esquema Oracle inicializado.
