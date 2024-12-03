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
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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
    private List<String> usarNameList;
    private boolean isFirst;
    private final Object isRunningLock = new Object();
    private final Object isFirstLock = new Object();
    private final Object ufoListLock = new Object();

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
    public void sendUserName(String text) {
        sendMessage("USER_NAME " + text);
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
    public boolean sendIsFirstClient() {
        sendMessage("IS_FIRST");
        synchronized (isFirstLock) {
            try {
                isFirstLock.wait();
         } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String jsonTrayectory = gson.toJson(selectedUfoTrayectory);
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

        synchronized (isRunningLock) {
            try {
                isRunningLock.wait();
         } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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

        synchronized (ufoListLock) {
            try {
                ufoListLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return ufoList; 
    }


    @Override
    public void startGame() {
        sendMessage("START_GAME");
    }

    public synchronized void sendMessage(String message) {
        try {
            writer.writeUTF(message);
            writer.flush();
        } catch (SocketException e) {
            if (e.getMessage().contains("Connection reset")) {
                handleServerDisconnected();
            } else {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleServerDisconnected() {
        System.err.println("El servidor cerró la conexión.");
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                "La conexión con el servidor se ha perdido.",
                "Conexión Perdida",
                JOptionPane.ERROR_MESSAGE);
                presenter.closeEverything();
        });
        try {
            if (client != null && !client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveIsRuning(String booleanMessage) {
        String booleanValue = booleanMessage.split(" ")[1];
        boolean isRunning = Boolean.parseBoolean(booleanValue);
        this.isRunning = isRunning;
    
        synchronized (isRunningLock) {
            isRunningLock.notify(); 
        }
    }
    

    public void receiveIsStopped(String booleanMessage){
        String booleanValue = booleanMessage.split(" ")[1];
        boolean isStopped = Boolean.parseBoolean(booleanValue);
        allUfoStopped = isStopped;
    }

    public void receiveIsFirstClient(String booleanMessage){
        String booleanValue = booleanMessage.split(" ")[1];
        boolean isFirst = Boolean.parseBoolean(booleanValue);
        this.isFirst = isFirst;
        synchronized (isFirstLock) {
            isFirstLock.notify(); 
        }
    }

    public void receiveSingleUfo(String jsonUfo) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Point.class, new PointAdapter());
            Gson gson = gsonBuilder.create();
            Ufo ufo = gson.fromJson(jsonUfo, Ufo.class);
            ufoAtPosition = ufo;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveUsersList(String jsonUserList) {
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {}.getType();
            usarNameList = gson.fromJson(jsonUserList, listType);
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
    
            synchronized (ufoListLock) {
                ufoListLock.notify(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private class ServerMessageHandler implements Runnable {

    private final UfoInterface.Presenter presenter;
    private final DataInputStream reader;
    private final Map<String, Consumer<String>> serverMessageHandlers;

    public ServerMessageHandler(UfoInterface.Presenter presenter, DataInputStream reader) {
        this.presenter = presenter;
        this.reader = reader;
        this.serverMessageHandlers = new HashMap<>();
        initializeMessageHandlers();
    }

    @Override
    public void run() {
        try {
            String serverMessage;
            while (true) {
                try {
                    serverMessage = reader.readUTF();
                    if (serverMessage != null) {
                        processServerMessage(serverMessage);
                    }
                } catch (SocketException e) {
                    if (e.getMessage().contains("Connection reset")) {
                        handleServerDisconnected();
                    }
                    break; // Salir del bucle al detectar la desconexión
                } catch (EOFException e) {
                    System.err.println("Se alcanzó el final del flujo de entrada. Cerrando conexión...");
                    break; // Salir del bucle cuando se detecta EOF
                } catch (IOException e) {
                    System.err.println("Error al leer el mensaje del servidor: " + e.getMessage());
                    break; // Salir del bucle por cualquier otro error de E/S
                }
            }
        } finally {
            // Limpieza final después de salir del bucle
            cleanUpResources();
        }
    }
    private void cleanUpResources() {
        try {
            if (reader != null) {
                reader.close();
            }
            if (client != null && !client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private void initializeMessageHandlers() {
        serverMessageHandlers.put("UFOS_LIST", this::handleUfosList);
        serverMessageHandlers.put("USERS_LIST", this::handleUsersList);
        serverMessageHandlers.put("UP_DATE_UFOS", this::handleUpdateUfos);
        serverMessageHandlers.put("SINGLE_UFO", this::handleSingleUfo);
        serverMessageHandlers.put("UFO_CRASHED_COUNT", this::handleUfoCrashedCount);
        serverMessageHandlers.put("UFO_ARRIVAL_COUNT", this::handleUfoArrivalCount);
        serverMessageHandlers.put("UFO_MOVING_COUNT", this::handleUfoMovingCount);
        serverMessageHandlers.put("FIRST_CLIENT", this::handleFirstClient);
        serverMessageHandlers.put("UFO_RUNNING", this::handleUfoRunning);
        serverMessageHandlers.put("UFO_STOPPED", this::handleUfoStopped);
    }

    private void processServerMessage(String serverMessage) {
        String command = extractCommand(serverMessage);
        serverMessageHandlers.getOrDefault(command, this::handleUnknownCommand).accept(serverMessage);
    }

    private String extractCommand(String message) {
        return message.contains(" ") ? message.split(" ")[0] : message;
    }

    private void handleUfosList(String message) {
        String jsonUfoList = message.substring("UFOS_LIST".length()).trim();
        receiveUfoList(jsonUfoList);
    }

    private void handleUsersList(String message) {
        String jsonUpdateList = message.substring("USERS_LIST".length()).trim();
        receiveUsersList(jsonUpdateList);
        presenter.updateUserNameList(usarNameList);
    }

    private void handleUpdateUfos(String message) {
        String jsonUpdateList = message.substring("UP_DATE_UFOS".length()).trim();
        receiveUfoList(jsonUpdateList);
        presenter.updateUfos(ufoList);
    }

    private void handleSingleUfo(String message) {
        String jsonUfo = message.substring("SINGLE_UFO".length()).trim();
        receiveSingleUfo(jsonUfo);
    }

    private void handleUfoCrashedCount(String message) {
        try {
            int crashedCount = Integer.parseInt(message.split(" ")[1]);
            presenter.updateScore(crashedCount);
        } catch (NumberFormatException e) {
            System.err.println("Error al parsear el contador de UFOs estrellados: " + message.split(" ")[1]);
        }
    }

    private void handleUfoArrivalCount(String message) {
        try {
            int arrivedCount = Integer.parseInt(message.split(" ")[1]);
            presenter.updateArrival(arrivedCount);
        } catch (NumberFormatException e) {
            System.err.println("Error al parsear el contador de UFOs llegados: " + message);
        }
    }

    private void handleUfoMovingCount(String message) {
        try {
            int movingCount = Integer.parseInt(message.split(" ")[1]);
            presenter.countMovingUfos(movingCount);
        } catch (NumberFormatException e) {
            System.err.println("Error al parsear el contador de UFOs en movimiento: " + message);
        }
    }

    private void handleFirstClient(String message) {
        receiveIsFirstClient(message);
    }

    private void handleUfoRunning(String message) {
        receiveIsRuning(message);
    }

    private void handleUfoStopped(String message) {
        receiveIsStopped(message);
    }

    private void handleUnknownCommand(String message) {
        System.out.println("Comando desconocido: " + message);
    }
}


}

