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
  def buttonSubmitClick(v: View) = findViewById(R.id.editTextAnswer).asInstanceOf[EditText].getText.toString match {
      case "" =>
      case s: String if s.charAt(0) == Answer.getAnswerChar => showMessage("正解です!\n\n" + Answer.getAnswersString)
      case _ => showMessage("違います!")
  }

  /**
   * buttonGiveUpClick
   */
  def buttonGiveUpClick(v: View) {
    findViewById(R.id.editTextAnswer).asInstanceOf[EditText].setText(Answer.getAnswerChar.toString);
    showMessage("答えは「" + Answer.getAnswerChar.toString + "」です。\n\n" + Answer.getAnswersString);
  }

  /**
   * buttonNextClick
   */
  def buttonNextClick(v: View) = showQuestion()

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
    try{
      val br = new java.io.BufferedReader(
        new java.io.InputStreamReader(getResources.getAssets.open("Jukugo2c_utf8.txt"))
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
    val watch = Watch("showQuestion")
    val selectedWord = this.wordList((new Random).nextInt(this.wordList.size)).kanji
    val selectedChar = selectedWord.charAt((new Random).nextInt(2))
    val wordListFilterd: Array[Word] = this.wordList.filter(_.kanji.indexOf(selectedChar) != -1)
    val preCandidate: List[Word] = wordListFilterd.filter(_.kanji.indexOf(selectedChar) == 1).toList
    val postCandidate: List[Word] = wordListFilterd.filter(_.kanji.indexOf(selectedChar) == 0).toList
    
    Answer.setAnswers(preCandidate, postCandidate, selectedChar)
    Answer.getAnswers.zipWithIndex.foreach { case (w:Word , i: Int) =>
      val vId = getResources.getIdentifier("textView" + (i + 1).toString, "id", getPackageName())
      findViewById(vId).asInstanceOf[TextView].setText(w.answerIndexToChar(i).toString)
    }

    findViewById(R.id.editTextAnswer).asInstanceOf[EditText].setText("")
    watch.log
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
    private var answerChar: Char = 0

    def setAnswers(preCandidate: List[Word], postCandidate: List[Word], AnswerChar: Char){
      this.initialize
      //preCandidate・postCandidateからランダムに4件づつ取り出し
      Random.shuffle(preCandidate).take(4).zipWithIndex.foreach{ case (w:Word , i: Int) => answersPre(i) = w }
      Random.shuffle(postCandidate).take(4).zipWithIndex.foreach{ case (w:Word , i: Int) => answersPost(i) = w }
      this.answerChar = AnswerChar
    }
    
    def getAnswerChar = this.answerChar
    
    def getAnswers() = {
      (answersPre ++ answersPost).toArray
    }
    
    def getAnswersString() = {
      (answersPre ++ answersPost).filter(_.yomi.nonEmpty).map(a => a.kanji + "  (" + a.yomi + ")").mkString("\n")
    }
    
    private def initialize {
      List(answersPre, answersPost).foreach { answers =>
        answers.clear
        answers ++= ArrayBuffer.fill(4)(Word("●●", ""))
      }
    }
  }// end object Answer
  
}
