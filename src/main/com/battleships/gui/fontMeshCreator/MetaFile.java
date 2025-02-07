package com.battleships.gui.fontMeshCreator;

import com.battleships.gui.window.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains all the necessary information of a .fnt file so a {@link GUIText} can be rendered using that .fnt file.
 * All .fnt files need to be distance font files for this implementation.
 *
 * @author Tim Staudenmaier
 */
public class MetaFile {

    /**
     * Index at which the padding value of the padding above a character is stored.
     */
    private static final int PAD_TOP = 0;
    /**
     * Index at which the padding value of the padding to the left of a character is stored.
     */
    private static final int PAD_LEFT = 1;
    /**
     * Index at which the padding value of the padding below a character is stored.
     */
    private static final int PAD_BOTTOM = 2;
    /**
     * Index at which the padding value of the padding to the right of a character is stored.
     */
    private static final int PAD_RIGHT = 3;

    /**
     * Padding value each .fnt file should have that is used with this class.
     */
    private static final int DESIRED_PADDING = 8;

    /**
     * Character with which all characters are separated in the .fnt file.
     */
    private static final String SPLITTER = " ";
    /**
     * Character with which all numbers are separated in the .fnt file.
     */
    private static final String NUMBER_SEPARATOR = ",";

    /**
     * Aspect ratio of the screen the text should be rendered on.
     */
    private double aspectRatio;

    /**
     * How big each pixel of the font needs to be on the screen vertically after rendering.
     */
    private double verticalPerPixelSize;
    /**
     * How big each pixel of the font needs to be on the screen horizontally after rendering.
     */
    private double horizontalPerPixelSize;
    /**
     * Width of a space character.
     */
    private double spaceWidth;
    /**
     * Actual padding values of the .fnt file.
     */
    private int[] padding;
    /**
     * Vertical padding between two characters.
     */
    private int paddingWidth;
    /**
     * Horizontal padding between two characters.
     */
    private int paddingHeight;

    /**
     * Map that contains all ASCII-Values of all characters, mapping them to their corresponding character.
     */
    private Map<Integer, Character> metaData = new HashMap<>();

    /**
     * Reader to read file.
     */
    private BufferedReader reader;
    /**
     * Map containing all the information read by the reader.
     * First string is identifier of the value, second is the actual value.
     */
    private Map<String, String> values = new HashMap<>();

    /**
     * open a font file to read it, read all needed data from it and save the read data into this Object.
     *
     * @param file font file
     */
    protected MetaFile(String file) {
        this.aspectRatio = (double) WindowManager.getWidth() / (double) WindowManager.getHeight();
        try {
            reader = new BufferedReader(new InputStreamReader(MetaFile.class.getResourceAsStream("/com/battleships/gui/res/textures/font/" + file + ".fnt")));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't read font meta file!");
        }
        loadPaddingData();
        loadLineSizes();
        int imageWidth = getValueOfVariable("scaleW");
        loadCharacterData(imageWidth);
        close();
    }

    /**
     * @return the width of a space character
     */
    protected double getSpaceWidth() {
        return spaceWidth;
    }

    /**
     * @param ascii the ascii value of the character
     * @return the character for the ascii value
     */

    protected Character getCharacter(int ascii) {
        return metaData.get(ascii);
    }

    /**
     * read the next line and store the variable names and their values in the values hashMap
     *
     * @return {@code true} if end of the file hasn't been reached
     */
    private boolean processNextLine() {
        values.clear();
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (line == null) {
            return false;
        }
        for (String part : line.split(SPLITTER)) {
            String[] valuePairs = part.split("=");
            if (valuePairs.length == 2) {
                values.put(valuePairs[0], valuePairs[1]);
            }
        }
        return true;
    }

    /**
     * Get the {@code int} value of a variable with a certain name in the current line
     *
     * @param variable the name of the variable
     * @return The {@code int} value of the variable
     */
    private int getValueOfVariable(String variable) {
        return Integer.parseInt(values.get(variable));
    }

    /**
     * Get array of ints from a variable in the current line
     *
     * @param variable the name of the variable
     * @return The int array of values from the variable, split at commas
     */
    private int[] getValuesOfVariable(String variable) {
        String[] numbers = values.get(variable).split(NUMBER_SEPARATOR);
        int[] actualValues = new int[numbers.length];
        for (int i = 0; i < actualValues.length; i++) {
            actualValues[i] = Integer.parseInt(numbers[i]);
        }
        return actualValues;
    }

    /**
     * Closes the font file after reading
     */
    private void close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the data about how much padding (empty space) is used around each character in
     * the texture atlas
     */
    private void loadPaddingData() {
        processNextLine();
        this.padding = getValuesOfVariable("padding");
        this.paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
        this.paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
    }

    /**
     * Loads information about line height of this font in pixels, and uses
     * this as a way to find the conversion rate between pixels in the texture
     * atlas and screen-space
     */
    private void loadLineSizes() {
        processNextLine();
        int lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight;
        verticalPerPixelSize = TextMeshCreator.LINE_HEIGHT / (double) lineHeightPixels;
        horizontalPerPixelSize = verticalPerPixelSize / aspectRatio;
    }

    /**
     * Loads in data about each character and stores the data in the {@link Character} class and in
     * the metaData hashMap with the characters ascii value
     *
     * @param imageWidth the width of the texture atlas in pixels
     */
    private void loadCharacterData(int imageWidth) {
        //ship first 2 info lines
        processNextLine();
        processNextLine();
        while (processNextLine()) {
            Character c = loadCharacter(imageWidth);
            if (c != null) {
                metaData.put(c.getId(), c);
            }
        }
    }

    /**
     * Load all the data from one character in the texture atlas and convert
     * it all from 'pixels' to 'screen-space' before storing. Also remove padding
     * between characters
     *
     * @param imageSize size of the texture atlas in pixels
     * @return The data about the character as {@link Character}
     */
    private Character loadCharacter(int imageSize) {
        int id = getValueOfVariable("id");
        if (id == TextMeshCreator.SPACE_ASCII) {
            this.spaceWidth = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
            return null;
        }
        double xTex = ((double) getValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize;
        double yTex = ((double) getValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING)) / imageSize;
        int width = getValueOfVariable("width") - (paddingWidth - (2 * DESIRED_PADDING));
        int height = getValueOfVariable("height") - ((paddingHeight) - (2 * DESIRED_PADDING));
        double quadWidth = width * horizontalPerPixelSize;
        double quadHeight = height * verticalPerPixelSize;
        double xTexSize = (double) width / imageSize;
        double yTexSize = (double) height / imageSize;
        double xOff = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING) * horizontalPerPixelSize;
        double yOff = (getValueOfVariable("yoffset") + (padding[PAD_TOP] - DESIRED_PADDING)) * verticalPerPixelSize;
        double xAdvance = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
        return new Character(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance);
    }
}
