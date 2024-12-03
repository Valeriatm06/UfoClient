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
    private List<String> usarNameList;
    private boolean isFirst;
    private final Object isRunningLock = new Object();
    private final Object isStoppedLock = new Object();
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
        // synchronized (isStoppedLock) {
        //     try {
        //         isStoppedLock.wait();
        //  } catch (InterruptedException e) {
        //         e.printStackTrace();
        //     }
        // }
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

    public synchronized void sendMessage(String msg) {
        try {
            writer.writeUTF(msg);
            writer.flush(); 
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
        // synchronized (isStoppedLock) {
        //     isStoppedLock.notify(); 
        // }
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
        
            System.out.println("JSON recibido para UFO: " + jsonUfo);
        
            Ufo ufo = gson.fromJson(jsonUfo, Ufo.class);
        
            ufoAtPosition = ufo;
        
            System.out.println("Recibido UFO en posición: " + ufoAtPosition.getPosition());
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
            System.out.println("Error al deserializar la lista de usuarios: " + jsonUserList);
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
                while (true) {
                    try {
                        serverMessage = reader.readUTF();
                        if (serverMessage != null) {
                            processServerMessage(serverMessage);
                        }
                    } catch (EOFException e) {
                        System.err.println("Se alcanzó el final del flujo de entrada. Cerrando conexión...");
                    } catch (IOException e) {
                        System.err.println("Error al leer el mensaje del servidor: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
        private void processServerMessage(String serverMessage) {
            if (serverMessage.startsWith("UFOS_LIST")) {
                String jsonUfoList = serverMessage.substring("UFOS_LIST".length()).trim();
                receiveUfoList(jsonUfoList);
            } else if (serverMessage.startsWith("USERS_LIST")) {
                String jsonUpdateList = serverMessage.substring("USERS_LIST".length()).trim();
                receiveUsersList(jsonUpdateList);
                presenter.updateUserNameList(usarNameList);
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
                receiveIsFirstClient(serverMessage);
            } else if (serverMessage.startsWith("UFO_RUNNING")) {
                receiveIsRuning(serverMessage);
            } else if (serverMessage.startsWith("UFO_STOPPED")) {
                receiveIsStopped(serverMessage);
            } else {
                System.out.println("Comando desconocido: " + serverMessage);
            }
        }
    }

}

