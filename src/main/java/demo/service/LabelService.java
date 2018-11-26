package demo.service;

import java.util.List;
import java.util.Map;

/**
 * Created by cengruilin on 2017/12/19.
 */
public interface LabelService {
    public final static String TABLE = "label";

    void createLabels(long taskId, List<Long> exampleIds);

    Map<Long, String> findLabelValues(long taskId);

    void updateLabelValue(long taskId, long exampleId, String value);

    void deleteLabels(long taskId);

    void deleteLabelByTaskAndExample(Long taskId, Long exampleId);
}
