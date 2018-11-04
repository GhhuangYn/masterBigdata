package group.wc;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import wc.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @author: HuangYn
 * @date: 2018/10/5 18:43
 */
public class WordCountCounterBolt implements IRichBolt {

    private TopologyContext context;
    private OutputCollector collector;

    private Map<String, Integer> wordMap = new HashMap<>();

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
//        Util.sendToClient(this, "prepare()","node01",8888);
        this.context = context;
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        String word = (String) input.getValueByField("word");
        Integer count = (Integer) input.getValueByField("count");
        if (!wordMap.containsKey(word)) {
            wordMap.put(word, count);
        } else {
            wordMap.put(word, wordMap.get(word) + count);
        }
        Util.sendToLocalClient(this, "execute()-(" + word + "," + count + ")", 6667);
        collector.emit(new Values(word, count));
    }

    @Override
    public void cleanup() {
        wordMap.forEach((k, v) -> System.out.println(k + ": " + v));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
