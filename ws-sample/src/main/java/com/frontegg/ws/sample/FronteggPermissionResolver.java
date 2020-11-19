package com.frontegg.ws.sample;

import com.frontegg.sdk.middleware.model.Permission;
import com.frontegg.sdk.middleware.model.FronteggPermissionEnum;
import com.frontegg.sdk.middleware.model.PermissionActionEnum;
import com.frontegg.sdk.middleware.spring.context.IPermissionResolver;

import java.util.Arrays;
import java.util.List;

public class FronteggPermissionResolver implements IPermissionResolver {
    @Override
    public List<Permission> resolveAppPermissions() {
        return Arrays.asList(
                FronteggPermissionEnum.AUDITS.with(
                        PermissionActionEnum.READ,
                        PermissionActionEnum.STATS
                ),

                FronteggPermissionEnum.TENANTS.with(
                        PermissionActionEnum.ALL
                )
        );
    }
}
