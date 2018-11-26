package demo.ml.impl;

import com.google.common.collect.Maps;
import demo.ml.MachineLearningCalculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;

/**
 * Created by cengruilin on 2018/2/21.
 */
public class TFMachineLearningCalculator implements MachineLearningCalculator {
    @Override
    public List<Map<String, Object>> calculate(List<Map<String, Object>> input) {

        // try (Graph g = new Graph()) {
        //   final String value = "Hello from " + TensorFlow.version();

        //   // Construct the computation graph with a single operation, a constant
        //   // named "MyConst" with a value "value".
        //   try (Tensor t = Tensor.create(value.getBytes("UTF-8"))) {
        //     // The Java API doesn't yet include convenience functions for adding operations.
        //     g.opBuilder("Const", "MyConst").setAttr("dtype", t.dataType()).setAttr("value", t).build();
        //   }

        //   // Execute the "MyConst" operation in a Session.
        //   try (Session s = new Session(g);
        //        Tensor output = s.runner().fetch("MyConst").run().get(0)) {
        //     System.out.println(new String(output.bytesValue(), "UTF-8"));
        //   }
        // } catch (java.io.UnsupportedEncodingException e) {
        //   throw new AssertionError("UTF-8 is unknown");
        // }

        // Define column types.
        ArrayList<String> string_colnames = new ArrayList<String>();
        string_colnames.add("LabChemTestName");
        string_colnames.add("Units");
        string_colnames.add("LOINC");
        string_colnames.add("Component");
        string_colnames.add("Topography");
        int num_numeric_features = 10;

        // Init maps from column name -> (value -> index).
        HashMap<String, HashMap<String, Integer>> value_to_index = new HashMap<String, HashMap<String, Integer>>();
        string_colnames.forEach(colname -> {
          value_to_index.put(colname, new HashMap<String, Integer>());
        });

        // Assign an index to each value, for each column.
        input.stream().forEach(item -> {
          string_colnames.forEach(colname -> {
            String value = item.get(colname).toString();
            HashMap<String, Integer> hm = value_to_index.get(colname);
            if (!hm.containsKey(value)) {
              int index = hm.size();
              hm.put(value, index);
              //System.out.println(String.format("Assigning %d to %s for %s", index, value, colname));
            }
          });
        });

        // Determine the first feature index for each column.
        HashMap<String, Integer> colname_to_offset = new HashMap<String, Integer>();
        int total_num_features;
        {
          int offset = num_numeric_features;
          for (String colname : string_colnames) {
            colname_to_offset.put(colname, offset);
            int num_features_for_column = value_to_index.get(colname).size();
            offset += num_features_for_column;
          }
          total_num_features = offset;
        }

		// List<Instance> instances
        List<Logistic.Instance> train_dataset = new ArrayList<Logistic.Instance>();
        HashMap<String, Logistic.Instance> test_dataset = new HashMap<String, Logistic.Instance>();
        input.stream().forEach(item -> {
          double[] data = new double[total_num_features];
          data[0] = Double.parseDouble(item.get("p1")==null?"0":item.get("p1").toString());
          data[1] = Double.parseDouble(item.get("p1")==null?"0":item.get("p1").toString());
          data[2] = Double.parseDouble(item.get("p5")==null?"0":item.get("p5").toString());
          data[3] = Double.parseDouble(item.get("p10")==null?"0":item.get("p10").toString());
          data[4] = Double.parseDouble(item.get("p25")==null?"0":item.get("p25").toString());
          data[5] = Double.parseDouble(item.get("p50")==null?"0":item.get("p50").toString());
          data[6] = Double.parseDouble(item.get("p75")==null?"0":item.get("p75").toString());
          data[7] = Double.parseDouble(item.get("p90")==null?"0":item.get("p90").toString());
          data[8] = Double.parseDouble(item.get("p95")==null?"0":item.get("p95").toString());
          data[9] = Double.parseDouble(item.get("p99")==null?"0":item.get("p99").toString());

          string_colnames.forEach(colname -> {
            String value = item.get(colname).toString();
            int index = value_to_index.get(colname).get(value);
            int offset = colname_to_offset.get(colname);
            //System.out.println(String.format("%s %s %d %d %d", colname, value, index, offset, total_num_features));
            data[offset+index] = 1.0;
          });

          for (String k: item.keySet()) {
            //System.out.println(k);
          }

          boolean is_train = false;
          int label = 1000000;
          if (item.containsKey("Pred")) {
            String label_string = item.get("Pred").toString();
            if (label_string.equals("yes")) {
              is_train = true;
              label = 1;
            } else if (label_string.equals("no")) {
              is_train = true;
              label = 0;
            }
          }

          Logistic.Instance instance = new Logistic.Instance(label, data);
          if (is_train)
            train_dataset.add(instance);

          String row_id = item.get("id").toString();
          test_dataset.put(row_id, instance);
        });

        // Check if we have both yes and no labels.
        boolean has_yes = false;
        boolean has_no = false;
        for (Logistic.Instance instance : train_dataset) {
          if (instance.label == 1)
            has_yes = true;
          else if (instance.label == 0)
            has_no = true;
        }
        
        // If we do have both yes and no labels, train and report predictions.
        if (has_yes && has_no) {
          // Train the model.
          Logistic logistic = new Logistic(total_num_features);
          logistic.train(train_dataset);

          // Use the model to make predictions.
          HashMap<String, Integer> test_labels = new HashMap<String, Integer>();
          HashMap<String, Double> test_probs = new HashMap<String, Double>();
          HashMap<String, Double> test_margins = new HashMap<String, Double>();
          test_dataset.forEach((row_id, instance) -> {
            double prob_yes = logistic.classify(instance.x);
            double prob_no = 1.0 - prob_yes;
            double margin = Math.abs(prob_yes - prob_no);
            int label = (prob_yes >= prob_no) ? 1 : 0;
            test_labels.put(row_id, label);
            test_probs.put(row_id, prob_yes);
            test_margins.put(row_id, margin);
          });

          // Record the predictions.
          input.stream().forEach(item -> {
            String row_id = item.get("id").toString();
            int user_label = test_dataset.get(row_id).label;
            item.put("Pred", test_labels.get(row_id) == 1 ? "yes" : "no");
            item.put("User", generateLabelLink(row_id, user_label));
            item.put("P(yes)", String.format("%.2f", test_probs.get(row_id)));
            item.put("Margin", String.format("%.2f", test_margins.get(row_id)));
          });
        }

        // If we don't have both yes and no labels, report all zeros.
        else {
          input.stream().forEach(item -> {
            String row_id = item.get("id").toString();
            int user_label = test_dataset.get(row_id).label;
            item.put("Pred", "-");
            item.put("User", generateLabelLink(row_id, user_label));
            item.put("P(yes)", "-");
            item.put("Margin", "-");
          });
        }

        return input;
    }

    private String generateLabelLink(String rowId, int label) {
        if (label == 1) { // yes
            return String.format("yes&nbsp;<a href='javascript:label_example(%s, \"no\")'>no</a>&nbsp;<a href='javascript:label_example(%s, \"-\")'>-</a>", rowId, rowId);
        } else if (label == 0) { // no
            return String.format("<a href='javascript:label_example(%s, \"yes\")'>yes</a>&nbsp;no&nbsp;<a href='javascript:label_example(%s, \"-\")'>-</a>", rowId, rowId);
        } else { // -
          return String.format("<a href='javascript:label_example(%s, \"yes\")'>yes</a>&nbsp;<a href='javascript:label_example(%s, \"no\")'>no</a>&nbsp;-", rowId, rowId);
        }
    }
}
