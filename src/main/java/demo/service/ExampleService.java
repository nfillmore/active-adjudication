package demo.service;

import java.util.List;
import java.util.Map;

/**
 * Created by cengruilin on 2017/11/2.
 */
public interface ExampleService {
    List<String> getColumns();

    List<Map<String, Object>> findAll();

    List<Map<String, Object>> findAll(long taskId, String where);

    List<Map<String, Object>> findByTaskId(long taskId, String where, String sortColumnName, String sortDirection);

    List<Map<String, Object>> findAllWithSort(String sortColumnName, String sortDirection);

    List<Map<String, Object>> findBySearchCriteria(String searchCriteria);

}
