package ttc;

import org.apache.spark.*;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.twitter.*;
import org.apache.spark.streaming.api.java.*;
import java.util.*;
import twitter4j.*;

class TStreamer {

  Thread stream;
  static final int INTERVAL = 5;
  int CLEANER;
  int minutes=1;
  int round = 1;

  JavaStreamingContext jsc;
  HashMap<String, ArrayList<Integer>> hashTags;
  JavaDStream<Status> tweets;

  TStreamer() {
    hashTags = new HashMap();
  }

  TStreamer(int n) {
    this.minutes = n;
    hashTags = new HashMap();
    CLEANER = (n * 60)/INTERVAL;

    SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("twitter-tag-cloud");
     jsc = new JavaStreamingContext(conf, Durations.seconds(INTERVAL));

    JavaDStream<Status> tweets = TwitterUtils.createStream(jsc).window(Durations.seconds(INTERVAL), Durations.seconds(INTERVAL));

    tweets.map(tweet -> tweet.getText()).flatMap(msg -> Arrays.asList(msg.split(" "))).filter(word -> word.startsWith("#")).foreach(e -> { addToMap(e.collect()); filterMap(); return null;});

  }

  public void addToMap(List<String> tags) {
    for (String tag : tags) {
      if(hashTags.containsKey(tag)) {
      hashTags.get(tag).add(round);
      System.out.printf("ADDED %s\n", tag);
    } else {
      ArrayList<Integer> count = new ArrayList();
      count.add(round);
      hashTags.put(tag, count);
      System.out.printf("INITIAL ADD %s\n", tag);
      }
		}
  }

  public void filterMap() {
    hashTags.values().removeAll(Collections.singleton(round));
    System.out.println(CLEANER);
    round = (round + 1) % CLEANER;
    System.out.printf("CHANGE ROUND TO %s\n", round);
  }

  public void stream() {
    jsc.start();
    jsc.awaitTermination();
  }

  public void close() {
    // jsc.stop();
    // jsc.close();
  }

  // getWordCount() return map[string]int
}
