package com.example.jukugoquiz

import android.os.Bundle
import android.app.Activity
import android.view.Menu
import android.widget._

class MainActivity extends Activity {

  override protected def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
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

  def showMessage(s: String){
    val b = new android.app.AlertDialog.Builder(this)
    b.setMessage(s).setPositiveButton(android.R.string.ok, null).show()
    android.util.Log.d(getString(R.string.app_name), s)
  }


}
