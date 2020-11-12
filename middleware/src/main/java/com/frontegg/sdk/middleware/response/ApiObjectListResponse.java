package com.frontegg.sdk.middleware.response;

import java.util.List;

public class ApiObjectListResponse<T> extends BaseResponse {

    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}