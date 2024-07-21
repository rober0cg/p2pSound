Programa muy simple que intenta enviar y recibir audio entre dos equipos conectados directamente.
Al estar conectados "peer to peer" se minimiza al m�ximo posible los "saltos" entre equipos, y as� conseguir el objetivo de minimizar retrasos.
La necesidad surgi� cuando mi compa�ero de guitarra ha iniciado vacaciones, y ver que la experiencia de estar conectados v�a whatsapp mete un retraso considerable para un ensayo.

Esquem�ticamente:
  - por un lado, captura de audio y env�o sin procesar,
  - y por otro, recepci�n y reproducci�n directa.

El proceso utiliza 2 sockets:
  - uno TCP para iniciar el proceso, intercambiar configuraci�n,
  - y otro UDP para el env�o de los paquetes de frames.

El uso es sencillo:
  Configuraci�n de audio, calidades y paquetes:
  - selecci�n de los dispositivos de audio del equipo local: el de captura (mic, line-in...) y el de reproducir (altavoz, line-out...);
  - selecci�n de la calidad del audio: frecuencia de muestreo (8, 16, 44.1, 48 KHz), n�mero de canales (1 mono, 2 stereo), bits por muestra (8, 16);
  - configurar el tama�o de los paquetes, como un n�mero de frames. Hay dos valores: uno para el primer paquete y as� "cebar buffers", y otro para todos los dem�s.
  Datos para conexi�n:
  - indicar si se act�a como llammante o llamado,
  - nombre o ip del equipo remoto, s�lo necesario si actuamos como llamante,
  - puerto en el que est� el llamado, neceario para llamante y llamado; este puerto se utiliza en ambas conexiones, la TCP y la UDP.

El funcionamiento:
  Conexi�n:
  - Lo primero es crear el socket UDP en el puerto configurado.
  - Un equipo crea el socket TCP que espera ser llamado, tambi�n en el puerto configurado.
  - El otro equipo establece la conexi�n con su socket TCP al destino y puerto configurados.
  - Este socket TCP por ahora s�lo se usa para que el llamante env�e su configuraci�n de audio ( rate, channels, bits y tama�o paquetes), y el llamado valide.
  Proceso principal:
  - Ya conectados se arrancan dos hilos:
    - uno par capturar audio y enviarlo por el socket UDP,
    - otro para recibir, tambi�n por el socket UDP, y reproducirlo.
  - El primer paquete es� preparado para que tenga un tama�o mayor y as� "alimentar" buffers y evitar "chisporroteos".
  - El resto del proceso en un bucle infinito en capturar-env�ary recebir-reproducir utilizando un tama�o de paquete menor.

El c�digo es muy mejorable:
- Tiene una "mini" versi�n del RTP pero que en la veris�n inicial no se utiliza ni el control de secuencia, ni de flujo...
- Tiene implementado m�todo para controlar retrasos e intentar recuperarlos aprovechando parte del buffer...
