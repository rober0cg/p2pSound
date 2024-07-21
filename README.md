Programa muy simple que intenta enviar y recibir audio entre dos equipos conectados directamente.
Al estar conectados "peer to peer" se minimiza al máximo posible los "saltos" entre equipos, y así conseguir el objetivo de minimizar retrasos.
La necesidad surgió cuando mi compañero de guitarra ha iniciado vacaciones, y ver que la experiencia de estar conectados vía whatsapp mete un retraso considerable para un ensayo.

Esquemáticamente:
  - por un lado, captura de audio y envío sin procesar,
  - y por otro, recepción y reproducción directa.

El proceso utiliza 2 sockets:
  - uno TCP para iniciar el proceso, intercambiar configuración,
  - y otro UDP para el envío de los paquetes de frames.

El uso es sencillo:
  Configuración de audio, calidades y paquetes:
  - selección de los dispositivos de audio del equipo local: el de captura (mic, line-in...) y el de reproducir (altavoz, line-out...);
  - selección de la calidad del audio: frecuencia de muestreo (8, 16, 44.1, 48 KHz), número de canales (1 mono, 2 stereo), bits por muestra (8, 16);
  - configurar el tamaño de los paquetes, como un número de frames. Hay dos valores: uno para el primer paquete y así "cebar buffers", y otro para todos los demás.
  Datos para conexión:
  - indicar si se actúa como llammante o llamado,
  - nombre o ip del equipo remoto, sólo necesario si actuamos como llamante,
  - puerto en el que está el llamado, neceario para llamante y llamado; este puerto se utiliza en ambas conexiones, la TCP y la UDP.

El funcionamiento:
  Conexión:
  - Lo primero es crear el socket UDP en el puerto configurado.
  - Un equipo crea el socket TCP que espera ser llamado, también en el puerto configurado.
  - El otro equipo establece la conexión con su socket TCP al destino y puerto configurados.
  - Este socket TCP por ahora sólo se usa para que el llamante envíe su configuración de audio ( rate, channels, bits y tamaño paquetes), y el llamado valide.
  Proceso principal:
  - Ya conectados se arrancan dos hilos:
    - uno par capturar audio y enviarlo por el socket UDP,
    - otro para recibir, también por el socket UDP, y reproducirlo.
  - El primer paquete esá preparado para que tenga un tamaño mayor y así "alimentar" buffers y evitar "chisporroteos".
  - El resto del proceso en un bucle infinito en capturar-envíary recebir-reproducir utilizando un tamaño de paquete menor.

El código es muy mejorable:
- Tiene una "mini" versión del RTP pero que en la verisón inicial no se utiliza ni el control de secuencia, ni de flujo...
- Tiene implementado método para controlar retrasos e intentar recuperarlos aprovechando parte del buffer...
