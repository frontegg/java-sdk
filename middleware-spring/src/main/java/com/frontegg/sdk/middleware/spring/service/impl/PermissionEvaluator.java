package com.frontegg.sdk.middleware.spring.service.impl;

import com.frontegg.sdk.common.exception.InefficientAccessException;
import com.frontegg.sdk.common.util.HttpUtil;
import com.frontegg.sdk.common.util.StringHelper;
import com.frontegg.sdk.config.WhiteListConfig;
import com.frontegg.sdk.middleware.IPermissionEvaluator;
import com.frontegg.sdk.middleware.context.FronteggContext;
import com.frontegg.sdk.middleware.model.Permission;
import com.frontegg.sdk.middleware.model.FrontEggPermissionEnum;
import com.frontegg.sdk.middleware.model.PermissionActionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class PermissionEvaluator implements IPermissionEvaluator {
    private static final Logger logger = LoggerFactory.getLogger(PermissionEvaluator.class);

    public static final String CONTEXT_MAIN_PATH = "/frontegg";

    @Autowired
    private WhiteListConfig whiteListConfig;

    @Override
    public void validatePermissions(HttpServletRequest request, FronteggContext context) {
        List<Permission> permissions = context.getPermissions();

        if (permissions == null ) {
            logger.error("No permissions were passed for frontegg middleware");
            throw new InefficientAccessException("No permissions were passed for frontegg middleware");
        }

        if (permissions.isEmpty()) {
            logger.error("Permissions array is empty for frontegg middleware");
            throw new InefficientAccessException("Permissions array is empty for frontegg middleware");
        }

        // We allow OPTIONS
        if (request.getMethod().equals("OPTIONS")) {
            logger.info("OPTIONS is allowed");
            return;
        }

        String url = HttpUtil.getRequestUrl(request.getRequestURI(), CONTEXT_MAIN_PATH);

        if (isWhiteListUrl(url)) return;

        if (isValidatePermission(url, request.getMethod(), permissions)) return;


        logger.error("No matching permission for "+ request.getMethod() + " " + url + ". Permissions : " + permissions);
        throw new InefficientAccessException("No matching permission for " + request.getMethod() + " " + url);
    }

    private boolean isWhiteListUrl(String url) {
        for (String whiteListed : whiteListConfig.getUrls()) {
            if (url.startsWith(whiteListed)) {
                logger.info("URL " + url + " is whitelisted");
                return true;
            }
        }
        return false;
    }

    private boolean isValidatePermission(String url, String method, List<Permission> permissions) {
        if (includes(permissions, FrontEggPermissionEnum.ALL)) {
            logger.info("User is authorized for ALL actions in the system");
            return true;
        }

        for (Permission permission : permissions) {
            FrontEggPermissionEnum permissionEnum = FrontEggPermissionEnum.valueOf(permission.getRootPermission());

            for (String action : permission.getActionPermissions()) {
                PermissionActionEnum actionEnum = PermissionActionEnum.valueOf(action);
                Map<String, List<String>> methodRoutMap = permissionEnum.getRoutsByOperation(actionEnum);
                List<String> routs = new ArrayList<>();
                for (List<String> lrs : methodRoutMap.values()) {
                    routs.addAll(lrs);
                }


                if (actionEnum == PermissionActionEnum.ALL && StringHelper.startWithAny(routs, url)) {
                    logger.info("All operations are allowed for this user");
                    return true;
                }

                if (methodRoutMap.keySet().contains(method) && methodRoutMap.get(method).contains(url)) {
                    logger.info("User is authorized for " + method + " " + url);
                    return true;
                }

            }
        }

        return false;
    }

    private boolean includes(List<Permission> permissions, FrontEggPermissionEnum permission) {
        return permissions.stream().filter(  p -> permission.name().equals(p.getRootPermission())).findAny().isPresent();
    }
}
