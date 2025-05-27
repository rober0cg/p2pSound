# Project Name
p2pSound

# Project Description
Programa con la intención de transmitir, y recibir, audio con la menor latencia posible.
Surge porque toco la guitarra con un amigo, durante el verano pasado quisimos mantener las sesiones y con whatsapp fue penoso. Intentamos con otro sw, pero... no investigamos lo suficiente. Se me ocurrió que podría "conectarse" el micro de un pc con el altavoz del otro y viceversa. Y tras unas pruebas aquí está una primerísima versión.

Para la transferencia del audio utiliza socket UDP; también hay socket TCP para control, en esta versión muy básico.
La interfaz es swing para la configuraicón de audio y conexión.
Respecto a la conexión tiene dos modos:
    - llamante: el socket TCP establece conexión con la host y puerto indicados. Este mismo host y puerto serán el destino de los mensajes UDP.
    - llamado: el socket TCP espera llamada entrante en el puerto indicado. Cuando se produce se obtiene la dirección del otro extremo, y esta será la dirección, junto con el puerto configurado, destino de los mensajes UDP.
El llamante envía por el socket TCP la configuración de audio que irá en los mensajes UDP. Si el llamante la acepta, pasamos a la fase de transferencia de audio.

Ya conectados hay 3 hilos en ejecución:
    - uno para la recepción por socket TCP, por ahora sólo con los mensajes de inicio y de fin;
    - otro para la lectura del dispositivo de audio de entrada (TargetDataLine) y envío por el socket UDP, y
    - otro para la recepción por el socket UDP y reproducción en el dispositivo de audio de salida (SourceDataLine).

Para la transferencia del audio por UDP hay un mini protocolo con una cabecera muy sencilla con 12 bytes que llevan:
    - id del protocolo;
    - command, por ahora sólo 2 valores: uno para mensaje de datos sin más, y otro para indicar que hay reseteo de timestamp;
    - longitud de los datos;
    - secuencia, un simple contador de mensajes, y
    - timestamp, con los microsegundos desde el primer envío, o desde el último reseteo.
Tras la cabecera va el audio en raw.

Tengo pendiente implementar que funcinen los controles de volumen de los dispositivos de audio de entrada y salida.
Como extras, ya que tanto en esto del java como con la guitarra soy aprendriz, se le podría implementar un metrónomo, un afinador, un analizador de espectro que indique las notas que suenan... tiene recorrido!!

Por ahora poco más...
!Esto es todo amigos!

# Contact
Todo comentario, sugerencia... es bien recibido.
¡Gracias!

rober_cg@telefonica.net
