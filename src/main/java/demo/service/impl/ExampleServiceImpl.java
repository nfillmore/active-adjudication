package demo.service.impl;

import demo.service.ExampleService;
import demo.service.LabelService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cengruilin on 2017/11/2.
 */
@Service
public class ExampleServiceImpl implements ExampleService, InitializingBean {

    private final static String TABLE = "result_set";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<String> columns;

    @Override
    public List<String> getColumns() {
        return columns;
    }

    @Override
    public List<Map<String, Object>> findAll() {
        return jdbcTemplate.queryForList("SELECT * from " + TABLE);
    }

    @Override
    public List<Map<String, Object>> findAll(long taskId, String where) {
        return jdbcTemplate.queryForList(String.format("select a.* from " + TABLE + " a, " + LabelService.TABLE + " b where b.task_id=? and a.id=b.example_id %s", where == null ? "" : where), new Object[]{taskId});
    }

    @Override
    public List<Map<String, Object>> findByTaskId(long taskId, String where, String sortColumnName, String sortDirection) {
        return jdbcTemplate.queryForList(String.format("select a.* from " + TABLE + " a, " + LabelService.TABLE + " b where b.task_id=? and a.id=b.example_id %s ORDER BY " + sortColumnName + " " + sortDirection,
                where == null ? "" : where), new Object[]{taskId});
    }

    @Override
    public List<Map<String, Object>> findAllWithSort(String sortColumnName, String sortDirection) {
        return jdbcTemplate.queryForList("SELECT * from " + TABLE + " ORDER BY " + sortColumnName + " " + sortDirection);
    }

    @Override
    public List<Map<String, Object>> findBySearchCriteria(String searchCriteria) {
        if (StringUtils.isEmpty(searchCriteria)) {
            return jdbcTemplate.queryForList("SELECT * from " + TABLE + " LIMIT 200");
        }
        return jdbcTemplate.queryForList("SELECT * from " + TABLE + " WHERE 1=1 " + (searchCriteria == null ? "" : " AND (" + searchCriteria + ")") + " LIMIT 200");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList("SELECT * from " + TABLE + " LIMIT 1");
        if (queryResult.isEmpty()) {
            throw new IllegalStateException("No any data, can't get the columns");
        }
        columns = new ArrayList<>(queryResult.get(0).keySet());
    }
}
