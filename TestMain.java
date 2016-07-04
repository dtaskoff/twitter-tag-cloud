package ttc;
import java.io.*;
import java.util.*;

import java.lang.Thread;

public class TestMain {
  public static void test() {

    TStreamer streamer = new TStreamer(1);
    streamer.stream();

    new java.util.Timer().schedule(
        new java.util.TimerTask() {
            @Override
            public void run() {
              streamer.close();
              HashMap<String,Integer> hey = (HashMap<String,Integer>) streamer.getWordCount();
              System.out.println(hey.size());
            }
        },
        10000
   );

  }
}
