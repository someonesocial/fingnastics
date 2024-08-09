package com.xnorroid.fingnastics;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    TextView middleText;


    private List<Button> buttons;
    private List<Button> allButtons;
    private static final int[] BUTTON_IDS = {
            R.id.buttonOne,
            R.id.buttonTwo,
            R.id.buttonThree,
            R.id.buttonFour,
            R.id.buttonFive,
            R.id.buttonSix,
            R.id.buttonSeven,
            R.id.buttonEight,
            R.id.buttonNine,
            R.id.buttonTen,
    };
    private Controller theController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        this.hideStatusBar();

        middleText = (TextView) findViewById(R.id.middleText);

        allButtons = new ArrayList<Button>();

        buttons = new ArrayList<Button>(BUTTON_IDS.length);

        for (int id : BUTTON_IDS) {
            Button button = (Button) findViewById(id);
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (!buttons.contains(button)) {
                            buttons.add(button);
                        }
                        theController.test();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        buttons.remove(button);
                        button.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
                        theController.test();
                    }
                    return true;
                }
            });
            allButtons.add(button);
        }
        //Check if layout is there
        allButtons.get(0).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                allButtons.get(0).getViewTreeObserver().removeOnGlobalLayoutListener(this);
                theController.nextLevel();
                // Now you can use the width/height
            }
        });

        theController = new Controller(this, allButtons);
    }


    public void hideStatusBar() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    public void showButtons(List<Button> pSearchedButtons) {
        for (Button button : pSearchedButtons) {
            button.setVisibility(View.INVISIBLE);
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getRealSize(size); //real size without notch
            float dx;
            float dy;
            Random R = new Random();
            do {
                dx = R.nextFloat() * (size.x - button.getWidth());  //minus width button
                dy = R.nextFloat() * (size.y - button.getHeight());  //minus height button

                button.setX(dx);
                button.setY(dy);
            } while (checkIfOverlapping(button, pSearchedButtons));
            button.setVisibility(View.VISIBLE);
        }
    }

    public boolean checkIfOverlapping(Button pButton, List<Button> pSearchedButtons) {
        if (isViewOverlapping(pButton, middleText)) {
            return true;
        }
        for (Button button : pSearchedButtons) {
            if (pButton != button) {
                if (isViewOverlapping(button, pButton)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isViewOverlapping(View firstView, View secondView) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        firstView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        firstView.getLocationOnScreen(firstPosition);
        secondView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        secondView.getLocationOnScreen(secondPosition);

        return firstPosition[0] < secondPosition[0] + secondView.getWidth()
                && firstPosition[0] + firstView.getWidth() > secondPosition[0]
                && firstPosition[1] < secondPosition[1] + secondView.getHeight()
                && firstPosition[1] + firstView.getHeight() > secondPosition[1];
    }


    public void hideButtons(List<Button> pButtons) {
        buttons.clear();
        for (Button button : pButtons) {
            button.setVisibility(View.INVISIBLE);
            button.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));
        }
    }

    public List<Button> getButtons() {
        return buttons;
    }


    public void display(String pDisplay) {
        middleText.setText(pDisplay);
        manageBlinkEffect();
    }

    private void manageBlinkEffect() {
        ObjectAnimator anim = ObjectAnimator.ofInt(middleText, "backgroundColor", Color.WHITE, ContextCompat.getColor(getApplicationContext(), R.color.primary),
                Color.WHITE);
        anim.setDuration(1500);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        anim.start();
    }

    public void setSharedPreferences(int pCurrentLevel) {
        SharedPreferences sharedPreferences = getSharedPreferences("data", 0);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("currentLevel", pCurrentLevel);
        edit.apply();
    }

}