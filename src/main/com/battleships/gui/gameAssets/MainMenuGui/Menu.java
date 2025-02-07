package com.battleships.gui.gameAssets.MainMenuGui;

import com.battleships.gui.fontMeshCreator.FontType;
import com.battleships.gui.fontMeshCreator.GUIText;
import com.battleships.gui.fontRendering.TextMaster;
import com.battleships.gui.gameAssets.GameManager;
import com.battleships.gui.guis.GuiClickCallback;
import com.battleships.gui.guis.GuiManager;
import com.battleships.gui.guis.GuiTexture;
import com.battleships.gui.renderingEngine.Loader;
import com.battleships.logic.SaveFile;
import com.battleships.logic.SaveFileManager;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains all functions and constants for the menus
 *
 * @author Sascha Mößle
 */
public abstract class Menu extends GuiClickCallback {
    /**
     * Main button texture used in all menus
     */
    protected static int buttonTexture;
    /**
     * Texture behind the Buttons in the background
     */
    protected static int scrollBackground;
    /**
     * List containing all {@link GuiTexture}s behind the buttons
     */
    protected static List<GuiTexture> backgrounds = new ArrayList<>();
    /**
     * Texture above all buttons
     */
    protected static int icon;
    /**
     * Standard size for all buttons
     */
    protected Vector2f buttonSize = new Vector2f(0.16f, 0.12f);
    /**
     * Standard position to start for all buttons
     */
    protected Vector2f standardButtonPos = new Vector2f(0.5f, 0.38f);
    /**
     * Standard gap between all buttons
     */
    protected Float buttonGap = 0.15f;
    /**
     * GuiManager that handles guis with click functions.
     */
    protected GuiManager guiManager;
    /**
     * Loader for loading models and textures.
     */
    protected Loader loader;
    /**
     * Main font used for all labels on buttons
     */
    protected FontType font;
    /**
     * Main font size for {@link GUIText}
     */
    protected float fontSize = 2.5f;
    /**
     * Main outline color used for the {@link GUIText}
     */
    protected Vector3f outlineColor = new Vector3f(0.63f, 0.63f, 0.63f);
    /**
     * List containing all {@link GuiTexture}s on the screen.
     */
    protected List<GuiTexture> buttons = new ArrayList<>();
    /**
     * List containing all {@link GUIText}s on the screen.
     */
    protected List<GUIText> guiTexts = new ArrayList<>();
    /**
     * Used to determine which button was the last one that was clicked.
     */
    protected int buttonClicked;
    /**
     * {@code true} if the user made an input through a {@link TinyFileDialogs} that is not yet processed.
     */
    protected boolean userInputMade;
    /**
     * Last inout from the user made  through a {@link TinyFileDialogs}.
     */
    protected String userInput;
    /**
     * Name of the file that needs to be loaded.
     */
    protected static String fileName;
    /**
     * {@code true} if a file was picked that now needs to be loaded.
     * {@code false} else and after loading is done.
     */
    protected boolean filePicked;
    /**
     * open the file explorer to chose the save file.
     */
    protected JFileChooser fc;

