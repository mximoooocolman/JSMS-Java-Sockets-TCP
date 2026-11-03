PROYECTO JSMS - Java Sockets TCP

Requisitos:
- JDK 8 o superior.
- Compilar desde la carpeta del proyecto:
  javac *.java

Ejecutar:
1) Terminal 1: java MultiClientServer
2) Terminal 2: java JSMSClient
3) Terminal 3: java JSMSClient
4) En cada cliente escribir:
   CONNECT nombre
   JOIN general
   MSG Hola a todos
   QUIT

Para probar el ejercicio 1:
1) Terminal 1: java EchoServer
2) Terminal 2: java EchoClient

El servidor JSMS final integra los ejercicios 2, 3, 4 y 5.
EchoServer y EchoClient corresponden al ejercicio 1.
