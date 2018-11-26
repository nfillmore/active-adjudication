package demo.ml.impl;

import com.google.common.collect.Maps;
import demo.ml.MachineLearningCalculator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by cengruilin on 2018/2/21.
 */
public class MockMachineLearningCalculator implements MachineLearningCalculator {
    @Override
    public List<Map<String, Object>> calculate(List<Map<String, Object>> input) {

        Map<Long, String> labelData = Maps.newHashMap();
        //resolve label data
        input.stream().forEach(item -> {
            if (item.containsKey("Pred") && null != item.get("Pred")) {
                long rowId = Long.parseLong(item.get("id").toString());
                labelData.put(rowId, item.get("Pred").toString());
            }
        });
        //Calculate
        boolean containsLabel = !labelData.isEmpty();
        input.stream().forEach(item -> {
            long rowId = Long.parseLong(item.get("id").toString());
            if (containsLabel && labelData.containsValue("yes") && labelData.containsValue("no")) {
                String pred = null;
                double pYes = 0;
                if (labelData.containsKey(rowId)) {
                    pred = labelData.get(rowId);
                    if (pred.equals("yes")) {
                        pYes = new BigDecimal(100 - new Random().nextInt(49)).divide(new BigDecimal(100)).doubleValue();
                    } else {
                        pYes = new BigDecimal(new Random().nextInt(49)).divide(new BigDecimal(100)).doubleValue();
                    }

                } else {
                    pYes = new BigDecimal(new Random().nextInt(100)).divide(new BigDecimal(100)).doubleValue();
                    pred = "yes";
                    if (pYes < 0.5) {
                        pred = "no";
                    }
                }
                item.put("Pred", pred);
                item.put("P(yes)", pYes);
                item.put("Margin", "" + new BigDecimal(new Random().nextInt(100)).divide(new BigDecimal(100)));

            } else {
                item.put("Pred", "-");
                item.put("P(yes)", "nan");
                item.put("Margin", "nan");
            }

            item.put("User", generateLabelLink(rowId, labelData));

            item.put("RowID", rowId);
        });

        return input;
    }

    private String generateLabelLink(long rowId, Map<Long, String> labelData) {
        if (labelData.containsKey(rowId)) {
            String val = labelData.get(rowId);
            if (val.equals("yes")) {
                return String.format("yes&nbsp;<a href='javascript:label_example(%s, \"no\")'>no</a>&nbsp;<a href='javascript:label_example(%s, \"-\")'>-</a>", rowId, rowId);
            } else {
                return String.format("<a href='javascript:label_example(%s, \"yes\")'>yes</a>&nbsp;no&nbsp;<a href='javascript:label_example(%s, \"-\")'>-</a>", rowId, rowId);
            }
        } else {
            return String.format("<a href='javascript:label_example(%s, \"yes\")'>yes</a>&nbsp;<a href='javascript:label_example(%s, \"no\")'>no</a>&nbsp;-", rowId, rowId);
        }
    }
}
