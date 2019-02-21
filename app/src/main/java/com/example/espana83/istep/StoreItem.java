package com.example.espana83.istep;

import java.io.Serializable;

/**
 * Created by Lance on 09/05/2017.
 */

public class StoreItem implements Serializable{

    private String name;
    private int price;
    private String description;
    private boolean purchased;
    private boolean recipe;
    private String recipeText;

    public StoreItem()
    {

    }

    public String getName(){return name;}

    public void setName(String name){this.name = name;}

    public int getPrice(){return price;}

    public void setPrice(int price){this.price = price;}

    public String getDescription(){return description;}

    public void setDescription(String description){this.description = description;}

    public boolean getPurchased(){return purchased;}

    public void setPurchased(boolean purchased){this.purchased = purchased;}

    public String getRecipeText(){return recipeText;}

    public void setRecipeText(String recipeText){this.recipeText = recipeText;}

    public boolean getRecipe(){return recipe;}

    public void setRecipe(boolean recipe){this.recipe = recipe;}



}
