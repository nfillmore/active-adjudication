package demo.dto;

import java.util.Date;

/**
 * Created by cengruilin on 2017/12/19.
 */
public class TaskSummary {
    private long id;
    private String ownerId;

    private String name;
    private String searchCriteria;
    private Date createdAt;
    private Date modifiedAt;
    private Long forkFromId;
    private String forkFromName;

    public TaskSummary() {
    }

    public Long getForkFromId() {
        return forkFromId;
    }

    public void setForkFromId(Long forkFromId) {
        this.forkFromId = forkFromId;
    }

    public String getForkFromName() {
        return forkFromName;
    }

    public void setForkFromName(String forkFromName) {
        this.forkFromName = forkFromName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(String searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
