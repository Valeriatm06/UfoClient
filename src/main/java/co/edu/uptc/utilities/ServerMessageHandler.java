package co.edu.uptc.utilities;

import co.edu.uptc.interfaces.UfoInterface;

import java.io.DataInputStream;
import java.io.IOException;

public class ServerMessageHandler implements Runnable {

    private UfoInterface.Presenter presenter;
    private DataInputStream reader;

    public ServerMessageHandler(UfoInterface.Presenter presenter, DataInputStream reader) {
        this.presenter = presenter;
        this.reader = reader;
    }

    @Override
    public void run() {
        try {
            String serverMessage;
            while ((serverMessage = reader.readUTF()) != null) {
                System.out.println("Mensaje del servidor: " + serverMessage);

                // Procesar los mensajes del servidor
                if (serverMessage.startsWith("UFO_LIST")) {
                    // Lógica para procesar la lista de UFOs (si es necesario)
                } else if (serverMessage.startsWith("UFO_CRASHED_COUNT")) {
                    int crashedCount = Integer.parseInt(serverMessage.split(" ")[1]);
                    presenter.updateScore(crashedCount); // Notificar al presenter del número de UFOs estrellados
                } else if (serverMessage.startsWith("UFO_ARRIVAL_COUNT")) {
                    int arrivedCount = Integer.parseInt(serverMessage.split(" ")[1]);
                    presenter.updateArrival(arrivedCount); // Notificar al presenter del número de UFOs que han llegado
                } else if (serverMessage.startsWith("UFO_MOVING_COUNT")) {
                    int movingCount = Integer.parseInt(serverMessage.split(" ")[1]);
                    presenter.countMovingUfos(movingCount); // Notificar al presenter del número de UFOs en movimiento
                } else if (serverMessage.startsWith("UFO_RUNING")) {
                    // Extraer la parte del mensaje que contiene el valor booleano
                    String booleanValue = serverMessage.split(" ")[1]; // El segundo valor sería "true" o "false"
                    boolean isRunning = Boolean.parseBoolean(booleanValue);
                    isRunning(isRunning); // Asumiendo que tienes un método en el presenter para manejar esto
                } else {
                    System.out.println("Comando desconocido: " + serverMessage);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean isRunning(boolean isRunning){
        return isRunning;
    }
}

