package co.edu.uptc.models;

import co.edu.uptc.interfaces.UfoInterface;
import co.edu.uptc.pojos.Ufo;
import co.edu.uptc.utilities.PointAdapter;

import com.google.gson.Gson;  // Importamos Gson para trabajar con JSON
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.awt.Point;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class UfoClient implements UfoInterface.Model {

    private UfoInterface.Presenter presenter;
    private Socket client;
    private String userName;
    private DataOutputStream writer;
    private DataInputStream reader;
    private boolean isRunning;
    
    // Atributo para almacenar la lista de UFOs recibida
    private List<Ufo> ufoList;
    private List<Ufo> upDatedUfoList;

    @Override
    public void setPresenter(UfoInterface.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void startConnection(String ip, int port, String userName) throws UnknownHostException, IOException {
        this.userName = userName;
        client = new Socket(ip, port);
        writer = new DataOutputStream(client.getOutputStream());
        reader = new DataInputStream(client.getInputStream());

        // Iniciar un hilo para escuchar los mensajes del servidor
        new Thread(new ServerMessageHandler(presenter, reader)).start();
    }

    public void closeConnection() {
        try {
            if (client != null && !client.isClosed()) {
                writer.close();
                reader.close();
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendUfoCount(int ufoCount) {
        sendMessage("UFO_COUNT " + ufoCount);  // Enviar el número de UFOs como mensaje
    }

    @Override
    public void sendDefaultSpeed(int speed) {
        sendMessage("SPEED " + speed);  // Enviar la velocidad como mensaje
    }

    @Override
    public void sendAppearanceTime(int appearanceTime) {
        sendMessage("APPEARANCE_TIME " + appearanceTime);  // Enviar el tiempo de aparición como mensaje
    }

    @Override
    public void sendSelectedUfoSpeed(int delta) {
        sendMessage("SELECTED_UFO_SPEED " + delta);
    }

    @Override
    public void changeSelectedUfoSpeed() {
        sendMessage("CHANGE_SELECTED_UFO_SPEED");
    }

    @Override
    public void sendXPosition(int x) {
        sendMessage("X_POSITION " + x);
    }

    @Override
    public void sendYPosition(int y) {
        sendMessage("Y_POSITION " + y);
    }

    @Override
    public Ufo selectUfoAtPosition() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selectUfoAtPosition'");
    }

    @Override
    public boolean isRunning() {
        sendMessage("SEND_RUNNING_STATE");
        return isRunning; // Retornar el estado de isRunning
    }

    @Override
    public List<Ufo> getUfosList() {
        sendMessage("SEND_UFOS");
        return ufoList;  // Devolver la lista de UFOs almacenada
    }

    @Override
    public void startGame() {
        sendMessage("START_GAME");
    }

    // Método genérico para enviar mensajes al servidor
    public void sendMessage(String msg) {
        try {
            writer.writeUTF(msg);  // Enviar el mensaje como cadena UTF-8
            writer.flush();  // Asegurarse de que los datos se envíen inmediatamente
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para recibir la lista de UFOs en formato JSON
   public void receiveUfoList(String jsonUfoList) {
        try {
            // Creamos el objeto Gson con el adaptador registrado
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Point.class, new PointAdapter());  // Registra el adaptador para Point
            Gson gson = gsonBuilder.create();

            // Usamos TypeToken para deserializar una lista de objetos Ufo
            Type listType = new TypeToken<List<Ufo>>() {}.getType();
            ufoList = gson.fromJson(jsonUfoList, listType);

            // Procesamos la lista de UFOs recibida
            processUfoList(ufoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processUfoList(List<Ufo> ufoList) {
        // Lógica para procesar la lista de UFOs
        for (Ufo ufo : ufoList) {
            System.out.println("UFO en posición: " + ufo.getPosition());
        }
    }

    // Método para recibir mensajes del servidor
    private String receiveMessage() {
        try {
            return reader.readUTF();  // Leer el mensaje como cadena de texto
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Clase interna que maneja los mensajes del servidor
    private class ServerMessageHandler implements Runnable {

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
                    if (serverMessage.startsWith("UFOS_LIST")) {
                        // Extraemos la lista de UFOs en formato JSON que sigue a "UFOS_LIST"
                        String jsonUfoList = serverMessage.substring("UFOS_LIST".length()).trim();
                        receiveUfoList(jsonUfoList);
                        presenter.updateUfos(upDatedUfoList);
                    } else if (serverMessage.startsWith("UP_DATE_UFOS")) {
                        String jsonUpdateList = serverMessage.substring("UP_DATE_UFOS".length()).trim();
                        receiveUfoList(jsonUpdateList);
                    }else if (serverMessage.startsWith("UFO_CRASHED_COUNT")) {
                        int crashedCount = Integer.parseInt(serverMessage.split(" ")[1]);
                        presenter.updateScore(crashedCount); // Notificar al presenter del número de UFOs estrellados
                    } else if (serverMessage.startsWith("UFO_ARRIVAL_COUNT")) {
                        int arrivedCount = Integer.parseInt(serverMessage.split(" ")[1]);
                        presenter.updateArrival(arrivedCount); // Notificar al presenter del número de UFOs que han llegado
                    } else if (serverMessage.startsWith("UFO_MOVING_COUNT")) {
                        int movingCount = Integer.parseInt(serverMessage.split(" ")[1]);
                        presenter.countMovingUfos(movingCount); // Notificar al presenter del número de UFOs en movimiento
                    } else if (serverMessage.startsWith("UFO_RUNNING")) {
                        // Extraer la parte del mensaje que contiene el valor booleano
                        String booleanValue = serverMessage.split(" ")[1]; // El segundo valor sería "true" o "false"
                        boolean isRunning = Boolean.parseBoolean(booleanValue);
                        UfoClient.this.isRunning = isRunning;
                    } else {
                        System.out.println("Comando desconocido: " + serverMessage);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

