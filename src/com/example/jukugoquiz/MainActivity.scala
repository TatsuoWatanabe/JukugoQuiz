package com.example.jukugoquiz

import android.os.Bundle
import android.app.Activity
import android.view.Menu
import android.view.View
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

 /**
  * buttonSubmitClick
  */
  def buttonSubmitClick(v: View) {
    val et = findViewById(R.id.editTextAnswer).asInstanceOf[EditText]
    et.getText.toString match {
      case "" =>
      case s: String if s.charAt(0) == AnswerChar => showMessage("正解です!\n\n" + this.getAnswerString)
      case _ => showMessage("違います!")
    }
  }

  /**
   * buttonGiveUpClick
   */
  def buttonGiveUpClick(v: View) {
    val et = findViewById(R.id.editTextAnswer).asInstanceOf[EditText]
    et.setText(AnswerChar.toString);
    showMessage("答えは「" + AnswerChar.toString + "」です\n\n" + this.getAnswerString);
  }

  /**
   * buttonNextClick
   */
  def buttonNextClick(v: View) {
    showQuestion();
  }

  /**
   * case class Watch
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

    showQuestion()
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

    val watch = Watch("showQuestion")

    val et = findViewById(R.id.editTextAnswer).asInstanceOf[EditText]
    val selectedWord = this.wordList((new Random).nextInt(this.wordList.size)).kanji
    val selectedChar = selectedWord.charAt((new Random).nextInt(2))
    val wordListFilterd: Array[Word] = this.wordList.filter(_.kanji.indexOf(selectedChar) != -1)
    val preCandidate: List[Word] = wordListFilterd.filter(_.kanji.indexOf(selectedChar) == 1).toList
    val postCandidate: List[Word] = wordListFilterd.filter(_.kanji.indexOf(selectedChar) == 0).toList
    AnswerChar = selectedChar

    et.setText("")

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

    android.util.Log.d("Watch", "Watch -- " + Answers.toString)
    watch.log
  }

  /**
   * getAnswerString
   */
  private def getAnswerString() = {
    Answers.map(a => a.kanji + "  (" + a.yomi + ")").mkString("\n")
  }

  /**
   * showMessage
   */
  private def showMessage(s: String){
    val b = new android.app.AlertDialog.Builder(this)
    b.setMessage(s).setPositiveButton(android.R.string.ok, null).show()
  }

  object Answer {
    import scala.collection.mutable.ArrayBuffer
    val answersPre  = ArrayBuffer[Word]()
    val answersPost = ArrayBuffer[Word]()
    var answerChar = 0

    def initialize {
      List(answersPre, answersPost).foreach { answers =>
        answers.clear
        answers ++= ArrayBuffer.fill(4)(Word("●●", ""))
      }
    }

    def setAnswers(preCandidate: List[Word], postCandidate: List[Word], AnswerChar: Char){
      //preCandidateからランダムに4件取り出し
      Random.shuffle(preCandidate).take(4).zipWithIndex.foreach{ case (w:Word , i: Int) => answersPre(i) = w }
      //postCandidateからランダムに4件取り出し
      Random.shuffle(postCandidate).take(4).zipWithIndex.foreach{ case (w:Word , i: Int) => answersPost(i) = w }
      this.answerChar = AnswerChar
    }
  }
}
