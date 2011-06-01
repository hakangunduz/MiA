package mia.clustering.ch12.grouper;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.mahout.common.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByKeyMapper extends Mapper<LongWritable,Text,Text,Text> {
  
  private static final Logger log = LoggerFactory
      .getLogger(ByKeyMapper.class);
  
  private Pattern splitter;
  
  private int selectedField;
  
  private int groupByField;
  
  @Override
  protected void map(LongWritable key, Text value, Context context) throws IOException,
                                                                   InterruptedException {
    String[] fields = splitter.split(value.toString());
    if (fields.length - 1 < selectedField || fields.length - 1 < groupByField) {
      context.getCounter("Map", "LinesWithErrors").increment(1);
      return;
    }
    String oKey = fields[groupByField];
    String oValue = fields[selectedField];
    context.write(new Text(oKey), new Text(oValue));
    
  }
  
  @Override
  protected void setup(Context context) throws IOException,
                                       InterruptedException {
    super.setup(context);
    Parameters params = Parameters.fromString(context
        .getConfiguration().get("job.parameters", ""));
    splitter = Pattern.compile(params.get("splitPattern", "\t"));
    
    selectedField = Integer.valueOf(params.get("selectedField", "1"));
    groupByField = Integer.valueOf(params.get("groupByField", "1"));
    log.info("Using: {} {} {} ", new Object[] {groupByField,
                                               splitter,
                                               selectedField});
  }
}
