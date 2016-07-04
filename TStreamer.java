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
    tweets = TwitterUtils.createStream(jsc).window(Durations.seconds(INTERVAL), Durations.seconds(INTERVAL));
  }

  public void stream() {
    stream = new Thread(new Runnable() {
      @Override
      public void run(){
          streamTweets();
      }
    });
    stream.start();
  }

  public void close() {
    jsc.stop();
  }

  public Map<String,Integer> getWordCount() {
    HashMap<String, Integer> countedHashtags = new HashMap<String, Integer>();
    HashMap<String, ArrayList<Integer>> currentHashtags;
    synchronized(this) {
      currentHashtags = (HashMap<String, ArrayList<Integer>>)hashTags.clone();
    }

    for (String key : currentHashtags.keySet()) {
      countedHashtags.put(key, currentHashtags.get(key).size());
    }

    return countedHashtags;

  }

  private void streamTweets() {
    tweets.map(tweet -> tweet.getText()).flatMap(msg -> Arrays.asList(msg.split(" "))).filter(word -> word.startsWith("#")).foreach(e -> { filterMap(); addToMap(e.collect()); return null;});
    jsc.start();
    jsc.awaitTermination();
  }

  private synchronized void filterMap() {
    hashTags.values().removeAll(Collections.singleton(round));
  }

  private synchronized void addToMap(List<String> tags) {
    for (String tag : tags) {
      tag = tag.substring(1);
      if(hashTags.containsKey(tag)) {
      hashTags.get(tag).add(round);
    } else {
      ArrayList<Integer> count = new ArrayList();
      count.add(round);
      hashTags.put(tag, count);
      }
    }
    round = (round + 1) % CLEANER;
  }
}
