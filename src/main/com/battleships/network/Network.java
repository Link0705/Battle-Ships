package com.battleships.network;

import com.battleships.gui.gameAssets.GameManager;
import com.battleships.gui.gameAssets.grids.GridManager;
import com.battleships.logic.AI.AI;
import com.battleships.logic.AI.AIMedium;
import com.battleships.logic.OnlineGrid;
import com.battleships.logic.SaveFile;
import com.battleships.logic.SaveFileManager;
import org.joml.Vector2i;

/**
 * This class can handle the Strings the network receives from the opponent.
 * Both networks (client and server) extend this class to be able to process the Strings they receive.
 *
 * @author Tim Staudenmaier
 */
public abstract class Network implements NetworkInterface {

    /**
     * Constants for the actions the logic may need to execute.
     */
    private static final int NONE = -1, SHOOT = 0, CONFIRM = 1, SAVE = 2, LOAD = 3, SIZE = 4, CLOSE = 5;

    /**
     * String containing the word the network sends for a shoot command.
     */
    private String shoot = "shot ";
    /**
     * String containing the word the network sends to set the size (only client receives this message).
     */
    private String size = "size ";
    /**
     * String containing the word the network sends to confirm ship placement.
     */
    private String confirmed = "confirmed";
    /**
     * String containing the word the network sends to answer what a shot hit.
     */
    private String answer = "answer ";
    /**
     * String containing the word the network sends to save the game.
     */
    private String save = "save ";
    /**
     * String containing the word the network sends to load a game.
     */
    private String load = "load ";
    /**
     * String containing the word the network sends to do nothing.
     */
    private String pass = "pass";

    /**
     * {@code true} if the player has already confirmed his ship placement.
     */
    private boolean playerConfirm;
    /**
     * {@code true} if the opponent has already confirmed his ship placement.
     */
    private boolean opponentConfirm;

    /**
     * Row of a shoot command the network received.
     */
    private int row;
    /**
     * Column of a shoot command the network received.
     */
    private int col;
    /**
     * ID the network sent to load a game with.
     */
    private String ID;

    /**
     * Action the network needs to process. (One of the constants)
     */
    private int action = -1;

    /**
     * X index of the last shot this network sent to the opponent.
     */
    private int lastShotX;
    /**
     * Y index of the last shot this network sent to the opponent.
     */
    private int lastShotY;

    /**
     * Execute the last action this network received from the opponent.
     */
    public void execute() {
        switch (action) {
            case NONE:
                break;
            case SHOOT:
                action = NONE;
                System.out.println("\u001B[34m" + "Processing Shot " + GameManager.getGridManager().getCannonball().isFlying());
                if (GameManager.getGridManager().getCannonball().isFlying())
                    return;
                GameManager.shoot(GridManager.OPPONENTFIELD, new Vector2i(row + 1, col + 1));
                break;
            case CONFIRM:
                action = NONE;
                setOpponentConfirm();
                break;
            case SAVE:
                SaveFileManager.saveToFile(ID);
                action = CLOSE;
                execute();
                break;
            case LOAD:
                action = NONE;
                SaveFile file = SaveFileManager.loadFromFile(ID);
                if (file == null) {
                    setStringFunction(null);
                    break;
                }
                GameManager.getMainMenuManager().clearAll();
                SaveFileManager.loadSaveFile(file);
                GameManager.prepareGame();
                break;
            case SIZE:
                action = NONE;
                GameManager.getMainMenuManager().clearAll();
                GameManager.resizeGrid();
                GameManager.getLogic().advanceGamePhase();
                break;
            case CLOSE:
                action = NONE;
                closeConnection();
                GameManager.getLogic().setGameState(GameManager.MENU);
                GameManager.getMainMenuManager().backToMainMenu();
                GameManager.getSettings().setOnline(false);
                break;
        }
    }


