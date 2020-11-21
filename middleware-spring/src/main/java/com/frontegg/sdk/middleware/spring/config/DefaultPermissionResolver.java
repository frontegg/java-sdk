package com.frontegg.sdk.middleware.spring.config;

import com.frontegg.sdk.middleware.permission.model.Permission;
import com.frontegg.sdk.middleware.permission.model.FronteggPermissionEnum;
import com.frontegg.sdk.middleware.permission.model.PermissionActionEnum;
import com.frontegg.sdk.middleware.spring.context.IPermissionResolver;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DefaultPermissionResolver implements IPermissionResolver {

    @Override
    public List<Permission> resolveAppPermissions() {
        return Arrays.asList(
                FronteggPermissionEnum.ALL.with(PermissionActionEnum.ALL)
        );
    }
}
