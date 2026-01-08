package org.example;

import java.io.*;
import java.util.Scanner;


enum Message {
    HIT ("trafiony"),
    MISS ("pudło"),
    SUNK("trafiony zatopiony"),
    LAST_SUNK("ostatni zatopiony"),
    START("start");
    private final String message;
    Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static Message convertStringToMessage(String msg){
        for(Message m: Message.values()) {
            if(m.message.equals(msg)) {
                return m;
            }
        }
        return null;
    }
}

public class App {

    private NetworkHandler network;
    private boolean isClient;
    private final BattleShipManager battleShipManager;
    private static final Scanner sc = new Scanner(System.in);
    private GameMessage lastSentMessage;


    public App() {
        battleShipManager = new BattleShipManager();
    }

    static void main(String[] args) throws IOException {
        App app = new App();

        app.setup(args);

        GameMessage msg = null;
        if(app.isClient) {
            msg = app.processTurn(msg);
            app.lastSentMessage = msg;
            app.network.sendMessage(msg.toString());
        }

        while(true) {
            //wait
            System.out.println("====== WAITING FOR ENEMY'S MOVE ======");
            //receive
            GameMessage incoming = app.receiveMessage();
            //check what happened
            System.out.println(incoming);
            GameMessage response = app.processTurn(incoming);
            //send
            app.network.sendMessage(response.toString());
            app.lastSentMessage = response;

            if(app.battleShipManager.isGameOver()) {
                break;
            }
        }

        if(app.battleShipManager.getResult() == BattleShipManager.Result.WON) {
            System.out.println("Wygrana");

            System.out.println("====== ENEMY'S MAP ======");
            app.battleShipManager.setEnemyMapAfterWinning();

        }
        else {
            System.out.println("Przegrana");
            System.out.println("====== ENEMY'S MAP ======");

        }
        app.battleShipManager.printBoard(app.battleShipManager.getEnemyMap());
        System.out.println();
        System.out.println("====== MY MAP =======");
        app.battleShipManager.printBoard(app.battleShipManager.getMyMap());

        app.disconnect();
    }

    private GameMessage receiveMessage() throws IOException {
        int tries = 0;
        while (true) {

            String incoming = network.readLine();
            GameMessage msg;
            if ((msg = GameMessage.isInvalid(incoming)) != null) {
                return msg;
            }

            tries++;

            if (tries < 3 && lastSentMessage != null) {
                System.out.println("Sending again");
                network.sendMessage(lastSentMessage.toString());
            } else {
                throw new IOException("ERROR: Wrong message, timeout limit of retries exceeded");
            }
        }
    }

    private void connect(int port, String hostName) throws IOException {
        if(isClient) {
            network = NetworkHandler.clientHandler(hostName, port);
        }
        else {
            network = NetworkHandler.serverHandler(port);
        }
    }

    private void disconnect() throws IOException  {
        network.close();
        if(sc != null) {
            sc.close();
        }
    }
    private GameMessage processTurn(GameMessage enemyMsg) {
        if(enemyMsg == null) {
            String move = askMove();
            battleShipManager.setLastMove(move);
            return new GameMessage(Message.START, move);
        }
        else {
            battleShipManager.updateEnemyMap(enemyMsg.status());
            Message enemyShotEffect = battleShipManager.checkEnemyShot(enemyMsg.coordinate());

            if(battleShipManager.isGameOver()) {
                return new GameMessage(enemyShotEffect, lastSentMessage.coordinate());
            }

            System.out.println("YOUR MAP");
            battleShipManager.printBoard(battleShipManager.getMyMap());

            System.out.println("ENEMY'S MAP");
            battleShipManager.printBoard(battleShipManager.getEnemyMap());

            String newMove = askMove();
            battleShipManager.setLastMove(newMove);
            return new GameMessage(enemyShotEffect, newMove);
        }
    }

    public void setup(String[] args) throws IOException {
        System.out.println("====== Setting up the BattleShip Game ======");
        System.out.println();

        Integer port = null;
        String hostName = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-mode")) {
                i++;
                isClient = args[i].equals("client");
            } else if (args[i].equals("-port")) {
                i++;
                port = Integer.parseInt(args[i]);

            } else if (args[i].equals("-map")) {
                try(BufferedReader in = new BufferedReader(new FileReader(args[i + 1]))) {
                    String map = in.readAllAsString();
                    battleShipManager.initMyMap(map);
                    battleShipManager.initEnemyMap();
                }

            } else if (args[i].equals("-host")) {
                i++;
                hostName = args[i];
            }
        }

        System.out.println("====== GAME INFO ======");
        System.out.println("MODE: " + (isClient ? "client" : "server"));
        System.out.println("PORT: " + port);
        System.out.println("====== MAP ======");
        battleShipManager.printBoard(battleShipManager.getMyMap());

        System.out.println("====== CONNECTING ======");
        connect(port, hostName);
        System.out.println("====== CONNECTED ======");
        System.out.println();
    }
    public String askMove() {
        System.out.print("Please enter your move: ");
        System.out.println();
        return sc.next().toUpperCase();
    }

}
