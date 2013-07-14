package com.example.jukugoquiz

import android.os.Bundle
import android.app.Activity
import android.view.Menu
import android.widget._
import java.io.IOException
import java.util.Date
import scala.util.Random

class MainActivity extends Activity {
  var wordList: Array[Word] = Array()
  var Answers: scala.collection.mutable.ArrayBuffer[Word] = scala.collection.mutable.ArrayBuffer.fill(8)(null)
  var AnswerChar: Char = 0

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
  case class Watch (label: String) {
    private val start: Date = new Date 
    def log() = {
      android.util.Log.d(label, "Watch -- " + label + " : " + ((new Date).getTime - start.getTime).toString + " ms")
    }
  }
  /**
   * initialize
   */
  private def initialize() {
    val tb = findViewById(R.id.table).asInstanceOf[TableLayout]
    val et = findViewById(R.id.editTextAnswer).asInstanceOf[EditText]
    try {
      val watch = Watch("loadData")
      loadData()
      watch.log
      
    } catch { case e: IOException =>
      showMessage(e.getLocalizedMessage)
    }

    val watch = Watch("showQuestion")
    showQuestion()
    watch.log
    
  }

  /**
   * loadData
   */
  private def loadData() {
    import java.io._
    try{
      val br = new BufferedReader(
        new InputStreamReader(getResources.getAssets.open("Jukugo2c_utf8.txt"))
      )
      val lines = Iterator.continually(br.readLine).takeWhile(_ != null).toArray
      this.wordList = lines.map(_.split(",")).map(arr => new Word(arr(0), arr(1)))
    } catch { case e: IOException =>
      throw new IOException(e.getMessage.toString)
    }
  }

  /**
   * showQuestion
   */
  private def showQuestion() {
    import scala.collection.mutable.ArrayBuffer
    import scala.util.Random
    
    val et = findViewById(R.id.editTextAnswer).asInstanceOf[EditText]
    et.setText("")
    var candidateCount = 0
    val preCandidate: ArrayBuffer[Word] = ArrayBuffer()
    val postCandidate: ArrayBuffer[Word] = ArrayBuffer()

    while((preCandidate.size + postCandidate.size) < 4){
      val selectedWord = this.wordList((new Random).nextInt(this.wordList.size)).kanji
      val selectedChar = selectedWord.charAt((new Random).nextInt(2))
      
      preCandidate.clear
      postCandidate.clear
      AnswerChar = selectedChar
      
      for(w <- this.wordList){
        w.search(selectedChar) match {
          case SearchResult.Pre => preCandidate += w
          case SearchResult.Post => postCandidate += w
          case _ =>
        }
      }
    }// end while
    
    this.showMessage(AnswerChar.toString)
    
    Answers = ArrayBuffer.fill(8)(Word("●●", ""))
    //preCandidateからランダムに4件取り出してAnswers配列の前半に格納
    Random.shuffle(preCandidate).take(4).zipWithIndex.foreach{ case (w:Word , i: Int) =>
      Answers(i) = w
      val vId = getResources.getIdentifier("textView" + (i + 1).toString, "id", getPackageName())
      findViewById(vId).asInstanceOf[TextView].setText(w.char1.toString)
    }
    //postCandidateからランダムに4件取り出してAnswers配列の後半に格納
    Random.shuffle(postCandidate).take(4).zipWithIndex.foreach{ case (w:Word , i: Int) =>
      Answers(i + 4) = w
      val vId = getResources.getIdentifier("textView" + (i + 5).toString, "id", getPackageName())
      findViewById(vId).asInstanceOf[TextView].setText(w.char2.toString)
    }
  }

  /**
   * showMessage
   */
  def showMessage(s: String){
    val b = new android.app.AlertDialog.Builder(this)
    b.setMessage(s).setPositiveButton(android.R.string.ok, null).show()
  }


}
