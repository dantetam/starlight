package com.example.currentplacedetailsonmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/2/2017.
 */
public class Recipe implements Serializable {

    public Inventory input;
    public Inventory output;

    public Recipe() {
        input = new Inventory();
        output = new Inventory();
    }

    public static Recipe newRecipeOnlyInput(Inventory input) {
        Recipe recipe = new Recipe();
        recipe.input = input;
        recipe.output = new Inventory();
        return recipe;
    }

    public static Recipe newRecipeOnlyOutput(Inventory output) {
        Recipe recipe = new Recipe();
        recipe.input = new Inventory();
        recipe.output = output;
        return recipe;
    }

    public Recipe(Inventory input, Inventory output) {
        this.input = input;
        this.output = output;
    }

    public String toString() {
        String inputString = "", outputString = "";
        for (Item item: input.getItems()) {
            inputString += item.toString() + " ";
        }
        for (Item item: output.getItems()) {
            outputString += item.toString() + " ";
        }
        inputString = inputString.trim();
        outputString = outputString.trim();
        if (input.size() == 0) {
            return outputString;
        }
        else if (output.size() == 0) {
            return inputString;
        }
        else {
            return inputString + " > " + outputString;
        }
    }

}
