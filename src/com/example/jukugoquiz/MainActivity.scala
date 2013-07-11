package com.example.jukugoquiz

import android.os.Bundle
import android.app.Activity
import android.view.Menu
import android.widget._
import java.io.IOException

class MainActivity extends Activity {

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
      val wordList = lines.map(_.split(",")).map(arr => new Word(arr(0), arr(1)))
    }
  }

  private def showQuestion() {
    showMessage("showQuestion")
  }

  /*
  private void initialize() {
    try {
      LoadData();
    } catch (IOException e) {
      showMessage(e.getLocalizedMessage());
      return;
    }
    showQuestion();
  }
   */
  /*
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
   */

  def showMessage(s: String){
    val b = new android.app.AlertDialog.Builder(this)
    b.setMessage(s).setPositiveButton(android.R.string.ok, null).show()
  }


}
