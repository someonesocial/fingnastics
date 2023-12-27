package com.xnorroid.fingnastics;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.Button;

import java.util.List;
import java.util.stream.Collectors;

public class Controller {
    private final MainActivity theMainActivity;

    private final List<Button> allButtons;
    private List<Button> searchedButtons;

    private int currentLevel = 1;

    public Controller(MainActivity pMainActivity, List pAllButtons) {
        theMainActivity = pMainActivity;
        allButtons = pAllButtons;
        SharedPreferences sharedPreferences = theMainActivity.getSharedPreferences("data", 0);
        if (sharedPreferences.contains("currentLevel")) {
            currentLevel = sharedPreferences.getInt("currentLevel", 1);
        }
    }

    public void nextLevel() {
        theMainActivity.setSharedPreferences(currentLevel);
        theMainActivity.hideButtons(allButtons);
        if (currentLevel >= 10) {
            theMainActivity.display("Congratulations, you've passed all levels!\nRestart the app and try again!");

            SharedPreferences sharedPreferences = theMainActivity.getSharedPreferences("data", 0);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putInt("currentLevel", 1);
            edit.apply();
        } else {
            theMainActivity.display("level: " + currentLevel);
            searchedButtons = allButtons.stream().limit(currentLevel).collect(Collectors.toList());
            theMainActivity.showButtons(searchedButtons);

        }
    }


    public void test() {
        for (int i = 0; i <= (theMainActivity.getButtons().size() - 1); i++) {

            if (theMainActivity.getButtons().get(i) == searchedButtons.get(i)) {
                theMainActivity.getButtons().get(i).setBackgroundColor(Color.parseColor("#808080"));
                theMainActivity.display(String.valueOf(theMainActivity.getButtons().size()) + " out of " + String.valueOf(searchedButtons.size()));
            } else {
                theMainActivity.getButtons().get(i).setBackgroundColor(Color.parseColor("#ff0000"));
                theMainActivity.display("wrong");
            }

        }

        if (theMainActivity.getButtons().equals(searchedButtons)) {

            theMainActivity.display("all correct");
            currentLevel++;
            nextLevel();

        }
    }
}