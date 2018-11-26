package demo.service;

import demo.dto.CreateTaskRequest;
import demo.dto.SubmitConsensusRequest;
import demo.dto.TaskSummary;

import java.util.List;
import java.util.Map;

/**
 * Created by cengruilin on 2017/12/19.
 */
public interface TaskService {
    public final static String TABLE = "task";

    public long createTask(String actorId, CreateTaskRequest createTaskRequest);

    public Long[] forkTask(String actorId, long forkFrom, String[] targetUsers);

    public void updateTask(String actorId, long taskId, CreateTaskRequest createTaskRequest);

    public void deleteTask(long taskId, String actorId);

    public List<TaskSummary> list(String ownerId);

    public List<TaskSummary> listAll();

    public List<TaskSummary> listRoot(String ownerId);

    public TaskSummary findById(String actorId, long taskId);

    public TaskSummary findById(long taskId);

    public Map<String, Object> simulateConsensus(long taskId);

    public List<TaskSummary> listByForkFromId(Long forkFromId);

    void submitConsensus(Long taskId, List<SubmitConsensusRequest> request);
}
