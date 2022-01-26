package com.pattycake.example.javacpp;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.annotation.Namespace;
import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.StdString;

@Platform(
//    library = "Greeter",
    include= "demo.h"
//    ,
//    includepath = "/Users/patrick/Documents/IntelliJ/examples/javacpp-native/src/main/headers/",
//    link = "Greeter",
//    linkpath = "/Users/patrick/Documents/IntelliJ/examples/javacpp-native/build/lib/main/debug/"
)
@Namespace("demo")
public class Greeter2 {
    static { Loader.load(); }

    // to call the getter and setter functions
    public static native @StdString String greeting();

  public static void main(String[] args) {
    greeting();
  }
}
