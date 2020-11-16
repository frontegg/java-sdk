package com.frontegg.sdk.middleware.spring.context;


import com.frontegg.sdk.middleware.model.Permission;

import java.util.List;

public interface IPermissionResolver {

    List<Permission> resolveAppPermissions();
}
