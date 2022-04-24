package com.mericoztiryaki.yasin.model.response;

import com.mericoztiryaki.yasin.model.Task;

import java.io.Serializable;

/**
 * Created by as on 2019-05-11.
 */
public class TaskStatus extends Task implements Serializable {

    private int responsibleId;
    private String responsibleUsername;
    private String department;
    private int isStarted;
    private String productTypeName;
    private String filePath = "";

    public String getFilePath() {
        if (filePath == null) {
            return "";
        }
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getResponsibleId() {
        return responsibleId;
    }

    public String getResponsibleUsername() {
        return responsibleUsername;
    }

    public String getDepartment() {
        return department;
    }

    public int getIsStarted() {
        return isStarted;
    }

    public String getProductTypeName() {
        return productTypeName;
    }
}
