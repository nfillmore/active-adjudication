package demo.service.impl;

import com.google.common.collect.Maps;
import demo.dto.CreateTaskRequest;
import demo.dto.SubmitConsensusRequest;
import demo.dto.TaskSummary;
import demo.service.ExampleService;
import demo.service.LabelService;
import demo.service.TaskService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by cengruilin on 2017/12/19.
 */
@Service
public class TaskServiceImpl implements TaskService, InitializingBean {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ExampleService exampleService;

    @Autowired
    private LabelService labelService;

    @Override
    @Transactional(readOnly = false)
    public long createTask(String actorId, CreateTaskRequest createTaskRequest) {
        if (StringUtils.isEmpty(createTaskRequest.getName())) {
            throw new IllegalArgumentException("Task name can not be empty");
        }

        List<Map<String, Object>> examples = this.exampleService.findBySearchCriteria(createTaskRequest.getSearchCriteria());

        Date now = new Date();
        createTaskRequest.setCreatedAt(now);
        createTaskRequest.setModifiedAt(now);
        createTaskRequest.setOwnerId(actorId);
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(TABLE).usingGeneratedKeyColumns("id");
        Number key = jdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(
                createTaskRequest));
        long taskId = ((Number) key).longValue();


        List<Long> exampleIds = examples.stream().map(item -> Long.parseLong(item.get("id").toString())).collect(Collectors.toList());
        labelService.createLabels(taskId, exampleIds);
        return taskId;
    }

    @Override
    public Long[] forkTask(String actorId, long forkFrom, String[] targetUsers) {
        if (forkFrom <= 0) {
            throw new IllegalArgumentException("Fork from can not be empty");
        }

        TaskSummary forkFromTask = findById(forkFrom);
        if (null == forkFromTask) {
            throw new IllegalArgumentException(String.format("Task[%s] not existed", forkFrom));
        }

        List<Long> result = new ArrayList<>();
        List<Map<String, Object>> examples = this.exampleService.findBySearchCriteria(forkFromTask.getSearchCriteria());
        for (String username : targetUsers) {
            CreateTaskRequest createTaskRequest = new CreateTaskRequest();
            Date now = new Date();
            createTaskRequest.setOwnerId(username);
            createTaskRequest.setModifiedAt(now);
            createTaskRequest.setCreatedAt(now);
            createTaskRequest.setName(String.format("%s(From %s)", forkFromTask.getName(), actorId));
            createTaskRequest.setSearchCriteria(forkFromTask.getSearchCriteria());
            createTaskRequest.setForkFromId(forkFrom);
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            jdbcInsert.withTableName(TABLE).usingGeneratedKeyColumns("id");
            Number key = jdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(
                    createTaskRequest));
            long taskId = ((Number) key).longValue();

            List<Long> exampleIds = examples.stream().map(item -> Long.parseLong(item.get("id").toString())).collect(Collectors.toList());
            labelService.createLabels(taskId, exampleIds);
            result.add(taskId);
        }
        return result.toArray(new Long[]{});
    }

    @Override
    public void updateTask(String actorId, long taskId, CreateTaskRequest createTaskRequest) {
        if (StringUtils.isEmpty(createTaskRequest.getName())) {
            throw new IllegalArgumentException("Task name can not be empty");
        }
        List<Map<String, Object>> examples = this.exampleService.findBySearchCriteria(createTaskRequest.getSearchCriteria());

        Date now = new Date();
        this.jdbcTemplate.update("UPDATE " + TABLE + " SET name=?, search_criteria=?, modified_at=? WHERE owner_id=? AND id=?",
                new Object[]{createTaskRequest.getName(), createTaskRequest.getSearchCriteria(), now, actorId, taskId});

        labelService.deleteLabels(taskId);


        List<Long> exampleIds = examples.stream().map(item -> Long.parseLong(item.get("id").toString())).collect(Collectors.toList());
        labelService.createLabels(taskId, exampleIds);

    }

    @Override
    @Transactional(readOnly = false)
    public void deleteTask(long taskId, String actorId) {
        int count = this.jdbcTemplate.update("DELETE FROM " + TABLE + " WHERE id=? AND owner_id=?", new Object[]{taskId, actorId});
        if (count > 0) {
            this.labelService.deleteLabels(taskId);
        }
    }

    @Override
    public List<TaskSummary> list(String ownerId) {
        return this.jdbcTemplate.query("SELECT a.*, b. name as fork_from_name FROM " + TABLE + " a left join " + TABLE + " b on a.fork_from_id = b.id WHERE a.owner_id=?", new Object[]{ownerId},
                new BeanPropertyRowMapper<>(TaskSummary.class));
    }

    @Override
    public List<TaskSummary> listAll() {
        return this.jdbcTemplate.query("SELECT a.*, b. name as fork_from_name FROM " + TABLE + " a left join " + TABLE + " b on a.fork_from_id = b.id",
                new BeanPropertyRowMapper<>(TaskSummary.class));
    }


    @Override
    public List<TaskSummary> listRoot(String ownerId) {
        return this.jdbcTemplate.query("SELECT a.*, b. name as fork_from_name FROM " + TABLE + " a left join " + TABLE + " b on a.fork_from_id = b.id WHERE a.fork_from_id IS NULL AND a.owner_id=?", new Object[]{ownerId},
                new BeanPropertyRowMapper<>(TaskSummary.class));
    }


    @Override
    public List<TaskSummary> listByForkFromId(Long forkFromId) {
        return this.jdbcTemplate.query("SELECT a.*, b. name as fork_from_name FROM " + TABLE + " a left join " + TABLE + " b on a.fork_from_id = b.id WHERE a.fork_from_id=?", new Object[]{forkFromId},
                new BeanPropertyRowMapper<>(TaskSummary.class));
    }

    @Override
    public void submitConsensus(Long taskId, List<SubmitConsensusRequest> request) {
        TaskSummary sourceTask = findById(taskId);
        if (null == sourceTask) {
            throw new IllegalArgumentException(String.format("Task which id is [%s] not existed", taskId));
        }

        for (SubmitConsensusRequest item : request) {
            if (StringUtils.isEmpty(item.getLabelValue())) {
                continue;
            }
            if ("-".equals(item.getLabelValue())) {
                labelService.deleteLabelByTaskAndExample(taskId, item.getExampleId());
            } else {
                labelService.updateLabelValue(taskId, item.getExampleId(), item.getLabelValue());
            }
        }
    }

    @Override
    public TaskSummary findById(String actorId, long taskId) {
        return this.jdbcTemplate.queryForObject("SELECT * FROM " + TABLE + " WHERE owner_id=? AND id=?", new Object[]{actorId, taskId},
                new BeanPropertyRowMapper<>(TaskSummary.class));
    }

    @Override
    public TaskSummary findById(long taskId) {
        return this.jdbcTemplate.queryForObject("SELECT * FROM " + TABLE + " WHERE id=?", new Object[]{taskId},
                new BeanPropertyRowMapper<>(TaskSummary.class));
    }

    @Override
    public Map<String, Object> simulateConsensus(long taskId) {
        TaskSummary sourceTask = findById(taskId);
        List<Map<String, Object>> examples = this.exampleService.findBySearchCriteria(sourceTask.getSearchCriteria());
        Map<Long, String> sourceLabels = labelService.findLabelValues(taskId);
        examples.stream().forEach(item -> {
            long exampleId = Long.parseLong(item.get("id").toString());
            if (sourceLabels.containsKey(exampleId)) {
                item.put("sourceLabelValue", sourceLabels.get(exampleId));
            }
        });

        List<TaskSummary> forkedSubTasks = listByForkFromId(sourceTask.getId());
        for (TaskSummary subTask : forkedSubTasks) {
            Map<Long, String> subTaskLabels = labelService.findLabelValues(subTask.getId());
            examples.stream().forEach(item -> {
                long exampleId = Long.parseLong(item.get("id").toString());
                if (subTaskLabels.containsKey(exampleId)) {
                    item.put(subTask.getId() + "_LabelValue", subTaskLabels.get(exampleId));
                }
            });
        }

        Map result = Maps.newHashMap();
        result.put("forkedSubTasks", forkedSubTasks);
        result.put("examples", examples);

        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            this.jdbcTemplate.queryForRowSet("select count(*) from " + TABLE);
        } catch (Exception ex) {
            if (ex.getMessage().indexOf("no such table") > -1) {
                String createTableSql = "create table " + TABLE + " ";
                createTableSql += " (";
                createTableSql += " id integer PRIMARY KEY AUTOINCREMENT, ";
                createTableSql += " owner_id text NOT NULL, ";
                createTableSql += " name text NOT NULL, ";
                createTableSql += " search_criteria text NOT NULL, ";
                createTableSql += " created_at datetime, ";
                createTableSql += " modified_at datetime ";
                createTableSql += " )";
                this.jdbcTemplate.execute(createTableSql);
            } else {
                throw ex;
            }
        }
    }
}
