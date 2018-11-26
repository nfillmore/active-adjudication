package demo.dto;

import java.util.Date;

/**
 * Created by cengruilin on 2017/12/19.
 */
public class CreateTaskRequest {
    private String name;
    private String searchCriteria;
    private Date createdAt;
    private Date modifiedAt;
    private String ownerId;
    private Long forkFromId;

    public Long getForkFromId() {
        return forkFromId;
    }

    public void setForkFromId(Long forkFromId) {
        this.forkFromId = forkFromId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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
}
