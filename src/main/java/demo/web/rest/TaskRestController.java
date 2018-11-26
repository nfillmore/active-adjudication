package demo.web.rest;

import demo.dto.CreateTaskRequest;
import demo.dto.SubmitConsensusRequest;
import demo.dto.TaskSummary;
import demo.security.SecurityContext;
import demo.service.ExampleService;
import demo.service.LabelService;
import demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cengruilin on 2017/12/20.
 */
@RestController
@RequestMapping("/api")
public class TaskRestController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ExampleService exampleService;

    @Autowired
    private LabelService labelService;

    @GetMapping("/tasks")
    public List<TaskSummary> findAll() {
        return taskService.list(SecurityContext.currentUserId());
    }

    @GetMapping("/tasks/root")
    public List<TaskSummary> findAllRoot() {
        return taskService.listRoot(SecurityContext.currentUserId());
    }

    @PostMapping("/tasks")
    public Object create(CreateTaskRequest request) {
        Map result = new HashMap();
        long id = taskService.createTask(SecurityContext.currentUserId(), request);
        result.put("id", id);
        return result;
    }

    @PostMapping("/tasks/testSearchCriteria")
    public List<Map<String, Object>> testSearchCriteria(String searchCriteria) {
        return exampleService.findBySearchCriteria(searchCriteria);
    }

    @GetMapping("/tasks/{taskId}")
    public Object findById(@PathVariable("taskId") Long taskId) {
        return taskService.findById(SecurityContext.currentUserId(), taskId);
    }

    @PostMapping("/tasks/{taskId}")
    public void update(@PathVariable("taskId") Long taskId, CreateTaskRequest request) {
        taskService.updateTask(SecurityContext.currentUserId(), taskId, request);
    }

    @PostMapping("/tasks/{taskId}/fork")
    public void fork(@PathVariable("taskId") Long taskId, String targetUsers) {
        taskService.forkTask(SecurityContext.currentUserId(), taskId, targetUsers.split(","));
    }

    @DeleteMapping("/tasks/{taskId}")
    public void deleteTask(@PathVariable("taskId") Long taskId) {
        if (null == taskId) {
            throw new IllegalArgumentException("Task id can not be null for delete");
        }

        taskService.deleteTask(taskId, SecurityContext.currentUserId());
    }

    @PostMapping("/tasks/{taskId}/simulateConsensus")
    public Map<String, Object> simulateConsensus(@PathVariable("taskId") Long taskId) {
        return taskService.simulateConsensus(taskId);
    }

    @PostMapping("/tasks/{taskId}/submitConsensus")
    public void submitConsensus(@PathVariable("taskId") Long taskId, @RequestBody  List<SubmitConsensusRequest> request) {
        taskService.submitConsensus(taskId, request);
    }

    @GetMapping("/tasks/{taskId}/export")
    public void export(@PathVariable("taskId") Long taskId, HttpServletResponse response) throws IOException {
        if (null == taskId) {
            throw new IllegalArgumentException("Task id can not be null for delete");
        }
        TaskSummary task = taskService.findById(SecurityContext.currentUserId(), taskId);
        List<Map<String, Object>> queryResult = exampleService.findAll(taskId, null);
        Map<Long, String> labelData = labelService.findLabelValues(taskId);

        response.setContentType("text/csv");
        String csvFileName = task.getName() + "'s labels.csv";
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                csvFileName);
        response.setHeader(headerKey, headerValue);

        ICsvMapWriter csvWriter = new CsvMapWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);
        List<String> exampleColumns = new ArrayList<>(exampleService.getColumns());
        exampleColumns.add(0, "Label");
        String[] header = exampleColumns.toArray(new String[]{});
        csvWriter.writeHeader(header);
        for (Map<String, Object> example : queryResult) {
            String labelValue = labelData.get(Long.parseLong(example.get("id").toString()));
            if (null == labelValue) {
                labelValue = "-";
            }
            example.put("Label", labelValue);
            csvWriter.write(example, header);
        }
        csvWriter.close();
    }
}
