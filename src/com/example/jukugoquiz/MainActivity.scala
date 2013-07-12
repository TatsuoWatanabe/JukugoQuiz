package com.example.jukugoquiz

import android.os.Bundle
import android.app.Activity
import android.view.Menu
import android.widget._
import java.io.IOException

class MainActivity extends Activity {
  var wordList: List[Word]

  override protected def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    this.initialize
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater().inflate(R.menu.main, menu)
    true
  }

  /*
  def clickButton(view: android.view.View) {
    val edit01 = findViewById(R.id.editText1).asInstanceOf[EditText]
    val edit02 = findViewById(R.id.editText2).asInstanceOf[EditText]
    val msg = "Hi " + edit01.getText + edit02.getText
    //Toast.makeText(this, msg , Toast.LENGTH_LONG).show()
    this.showMessage(msg)
  }
  */

  private def initialize() {
    val tb = findViewById(R.id.table).asInstanceOf[TableLayout]
    val et = findViewById(R.id.editTextAnswer).asInstanceOf[EditText]
    try {
      loadData()
    } catch { case e: IOException =>
      showMessage(e.getLocalizedMessage)
    }

    showQuestion()
  }

  private def loadData() {
    import java.io._
    showMessage("loadData")
    try{
      val br = new BufferedReader(
        new InputStreamReader(getResources.getAssets.open("Jukugo2c_utf8.txt"))
      )
      val lines = Iterator.continually(br.readLine).takeWhile(_ != null).toList
      this.wordList = lines.map(_.split(",")).map(arr => new Word(arr(0), arr(1)))
    } catch { case e: IOException =>
      throw new IOException(e.getMessage.toString)
    }
  }

  private def showQuestion() {
    val et = findViewById(R.id.editTextAnswer).asInstanceOf[EditText]
    et.setText("")
    val candidateCount = 0
    val selectedWord = ""
    val selectedChar: Char = 'a'
    val preCandidate: Array[Word] = Array()
    val postCandidate: Array[Word] = Array()
    val rnd = new java.util.Random()
    showMessage("showQuestion")
    
    this.wordList
    
  }

  /*
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
        rnd.nextInt(Dictionary.size())).Kanji;
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
        TextViews[i].setText(Answers[i].Kanji.
          substring(startIndex, startIndex + 1));
      }
    }
    AnswerChar = selectedChar;
  }
   */

  def showMessage(s: String){
    val b = new android.app.AlertDialog.Builder(this)
    b.setMessage(s).setPositiveButton(android.R.string.ok, null).show()
  }


}
