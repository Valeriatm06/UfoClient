package co.edu.uptc.models;

import co.edu.uptc.interfaces.UfoInterface;
import co.edu.uptc.pojos.Ufo;
import co.edu.uptc.utilities.PointAdapter;

import com.google.gson.Gson;  
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
    private boolean allUfoStopped;
    private Ufo ufoAtPosition;
    private List<Ufo> ufoList;
    private boolean isFirst;

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
        sendMessage("UFO_COUNT " + ufoCount);  
    }

    @Override
    public void sendDefaultSpeed(int speed) {
        sendMessage("SPEED " + speed); 
    }

    @Override
    public void sendAppearanceTime(int appearanceTime) {
        sendMessage("APPEARANCE_TIME " + appearanceTime);  
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
    public boolean isFirstClient() {
        sendMessage("IS_FIRST");
        return isFirst;
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
       sendMessage("SEND_SELECTED_UFO");
        return ufoAtPosition;
    }

    @Override
public void sendSelectedUfo(Ufo selectedUfo) {
    try {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Point.class, new PointAdapter()); 
        Gson gson = gsonBuilder.create();

        String ufoJson = gson.toJson(selectedUfo);

        sendMessage("SELECTED_UFO " + ufoJson);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    @Override
public synchronized void sendSelectedUfoTrayectory(List<Point> selectedUfoTrayectory) {
    try {
        // Crear el objeto Gson y el adaptador para Point
        GsonBuilder gsonBuilder = new GsonBuilder();
        // gsonBuilder.registerTypeAdapter(Point.class, new PointAdapter()); 
        Gson gson = gsonBuilder.create();

        // Convertir la lista de trayectorias en una cadena JSON
        String jsonTrayectory = gson.toJson(selectedUfoTrayectory);
        
        // Agregar la etiqueta "SELECTED_UFO_TRAYECTORY" y enviar el mensaje
        String message = "SELECTED_UFO_TRAYECTORY " + jsonTrayectory;
        
        // Depuración: Imprimir el mensaje para asegurarnos de que se está enviando correctamente
        System.out.println("Enviando mensaje al servidor: " + message);
        
        sendMessage("TRAYECTORY " + jsonTrayectory);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    


    @Override
    public void startUfoMovement() {
        sendMessage("START_UFO_MOVEMENT");
    }

    @Override
    public boolean isRunning() {
        sendMessage("SEND_RUNNING_STATE");
        return isRunning; 
    }

    @Override
    public boolean allUfosStopped() {
        sendMessage("SEND_UFOS_STOPPED");
        return allUfoStopped;
    }

    @Override
    public List<Ufo> getUfosList() {
        sendMessage("SEND_UFOS");
        return ufoList;  
    }

    @Override
    public void startGame() {
        sendMessage("START_GAME");
    }

    public synchronized void sendMessage(String msg) {
        try {
            writer.writeUTF(msg);
            writer.flush(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveSingleUfo(String jsonUfo) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Point.class, new PointAdapter());
            Gson gson = gsonBuilder.create();
        
            System.out.println("JSON recibido para UFO: " + jsonUfo);
        
            Ufo ufo = gson.fromJson(jsonUfo, Ufo.class);
        
            ufoAtPosition = ufo;
        
            System.out.println("Recibido UFO en posición: " + ufoAtPosition.getPosition());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
   public void receiveUfoList(String jsonUfoList) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Point.class, new PointAdapter());  
            Gson gson = gsonBuilder.create();

            Type listType = new TypeToken<List<Ufo>>() {}.getType();
            ufoList = gson.fromJson(jsonUfoList, listType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
                    // System.out.println("Mensaje del servidor: " + serverMessage);
                    
                    if (serverMessage.startsWith("UFOS_LIST")) {
                        String jsonUfoList = serverMessage.substring("UFOS_LIST".length()).trim();
                        receiveUfoList(jsonUfoList);
                    } else if (serverMessage.startsWith("UP_DATE_UFOS")) {
                        String jsonUpdateList = serverMessage.substring("UP_DATE_UFOS".length()).trim();
                        receiveUfoList(jsonUpdateList);
                        presenter.updateUfos(ufoList);
                    } else if (serverMessage.startsWith("SINGLE_UFO")) {
                        String jsonUfo = serverMessage.substring("SINGLE_UFO".length()).trim();
                        receiveSingleUfo(jsonUfo);
                    } else if (serverMessage.startsWith("UFO_CRASHED_COUNT")) {
                        try {
                            int crashedCount = Integer.parseInt(serverMessage.split(" ")[1]);
                            presenter.updateScore(crashedCount);
                        } catch (NumberFormatException e) {
                            System.err.println("Error al parsear el contador de UFOs estrellados: " + serverMessage.split(" ")[1]);
                            e.printStackTrace();
                        }
                    } else if (serverMessage.startsWith("UFO_ARRIVAL_COUNT")) {
                        String[] parts = serverMessage.split(" ");
                        if (parts.length > 1 && !parts[1].isEmpty()) {
                            try {
                                int arrivedCount = Integer.parseInt(parts[1]);
                                presenter.updateArrival(arrivedCount);
                            } catch (NumberFormatException e) {
                                System.err.println("Error al parsear el contador de UFOs llegados: " + parts[1]);
                                e.printStackTrace();
                            }
                        } else {
                            System.err.println("Mensaje mal formado para UFO_ARRIVAL_COUNT: " + serverMessage);
                        }
                    } else if (serverMessage.startsWith("UFO_MOVING_COUNT")) {
                        int movingCount = Integer.parseInt(serverMessage.split(" ")[1]);
                        presenter.countMovingUfos(movingCount);
                    } else if (serverMessage.startsWith("FIRST_CLIENT")) {
                        String[] parts = serverMessage.split(" ");
                        if (parts.length > 1) {
                            String booleanValue = parts[1];
                            System.out.println("Valor recibido para FIRST CLIENT: " + booleanValue);
                            boolean isFirst = Boolean.parseBoolean(booleanValue);
                            UfoClient.this.isFirst = isFirst;
                        } else {
                            System.err.println("Mensaje mal formado para UFO_RUNNING: " + serverMessage);
                        }
                    }else if (serverMessage.startsWith("UFO_RUNNING")) {
                        String booleanValue = serverMessage.split(" ")[1];
                        System.out.println("Valor recibido para UFO_RUNNING: " + booleanValue);
                        boolean isRunning = Boolean.parseBoolean(booleanValue);
                        UfoClient.this.isRunning = isRunning;
                    } else if (serverMessage.startsWith("UFO_STOPPED")) {
                        String booleanValue = serverMessage.split(" ")[1];
                        System.out.println("Valor recibido para UFO_RUNNING: " + booleanValue); // Depuración
                        boolean isStopped = Boolean.parseBoolean(booleanValue);
                        UfoClient.this.allUfoStopped = isStopped; 
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

