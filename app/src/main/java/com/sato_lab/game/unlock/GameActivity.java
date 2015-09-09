package com.sato_lab.game.unlock;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
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

            Button btn = (Button) findViewById(v.getId());

            Integer faceNum = btnIdFaceNumMap.get(btn.getId());

            if (faceNum == 0) {
                //already open
                return;
            }

            btn.setText(String.valueOf(faceNum));

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
                    btnIdFaceNumMap.put(firstBtn.getId(), 0);
                    btnIdFaceNumMap.put(secondBtn.getId(), 0);

                    Integer total = 0;
                    for (Integer statNum : btnIdFaceNumMap.values()) {
                        total += statNum;
                    }
                    if (total == 0) {
                        mChronometer.stop();
                        showToast("Complete!!");
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
                    }, 1000);
                }

                pairList.clear();
            }
        }
    };

    private void initGame() {

        int MAX_ROW = 6;
        int MAX_COL = 4;
        int MAX_SEQ = (MAX_ROW * MAX_COL) / 2;

/*
        TableLayout tableLayout = (TableLayout) findViewById(R.id.dynamicTableLayout);
        TableRow tableRow1 = new TableRow(this);

        Button btn1 = new Button(this);
        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        btn1.setTypeface(font);
        btn1.setText(Html.fromHtml("&#xf082;"));

        tableRow1.addView(btn1);
        tableLayout.addView(tableRow1);
*/

        List<Integer> list = new ArrayList();

        for (int i = 0; i < MAX_ROW; i++) {
            for (int j = 0; j < MAX_COL; j++) {
                Integer faceNum = (i * MAX_COL + j) % MAX_SEQ + 1;
                list.add(faceNum);
            }
        }
        Collections.shuffle(list);


        Resources res = getResources();
        btnIdFaceNumMap.clear();

        for (int i = 0; i < MAX_ROW; i++) {
            for (int j = 0; j < MAX_COL; j++) {

                int strId = res.getIdentifier(String.format("ic_custom%d%d", i + 1, j + 1), "id", getPackageName());
                CustomFontButton btn = (CustomFontButton) findViewById(strId);
                btn.setOnClickListener(clicked);
                btn.setText(" ");
                btnIdFaceNumMap.put(btn.getId(), list.get(i * MAX_COL + j));
            }
        }

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