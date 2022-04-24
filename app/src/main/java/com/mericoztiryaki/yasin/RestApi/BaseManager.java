package com.mericoztiryaki.yasin.RestApi;

public class BaseManager {


    protected RestApi getRestApiClient() {
        ApiClient restApiClient = new ApiClient(BaseUrl.URL);
        return restApiClient.getRestApi();
    }
}
