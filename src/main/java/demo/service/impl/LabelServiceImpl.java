package demo.service.impl;

import demo.service.LabelService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cengruilin on 2017/12/19.
 */
@Service
public class LabelServiceImpl implements LabelService, InitializingBean {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.jdbcTemplate.queryForRowSet("select count(*) from " + TABLE);
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("no such table") > -1) {
                String createTableSql = "create table " + TABLE + " ";
                createTableSql += " (";
                createTableSql += " id integer PRIMARY KEY AUTOINCREMENT, ";
                createTableSql += " task_id integer NOT NULL, ";
                createTableSql += " example_id integer NOT NULL, ";
                createTableSql += " label text, ";
                createTableSql += " created_at datetime, ";
                createTableSql += " modified_at datetime ";
                createTableSql += " )";
                this.jdbcTemplate.execute(createTableSql);
            } else {
                throw ex;
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void createLabels(long taskId, List<Long> exampleIds) {
        Date now = new Date();
        for (Long exampleId : exampleIds) {
            Map<String, Object> params = new HashMap<>();
            params.put("task_id", taskId);
            params.put("example_id", exampleId);
            params.put("created_at", now);
            params.put("modified_at", now);
            params.put("label", "-");

            SimpleJdbcInsert insert = new SimpleJdbcInsert(this.jdbcTemplate);
            insert.withTableName(TABLE).usingGeneratedKeyColumns("id");
            insert.execute(params);
        }

    }

    @Override
    public Map<Long, String> findLabelValues(long taskId) {
        List<Map<String, Object>> queryData = this.jdbcTemplate.queryForList("SELECT * from " + TABLE + " WHERE task_id=?", new Object[]{taskId});
        Map<Long, String> result = new HashMap<>();
        for (Map<String, Object> item : queryData) {
            String value = item.get("label").toString();
            if (!value.equals("-")) {
                result.put(Long.parseLong(item.get("example_id").toString()), value);
            }
        }
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public void updateLabelValue(long taskId, long exampleId, String value) {
        this.jdbcTemplate.update("UPDATE " + TABLE + " SET label=? WHERE example_id=? AND task_id=?", new Object[]{value, exampleId, taskId});
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteLabels(long taskId) {
        this.jdbcTemplate.update("DELETE FROM " + TABLE + " WHERE task_id=?", new Object[]{taskId});
    }

    @Override
    public void deleteLabelByTaskAndExample(Long taskId, Long exampleId) {
        this.jdbcTemplate.update("DELETE FROM " + TABLE + " WHERE task_id=? AND example_id=?", new Object[]{taskId, exampleId});
    }
}