    /**
     * Loads all {@link GuiTexture} needed and the font for the {@link GUIText}
     *
     * @param guiManager GuiManager that should handle the click function of these guis.
     * @param loader     Loader needed to load textures
     */
    public Menu(GuiManager guiManager, Loader loader) {
        this.guiManager = guiManager;
        this.loader = loader;
        fc = new JFileChooser();
        if (GameManager.getPirateFont() == null)
            this.font = new FontType(loader.loadFontTexture("font/pirate.png"), "pirate");
        else
            this.font = GameManager.getPirateFont();
        if (buttonTexture == 0)
            buttonTexture = loader.loadTexture("WoodButton.png");
        if (scrollBackground == 0)
            scrollBackground = loader.loadTexture("scroll.png");
        if (icon == 0)
            icon = loader.loadTexture("StartIcon.png");
        if (backgrounds.size() == 0)
            addBackground();
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * @return the file name of from the selected file
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * creates the amount of {@link GuiTexture} needed.
     *
     * @param anzahl how many button textures should be created
     */
    protected void CreateButtonTextures(int anzahl) {
        buttons.add(new GuiTexture(buttonTexture, standardButtonPos, buttonSize));
        for (int i = 0; i < anzahl - 1; i++) {
            buttons.add(new GuiTexture(buttonTexture, new Vector2f(buttons.get(i).getPositions().x, buttons.get(i).getPositions().y + buttonGap), buttonSize));
        }
        GameManager.getGuis().addAll(buttons);
    }

    /**
     * adds a {@link GuiTexture} behind the buttons as background
     */
    protected void addBackground() {
        backgrounds.add(new GuiTexture(scrollBackground, new Vector2f(0.5f, 0.5f), new Vector2f(0.375f, 1f)));
        backgrounds.add(new GuiTexture(icon, new Vector2f(0.5f, 0.175f), new Vector2f(0.3f, 0.3f)));
        GameManager.getGuis().addAll(backgrounds);
    }

    /**
     * clears all {@link GuiTexture} that are no buttons at the game Begin
     */
    protected void cleaBackgournd() {
        GameManager.getGuis().removeAll(backgrounds);
    }

    /**
     * adds all labels the the {@link TextMaster} to render
     */
    protected void CreateTextLabels() {
        SetTextColor();
        for (GUIText gui : guiTexts) {
            TextMaster.addText(gui);
        }
    }

    /**
     * sets the color for the {@link GUIText}
     */
    protected void SetTextColor() {
        for (GUIText gui : guiTexts) {
            gui.setColor(0.25f, 0.25f, 0.25f);
        }
    }

    /**
     * makes the {@link GuiTexture} clickable
     */
    protected void createClickable() {
        for (GuiTexture i : buttons) {
            guiManager.createClickableGui(i, () -> this);
        }
    }

    /**
     * removes the {@link GuiTexture} and {@link GUIText} from the screen
     */
    protected void clearMenu() {
        for (GUIText text : guiTexts) {
            TextMaster.removeText(text);
            text.remove();
        }
        for (GuiTexture gui : buttons) {
            guiManager.removeClickableGui(gui);
        }
        GameManager.getGuis().removeAll(buttons);

    }

    /**
     * Opens the dialog to select a file
     */
    protected void openLoadGameDialog() {
        try {
            FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
            File filepath = new File(SaveFileManager.getJarPath() + "/SaveFiles/");
            fc.setCurrentDirectory(filepath);
            fc.setFileFilter(xmlfilter);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        fc.setDialogTitle("Select save file");
        new Thread(new SaveFilePicker()).start();
    }

    /**
     * Loads the selected file into the game
     *
     * @return {@code true} when the file could be loaded {@code false} when the file could not be loaded
     */
    public boolean processLoadedFile() {
        filePicked = false;
        if (fileName != null) {
            String filename = fileName.replace(".xml", "");
            SaveFile saveFile = SaveFileManager.loadFromFile(filename);
            if (saveFile == null) {
                new Thread(new ErrorMessage("Error loading the file", "Loading Error")).start();
                return false;
            } else {
                clearMenu();
                cleaBackgournd();

                if (GameManager.getSettings().isOnline()) {
                    GameManager.getNetwork().start(true, null);
                    MainMenuManager.setMenu(new WaitingConnection(guiManager, loader, saveFile));
                } else {
                    GameManager.prepareGame();
                    SaveFileManager.loadSaveFile(saveFile);
                }

                return true;
            }
        } else {
            return false;
        }

    }

    /**
     * @return {@code true} if there is a unprocessed user input that needs to be processed.
     */
    public boolean isUserInputMade() {
        return userInputMade;
    }

    /**
     * @return if a file was picked
     */
    public boolean isFilePicked() {
        return filePicked;
    }

    /**
     * Tests if the click was on either of the {@link GuiTexture}.
     *
     * @param gui The gui to test for if the click was on it.
     * @param x   xPos of the click (left of screen = 0, right of screen = 1)
     * @param y   yPos of the click (top of screen = 0, bottom of screen = 1)
     * @return {@code true} if the click was on either of the{@link GuiTexture}, {@code false} else.
     */
    @Override
    protected boolean isClickOnGui(GuiTexture gui, double x, double y) {
        for (int i = 0; i < buttons.size(); i++) {
            if (super.isClickOnGui(buttons.get(i), x, y)) {
                buttonClicked = i;
                return true;
            }
        }
        return false;
    }

    /**
     * Clears all {@link GuiTexture}, {@link GUIText} from the rendered scene and resets the settings for the game
     */
    public void clearAllMenuElements() {
        ESCMenu.setActive(false);
        if (GameManager.getShipCounter() != null)
            GameManager.getShipCounter().hide();
        if (GameManager.getShipSelector() != null)
            GameManager.getShipSelector().hide();
        cleaBackgournd();
        clearMenu();
    }

    /**
     * Class for handling file choosing in separate thread.
     */
    protected class SaveFilePicker implements Runnable {
        /**
         * Displays the file picker
         */
        @Override
        public void run() {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
            int result = fc.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION){
                fileName = fc.getName(fc.getSelectedFile());
                filePicked = true;
            }
            if (result == JFileChooser.CANCEL_OPTION)
                filePicked=false;

        }
    }

    /**
     * Class to display an error message window in separate thread.
     */
    protected class ErrorMessage implements Runnable {
        String message, title;

        /**
         * Create a new Error Message.
         * @param title Title of the error window.
         * @param message Message the window has.
         */
        public ErrorMessage(String message, String title) {
            this.message = message;
            this.title = title;
        }

        /**
         * Displays the window.
         */
        @Override
        public void run() {
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Class for handling text input window in separate thread.
     */
    protected class TextInput implements Runnable {
        /**
         * Title and message of the text inout dialog.
         */
        private String title, message;

        /**
         * Create a new input window
         *
         * @param title   Title of the window
         * @param message Message of the window
         */
        public TextInput(String title, String message) {
            this.title = title;
            this.message = message;
        }

        /**
         * Open the window so user can enter text.
         */
        @Override
        public void run() {
            userInput = TinyFileDialogs.tinyfd_inputBox(title, message, "");
            userInputMade = true;
        }
    }
}
