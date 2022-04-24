package com.mericoztiryaki.yasin.model;

import com.mericoztiryaki.yasin.Constants;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by as on 2019-05-10.
 */
public class Task implements Serializable {

    private int id;
    private String modelRef;
    private String modelName;
    private String materialRef;
    private String materialName;
    private int quantity;
    private String description;
    private String productType;
    private int isOutSourced;
    private int isExpress;
    private int creatorId;
    private int isCompleted;
    private Date creationDate;
    private String fileURL;

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public Task(String modelRef, String modelName, String materialRef, String materialName,
                int quantity, String description, ProductType productType,
                boolean isOutSourced, boolean isExpress, int creatorId) {
        this.modelRef = modelRef;
        this.modelName = modelName;
        this.materialRef = materialRef;
        this.materialName = materialName;
        this.quantity = quantity;
        this.description = description;
        this.productType = productType.getCode();
        this.isOutSourced = isOutSourced ? 1 : 0;
        this.isExpress = isExpress ? 1 : 0;
        this.creatorId = creatorId;
    }

    public Task(int id, String modelRef, String modelName, String materialRef) {
        this.id = id;
        this.modelRef = modelRef;
        this.modelName = modelName;
        this.materialRef = materialRef;
    }

    public Task() {
    }

    public int getId() {
        return id;
    }

    public String getModelRef() {
        return modelRef;
    }

    public String getModelName() {
        return modelName;
    }

    public String getMaterialRef() {
        return materialRef;
    }

    public String getMaterialName() {
        return materialName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

    public String getProductType() {
        return productType;
    }

    public int getIsOutSourced() {
        return isOutSourced;
    }

    public int getIsExpress() {
        return isExpress;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public String getImageUrl() {
        return Constants.BASE_URL + "/uploads/task_img_" + id + ".jpg";
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getFormattedCreationTime(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(this.creationDate);
    }
}
