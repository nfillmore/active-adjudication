package demo.dto;

/**
 * Created by cengruilin on 2018/3/19.
 */
public class SubmitConsensusRequest {
    private Long exampleId;
    private String labelValue;

    public Long getExampleId() {
        return exampleId;
    }

    public void setExampleId(Long exampleId) {
        this.exampleId = exampleId;
    }

    public String getLabelValue() {
        return labelValue;
    }

    public void setLabelValue(String labelValue) {
        this.labelValue = labelValue;
    }
}
