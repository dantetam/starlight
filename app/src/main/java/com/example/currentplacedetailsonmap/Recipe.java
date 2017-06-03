package com.example.currentplacedetailsonmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dante on 6/2/2017.
 */
public class Recipe implements Serializable {

    public List<Item> input;
    public List<Item> output;

    public Recipe() {
        input = new ArrayList<>();
        output = new ArrayList<>();
    }

    public static Recipe newRecipeOnlyInput(List<Item> input) {
        Recipe recipe = new Recipe();
        recipe.input = input;
        recipe.output = new ArrayList<>();
        return recipe;
    }

    public static Recipe newRecipeOnlyOutput(List<Item> output) {
        Recipe recipe = new Recipe();
        recipe.input = new ArrayList<>();
        recipe.output = output;
        return recipe;
    }

    public Recipe(List<Item> input, List<Item> output) {
        this.input = input;
        this.output = output;
    }

    public String toString() {
        String inputString = "", outputString = "";
        for (Item item: input) {
            inputString += item.toString() + " ";
        }
        for (Item item: output) {
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
