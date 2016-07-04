package ttc;
import java.io.*;
import java.lang.Thread;

public class TestMain {
  public static void test() {
    // Create a local StreamingContext with two working thread and batch interval of 1 second
    TStreamer streamer = new TStreamer(1);
    streamer.stream();

    new java.util.Timer().schedule(
        new java.util.TimerTask() {
            @Override
            public void run() {
              System.out.println("Potato");
              streamer.close();
            }
        },
        5
   );

  }
}
