package com.example.jukugoquiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.AssetManager;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
  TextView[] TextViews = new TextView[16];
  ArrayList<Word> Dictionary = new ArrayList<Word>();
  Word[] Answers = new Word[8]; 
  char AnswerChar;

  @Override
  protected void onCreate(
    Bundle savedInstanceState) {
    
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initialize();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  private void initialize() {
    GridLayout gl = (GridLayout) findViewById(
      R.id.gridMain);
    int cellSize = 120;
    float textSize = 60.0f;

    for (int i = 0; i < 16; i++) {
      TextView tv = new TextView(this);
      tv.setTextSize(textSize);

      switch (i) {
      case 8: case 15:
        tv.setText("↘");
        break;
      case 9: case 14:
        tv.setText("↓");
        break;
      case 10: case 13:
        tv.setText("↙");
        break;
      case 11: case 12:
        tv.setText("→");
        break;
      default:
        tv.setText(String.valueOf(i));
      }

      GridLayout.LayoutParams p = 
        new GridLayout.LayoutParams();
      p.width = cellSize;
      p.height = cellSize;

      switch (i) {
      case 0: case 1: case 2:
        p.columnSpec = GridLayout.spec(i * 2);
        p.rowSpec = GridLayout.spec(0);
        break;
      case 3:
        p.columnSpec = GridLayout.spec(0);
        p.rowSpec = GridLayout.spec(2);
        break;
      case 4:
        p.columnSpec = GridLayout.spec(4);
        p.rowSpec = GridLayout.spec(2);
        break;
      case 5: case 6: case 7:
        p.columnSpec = GridLayout.spec((i - 5) * 2);
        p.rowSpec = GridLayout.spec(4);
        break;
      case 8: case 9: case 10:
        p.columnSpec = GridLayout.spec(i - 7);
        p.rowSpec = GridLayout.spec(1);
        break;
      case 11:
        p.columnSpec = GridLayout.spec(1);
        p.rowSpec = GridLayout.spec(2);
        break;
      case 12:
        p.columnSpec = GridLayout.spec(3);
        p.rowSpec = GridLayout.spec(2);
        break;
      case 13: case 14: case 15:
        p.columnSpec = GridLayout.spec(i - 12);
        p.rowSpec = GridLayout.spec(3);
        break;
      }
      tv.setLayoutParams(p);
      TextViews[i] = tv;
      gl.addView(tv);
    }

    EditText et = (EditText) findViewById(
      R.id.editTextAnswer);
    et.setWidth(cellSize);
    et.setHeight(cellSize);
    et.setTextSize(textSize);

    try {
      LoadData();
    } catch (IOException e) {
      showMessage(e.getLocalizedMessage());
      return;
    }
    showQuestion();
  }

  private void LoadData() throws IOException {
    AssetManager as = getResources().getAssets();
    try {
      InputStream is = as.open("Jukugo2c_utf8.txt");
      BufferedReader br = new BufferedReader(
        new InputStreamReader(is));
      String line;
      while ((line = br.readLine()) != null) {
        String[] columns = line.split(",");
        Word w = new Word(columns[0], columns[1]);
        Dictionary.add(w);
      }
    } catch (IOException e) {
      throw new IOException(e.toString());
    }
  }

  public void showMessage(String s) {
    AlertDialog.Builder builder = 
      new AlertDialog.Builder(this);
    builder.setMessage(s);
    builder.setPositiveButton(
      android.R.string.ok, null);
    builder.show();
  }

  private void showQuestion() {
    EditText et = (EditText) findViewById(
      R.id.editTextAnswer);
    et.setText("");
    int candidateCount = 0;
    String selectedWord = "";
    char selectedChar = 0;
    ArrayList<Word> preCandidate = 
      new ArrayList<Word>();
    ArrayList<Word> postCandidate = 
      new ArrayList<Word>();
    Random rnd = new Random();
    while (candidateCount < 4) {
      selectedWord = Dictionary.get(
        rnd.nextInt(Dictionary.size())).kanji();
      selectedChar = selectedWord.charAt(
        rnd.nextInt(2));
      preCandidate.clear();
      postCandidate.clear();
      for (Word w : Dictionary) {
        SearchResult sr = w.Search(selectedChar);
        switch (sr) {
        case Pre:
          preCandidate.add(w);
          break;
        case Post:
          postCandidate.add(w);
          break;
        default:
        }
      }
      candidateCount = preCandidate.size() + 
        postCandidate.size();
    }

    for (int i = 0; i < 8; i++)
      Answers[i] = null;

    if (preCandidate.size() > 4) {
      for (int i = 0; i < 4; i++) {
        int index = rnd.nextInt(
          preCandidate.size());
        Answers[i] = preCandidate.get(index);
        preCandidate.remove(index);
      }
    } else {
      for (int i = 0; i < preCandidate.size(); i++)
        Answers[i] = preCandidate.get(i);
    }

    if (postCandidate.size() > 4) {
      for (int i = 0; i < 4; i++) {
        int index = rnd.nextInt(
          postCandidate.size());
        Answers[i + 4] = postCandidate.get(index);
        postCandidate.remove(index);
      }
    } else {
      for (int i = 0; i < postCandidate.size(); i++)
        Answers[i + 4] = postCandidate.get(i);
    }

    for (int i = 0; i < 8; i++) {
      if (Answers[i] == null)
        TextViews[i].setText("●");
      else {
        int startIndex = 1;
        if (i < 4)
          startIndex = 0;
        TextViews[i].setText(Answers[i].kanji().
          substring(startIndex, startIndex + 1));
      }
    }
    AnswerChar = selectedChar;
  }

  public void buttonSubmitClick(View v) {
    String s = "";
    EditText et = (EditText) findViewById(
    	R.id.editTextAnswer);
    if (et.getText().toString().equals(""))
      return;
    if (et.getText().charAt(0) == AnswerChar)
      s = "正解です！\n\n" + getAnswerString();
    else
      s = "違います";
    showMessage(s);
  }

  private String getAnswerString() {
    String s = "";
    for (int i = 0; i < 8; i++) {
      if (Answers[i] == null)
        continue;
      s += Answers[i].kanji() + "　（" + 
        Answers[i].yomi() + "）\n";
    }
    return s;
  }

  public void buttonGiveUpClick(View v) {
    EditText et = (EditText) findViewById(
      R.id.editTextAnswer);
    et.setText(Character.toString(AnswerChar));

    showMessage("答えは「" + 
      Character.toString(AnswerChar) + 
        "」です\n\n" + getAnswerString());
  }

  public void buttonNextClick(View v) {
    showQuestion();
  }
}
