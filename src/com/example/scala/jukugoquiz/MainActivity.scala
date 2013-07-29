package com.example.scala.jukugoquiz

import android.os.Bundle
import android.app.Activity
import android.view.Menu
import android.view.View
import android.widget._
import java.io.IOException
import java.util.Date

class MainActivity extends Activity {
  override protected def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    this.showQuestion
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    getMenuInflater().inflate(R.menu.main, menu)
    true
  }

  override def onConfigurationChanged(newConfig: android.content.res.Configuration) {
    super.onConfigurationChanged(newConfig)
  }

 /**
  * buttonSubmitClick
  */
  def buttonSubmitClick(v: View) = findViewById(R.id.editTextAnswer) match {
      case e: EditText if e.getText.toString == "" => showMessage("漢字を入力してください。")
      case e: EditText if e.getText.toString.charAt(0) == Answer.getAnswerChar => {
        showMessage("正解です!\n\n" + Answer.getAnswersString)
        e.setText(Answer.getAnswerChar.toString)
        this.vibrate(500)
      }
      case e: EditText => showMessage("違います!"); e.setText(""); this.vibrate(50)
  }

  /**
   * buttonGiveUpClick
   */
  def buttonGiveUpClick(v: View) {
    findViewById(R.id.editTextAnswer).asInstanceOf[EditText].setText(Answer.getAnswerChar.toString)
    findViewById(R.id.buttonSubmit).asInstanceOf[Button].setEnabled(false)
    showMessage("答えは「" + Answer.getAnswerChar.toString + "」です。\n\n" + Answer.getAnswersString)
  }

  /**
   * buttonNextClick
   */
  def buttonNextClick(v: View) = showQuestion()

  /**
   * showQuestion
   */
  private def showQuestion() {
    val watch = Watch("showQuestion")

    Answer.setNewAnswers.getAnswers.zipWithIndex.foreach { case (w:Word , i: Int) =>
      val vId = getResources.getIdentifier("textView" + (i + 1).toString, "id", getPackageName())
      findViewById(vId).asInstanceOf[TextView].setText(w.answerIndexToChar(i).toString)
    }
    findViewById(R.id.editTextAnswer).asInstanceOf[EditText].setText("")
    findViewById(R.id.buttonSubmit).asInstanceOf[Button].setEnabled(true)

    watch.log
  }

  /**
   * showMessage
   */
  private def showMessage(s: String){
    val b = new android.app.AlertDialog.Builder(this)
    b.setMessage(s).setPositiveButton(android.R.string.ok, null).show()
  }

  /**
   * vibrate
   */
  private def vibrate(milliseconds: Long) {
    getSystemService(android.content.Context.VIBRATOR_SERVICE).asInstanceOf[android.os.Vibrator].vibrate(milliseconds)
  }

  /**
   * case class Word
   */
  case class Word(kanji: String, yomi: String) {
    lazy val char1 = kanji.charAt(0)
    lazy val char2 = kanji.charAt(1)
    def answerIndexToChar(i: Int): Char = i match {
      case i:Int if i >= 0 && i <= 3 => this.char1
      case i:Int if i >= 4 && i <= 8 => this.char2
      case _ => 0
    }
  }//end of case class Word

  /**
   * object Answer
   */
  object Answer {
    import scala.collection.mutable.ArrayBuffer
    val answersPre  = ArrayBuffer[Word]()
    val answersPost = ArrayBuffer[Word]()
    private var answerChar: Char = Char.MinValue

    def setNewAnswers() = {
      import scala.util.Random

      val selectedChar = Dictionary.words((new Random).nextInt(Dictionary.words.size)).kanji.charAt((new Random).nextInt(2))
      val wordsFilterd = Dictionary.words.filter(_.kanji.indexOf(selectedChar) != -1)
      val preCandidate  = wordsFilterd.filter(_.kanji.indexOf(selectedChar) == 1)
      val postCandidate = wordsFilterd.filter(_.kanji.indexOf(selectedChar) == 0)
      //answersPre・answersPostを初期化
      List(answersPre, answersPost).foreach(_.clear)
      List(answersPre, answersPost).foreach(_ ++= ArrayBuffer.fill(4)(Word("●●", "")))
      //preCandidate・postCandidateからランダムに4件づつ取り出し
      Random.shuffle(preCandidate.toList).take(4).zipWithIndex.foreach{ case (w:Word , i: Int) => answersPre(i) = w }
      Random.shuffle(postCandidate.toList).take(4).zipWithIndex.foreach{ case (w:Word , i: Int) => answersPost(i) = w }
      this.answerChar = selectedChar
      this
    }

    def getAnswerChar() = this.answerChar
    def getAnswers() = (answersPre ++ answersPost).toArray
    def getAnswersString() = (answersPre ++ answersPost).filter(_.yomi.nonEmpty).map(a => a.kanji + "  (" + a.yomi + ")").mkString("\n")
  }// end object Answer

  /**
   * object Dictionary
   */
  object Dictionary {
    lazy val words = try{
      val is = getResources.getAssets.open("Jukugo2c_utf8.txt")
      val bis = new java.io.BufferedInputStream(is)
      var byteArray: Array[Byte] = new Array(bis.available)
      bis.read(byteArray) //全文読み出し
      (new String(byteArray)).split("\n").map(l => Word(l.substring(0, l.indexOf(",")), l.substring(l.indexOf(",") + 1)))
    } catch { case e: IOException => throw new IOException(e.getMessage.toString) }
  }//end object Dictionary

  /**
   * 実行時間計測用
   * case class Watch
   */
  case class Watch (label: String) {
    private val start: Date = new Date
    def log() = {
      android.util.Log.d(label, "Watch -- " + label + " : " + ((new Date).getTime - start.getTime).toString + " ms")
    }
  }// end case class Watch
}