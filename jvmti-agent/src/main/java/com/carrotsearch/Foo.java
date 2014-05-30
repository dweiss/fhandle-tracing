package com.carrotsearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Foo {
  public static void main(String[] args) throws Exception {
    System.out.println("Foo!");
    InputStream is = new FileInputStream(new File("pom.xml"));
    is.close();
  }
}
