package demo.ml;

import java.util.List;
import java.util.Map;

/**
 * Created by cengruilin on 2018/2/21.
 */
public interface MachineLearningCalculator {
    List<Map<String, Object>> calculate(List<Map<String, Object>> input);
}
