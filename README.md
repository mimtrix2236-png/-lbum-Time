# Álbum Time

Aplicación Android para organizar, visualizar, recortar y componer fotografías con herramientas creativas y funciones inteligentes.

## Funciones previstas
- Galería del dispositivo y álbumes.
- **Biblioteca de plantillas editables**: ejemplos de fotos y composiciones de referencia para comenzar a editar.
- **Plantillas con fotos de ejemplo**: las imágenes de muestra pueden abrirse en el editor y usarse como base para probar recortes, fondos, filtros, ajustes y composiciones.
- **Editar fotos propias**: importar una foto desde la galería o cámara (si el usuario concede permiso) y comenzar una edición desde cero.
- Recorte libre y por proporciones, rotación y ajustes de encuadre.
- Recortar una persona u objeto como sticker/capa.
- **Quitar fondo inteligente**: separar sujeto y fondo, con refinado manual de bordes.
- Pegar y combinar recortes en otras fotos o fondos; mover, escalar, girar y ordenar capas.
- Cambiar fondos por colores, fotos propias o fondos incluidos.
- Borrador inteligente para eliminar objetos (IA cuando esté disponible).
- Brillo, contraste, saturación, temperatura, nitidez y desenfoque.
- Filtros, estilos creativos, ilustración y anime.
- Antes/después, deshacer/rehacer.
- Exportar como copia y compartir; conservar originales por defecto.

## Sección Plantillas
La pantalla de plantillas tendrá:
- **Plantillas incluidas** con ejemplos y composiciones de referencia.
- Categorías (por ejemplo: retratos, fondos, collage, recortes, estilos y efectos).
- Vista previa y botón **«Usar plantilla»** para abrir la plantilla en el editor.
- Opción **«Reemplazar foto»** para usar una foto propia dentro de la plantilla, cuando la plantilla tenga espacios preparados.
- Opción para guardar composiciones del usuario como plantilla personal.
- Plantillas editables: no se aplanará la composición; se conservarán las capas y los ajustes cuando sea posible.

Los archivos de imagen que se hayan compartido o generado en conversaciones anteriores deben incorporarse al catálogo como recursos reales del proyecto una vez que estén disponibles para el repositorio. No se deben inventar ni sustituir las fotos originales por imágenes distintas.

## MVP
1. Inicio y galería.
2. Pantalla de plantillas y vista previa.
3. Seleccionar una plantilla o importar foto propia.
4. Editor básico: recorte, rotación, brillo, contraste y saturación.
5. Guardar copia.
6. Base de capas para composición y recortes.

## Etapas
1. Interfaz Android en español.
2. Galería, plantillas e importación de fotos.
3. Editor base.
4. Recortes y composición por capas.
5. Segmentación inteligente y quitar fondo.
6. Borrado inteligente y estilos IA.
7. Compilar, probar y entregar APK.

## Privacidad
Priorizar procesamiento local. Las funciones que requieran servicios externos deben avisar claramente si la imagen se envía, y si se requiere conexión, cuenta o costo.


## Catálogo de plantillas inicial

Se registró el primer catálogo editable en `templates/catalog.json`, con estilos de parque/naturaleza, fondo verde y recorte, verano/piscina, retrato mejorado, cambio de estilo/vestuario, romántica rosa, anime y montaje libre. La documentación está en `templates/README.md`. Las plantillas están definidas como especificaciones; sus funciones aún deben implementarse en la app y las fotos de referencia deben incorporarse como recursos reales antes de empaquetarlas.
