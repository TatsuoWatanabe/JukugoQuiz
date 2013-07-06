package com.example.jukugoquiz

case class Word(kanji: String, yomi: String) {
  lazy val char1 = kanji.charAt(0);
  lazy val char2 = kanji.charAt(1);
  def Search(target: Char): SearchResult = {
    target match {
      case this.char1 => SearchResult.Post
      case this.char2 => SearchResult.Pre
      case _          => SearchResult.NotFound
    }
  }
}

/*
public class Word {
  public String Kanji;
  public String Yomi;
  public char Char1;
  public char Char2;

  public Word(String kanji, String yomi) {
    Kanji = kanji;
    Yomi = yomi;
    Char1 = kanji.charAt(0);
    Char2 = kanji.charAt(1);
  }

  public SearchResult Search(char target) {
    if (target == Char1)
      return SearchResult.Post;
    else if (target == Char2)
      return SearchResult.Pre;
    else
      return SearchResult.NotFound;
  }
}
*/