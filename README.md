# Consideraciones
- Se considera que el idTributario de un Proveedor es único y no puede ser modificado una vez creado.
- La paginación en el listado de Proveedor por estado espera un 0 como primera página.
- Se asume que en cada actualización de datos de un Proveedor, se entregarán todos los datos (razón social y email). Si no viene alguno, se considera malformado y se devuelve 400 Bad Request.
- En la creación de una Orden de Pago se asume que el "Proveedor asociado" se identifica por el id del proveedor.
- En el listado paginado de Ordenes de Pago, se asume que ambos filtros (estado y proveedor) son opcionales.

# Instalación