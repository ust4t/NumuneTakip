package com.mericoztiryaki.yasin.designer.taskform;

import com.mericoztiryaki.yasin.model.ProductType;
import com.mericoztiryaki.yasin.model.response.TaskResponse;

import java.util.List;

/**
 * Created by as on 2019-05-09.
 */
public interface TaskFormView {

    void showLoadingDialog();

    void closeLoadingDialog();

    void setProductList(List<ProductType> productList);

    void creationSuccessful(TaskResponse result);

    void creationFailed();

}