    /**
     * Reads a string the network has gotten from the opponent.
     * Sets the action depending on what this game needs to do, to execute the received command.
     * @param text Text that was received through network
     */
    public void setStringFunction(String text) {
        if (text == null) {
            if(action != SAVE) {
                action = CLOSE;
            }
            return;
        }
        text = text.toLowerCase();
        if (text.contains(shoot)) {
            text = text.replace(shoot, "");
            String[] temp = text.split(" ");

            col = Integer.parseInt(temp[0]);
            row = Integer.parseInt(temp[1]);

            action = SHOOT;
        } else if (text.contains(size)) {
            text = text.replace(size, "");

            GameManager.getSettings().setSize(Integer.parseInt(text));
            GameManager.getSettings().setOnline(true);
            action = SIZE;
        } else if (text.contains(confirmed)) {
            text = text.replace(confirmed, "");
            if (text.equals(""))
                action = CONFIRM;
        } else if (text.contains(answer)) {
            text = text.replace(answer, "");
            if (Integer.parseInt(text) == 0) {
                GameManager.processShootAnswer(false);
                if (GameManager.getLogic().getOpponentGrid() instanceof OnlineGrid)
                    ((OnlineGrid) GameManager.getLogic().getOpponentGrid()).processShot(lastShotX, lastShotY, 0);
                GameManager.getNetwork().sendPass();
            }
            if (Integer.parseInt(text) == 1) {
                AI ai = GameManager.getLogic().getTurnHandler().getOnlineAI();
                if (ai instanceof AIMedium)
                    ((AIMedium) ai).processAnswer(new Vector2i(lastShotX, lastShotY));
                GameManager.processShootAnswer(true);
                if (GameManager.getLogic().getOpponentGrid() instanceof OnlineGrid)
                    ((OnlineGrid) GameManager.getLogic().getOpponentGrid()).processShot(lastShotX, lastShotY, 1);
            } else if (Integer.parseInt(text) == 2) {
                AI ai = GameManager.getLogic().getTurnHandler().getOnlineAI();
                if (ai instanceof AIMedium)
                    ((AIMedium) ai).processAnswer(new Vector2i(lastShotX, lastShotY));
                GameManager.processShootAnswer(true);
                if (GameManager.getLogic().getOpponentGrid() instanceof OnlineGrid)
                    ((OnlineGrid) GameManager.getLogic().getOpponentGrid()).processShot(lastShotX, lastShotY, 2);
            }

        } else if (text.contains(save)) {
            text = text.replace(save, "");
            ID = text;
            action = SAVE;
        } else if (text.contains(load)) {
            GameManager.getSettings().setOnline(true);
            text = text.replace(load, "");
            ID = text;
            action = LOAD;
        } else if (text.contains(pass)) {

        } else {
            System.err.println("Received faulty message from Network!");
        }
    }

    /**
     * Sets the value to true that indicates whether the opponent has confirmed the ship placement.
     * If both players have confirmed this processes the confirms and starts the shooting phase.
     * If the player has confirmed but the confirm message was sent because network was waiting form enemy confirm,
     * the confirm message gets sent.
     */
    public void setOpponentConfirm() {
        opponentConfirm = true;
        if (playerConfirm) {
            GameManager.getLogic().advanceGamePhase();
            opponentConfirm = false;
            playerConfirm = false;
        }
        if (GameManager.getNetwork().isConfirmCanBeSent()) {
            GameManager.getNetwork().sendConfirm();
        }
    }

    /**
     * Sets the value to true that indicates whether the player has confirmed the ship placement.
     * If both players have confirmed this processes the confirms and starts the shooting phase.
     */
    public void setPlayerConfirm() {
        playerConfirm = true;
        if (opponentConfirm) {
            GameManager.getLogic().advanceGamePhase();
            opponentConfirm = false;
            playerConfirm = false;
        }
    }

    /**
     * Sets the index of the last shot that this network has made.
     *
     * @param x X index of the last shot sent.
     * @param y Y index of the last shot sent.
     */
    public void setLastShot(int x, int y) {
        lastShotX = x;
        lastShotY = y;
    }

    /**
     * @return {@code true} if the player has already confirmed ship placement.
     */
    public boolean isPlayerConfirm() {
        return playerConfirm;
    }

    /**
     * @return {@code true} if the opponent has already confirmed ship placement.
     */
    public boolean isOpponentConfirm() {
        return opponentConfirm;
    }
}
