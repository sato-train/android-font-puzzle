package com.sato_lab.game.unlock;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameActivity extends ActionBarActivity {

    Map<Integer, Integer> btnIdFaceNumMap = new HashMap();
    List<Button> pairList = new ArrayList(1);
    Boolean isFinished = false;
    Chronometer mChronometer = null;
    long mLastStopTime = 0;
    List<String> fontNameList = null;
    List<Integer> shuffleList = null;
    Handler booHandler = null;

    public void showToast(CharSequence text) {
        Toast ts = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        ts.setGravity(Gravity.CENTER, 0, 0);
        ts.show();
    }

    private View.OnClickListener clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (isFinished) {
                showToast("The Game has been completed!");
                return;
            }

            Button btn = (Button) v;

            Integer faceNum = btnIdFaceNumMap.get(btn.getId());

            System.out.println("click btn face num = " + faceNum);

            if (faceNum < 0) {
                //already open
                return;
            }

            btn.setText(Html.fromHtml(fontNameList.get(faceNum)));

            if (pairList.isEmpty()) {

                //1st Button clicked
                pairList.add(0, btn);
                btn.setOnClickListener(null);

                System.out.println("empty list");

            } else {

                final Button firstBtn = pairList.get(0);
                final Button secondBtn = btn;

                secondBtn.setOnClickListener(null);

                //2nd Button clicked
                if (firstBtn.getText().equals(secondBtn.getText())) {

                    //bingo!
                    btnIdFaceNumMap.put(firstBtn.getId(), -1);
                    btnIdFaceNumMap.put(secondBtn.getId(), -1);

                    Boolean isCompleted = true;
                    for (Integer statNum : btnIdFaceNumMap.values()) {
                        if (statNum >= 0) {
                            isCompleted = false;
                        }
                    }
                    if (isCompleted) {
                        mChronometer.stop();
                        showToast("Complete!!");
                        return;
                    }

                    System.out.println("match!");

                } else {

                    System.out.println("boo!");

                    //boo boo
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            firstBtn.setText(" ");
                            firstBtn.setOnClickListener(clicked);
                            secondBtn.setText(" ");
                            secondBtn.setOnClickListener(clicked);
                        }
                    }, 500);
                }

                pairList.clear();
            }
        }
    };

    private void initGame() {

        int MAX_ROW = 4;
        int MAX_COL = 3;
        int MAX_SEQ = (MAX_ROW * MAX_COL) / 2;

        shuffleList = new ArrayList();

        for (int i = 0; i < MAX_ROW; i++) {
            for (int j = 0; j < MAX_COL; j++) {
                Integer faceNum = (i * MAX_COL + j) % MAX_SEQ + 1;
                shuffleList.add(faceNum);
            }
        }
        Collections.shuffle(shuffleList);

        fontNameList = CsvUtil.loadFile(this, "cheatsheet.csv");
        System.out.println("fontNameList size = " + fontNameList.size());
        Collections.shuffle(fontNameList);

        GridLayout gridLayout = (GridLayout) findViewById(R.id.dynamicGridLayout);
        gridLayout.setColumnCount(MAX_COL);
        gridLayout.setRowCount(MAX_ROW);

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");


        btnIdFaceNumMap.clear();

        for (int i = 0; i < MAX_ROW; i++) {

            for (int j = 0; j < MAX_COL; j++) {

                Button btn = new Button(this);
                btn.setTypeface(font);
                btn.setOnClickListener(clicked);
                btn.setId(i * MAX_COL + j);
                btn.setText(" ");

                btnIdFaceNumMap.put(btn.getId(), shuffleList.get(i * MAX_COL + j));

                GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                        GridLayout.spec(i), GridLayout.spec(j));

                btn.setLayoutParams(params);

                gridLayout.addView(btn);
            }
        }

        System.out.println("btnIdFaceNumMap = " + btnIdFaceNumMap);

        isFinished = false;
        pairList.clear();

        mLastStopTime = 0;
        chronoStart();
    }

    private void chronoStart() {
        // on first start
        if (mLastStopTime == 0)
            mChronometer.setBase(SystemClock.elapsedRealtime());
            // on resume after pause
        else {
            long intervalOnPause = (SystemClock.elapsedRealtime() - mLastStopTime);
            mChronometer.setBase(mChronometer.getBase() + intervalOnPause);
        }

        mChronometer.start();
    }

    private void chronoPause() {

        mChronometer.stop();

        mLastStopTime = SystemClock.elapsedRealtime();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mChronometer = (Chronometer) findViewById(R.id.chronometer);

        Button resetBtn = (Button) findViewById(R.id.resetButton);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initGame();
            }
        });

        initGame();

    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        showToast("on pause");
        chronoPause();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        showToast("on resume");
        chronoStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up Button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}