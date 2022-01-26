package com.pattycake.example.javacpp;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
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
public class Greeter {
  public static class GreeterClass extends Pointer {
    static { Loader.load(); }
    public GreeterClass() { allocate(); }
    private native void allocate();

    // to call the getter and setter functions
    public native @StdString String greeting();
  }

}
