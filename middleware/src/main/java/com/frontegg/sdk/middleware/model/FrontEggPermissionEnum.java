package com.frontegg.sdk.middleware.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum FrontEggPermissionEnum {

    ALL (new ActionPermissionDefinition[]{
            new ActionPermissionDefinition(
                    PermissionActionEnum.ALL,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.ALL, "*")
                    }
            )
    }),

    AUDITS (new ActionPermissionDefinition[]{
            new ActionPermissionDefinition(
                    PermissionActionEnum.ALL,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.ALL, "/audits")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.STATS,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.GET, "/audits")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.EXPORT,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.POST, "/audits/export/pdf"),
                            new PermissionDefinition(RequestMethodEnum.POST, "/audits/export/csv")
                    }
            )
    }),



    NOTIFICATIONS (new ActionPermissionDefinition[]{
            new ActionPermissionDefinition(
                    PermissionActionEnum.ALL,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.ALL, "/notification")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.READ,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.GET, "/notification")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.CHANGE_STATUS,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.PUT, "/notification/status")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.PIN,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.PUT, "/notification/pin")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.UNPIN,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.PUT, "/notification/unpin")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.WEB_PUSH,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.PUT, "/notification/subscriptions/webpush")
                    }
            )
    }),



    TENANTS (new ActionPermissionDefinition[]{
            new ActionPermissionDefinition(
                    PermissionActionEnum.ALL,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.ALL, "/tenants")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.READ,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.GET, "/tenants")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.ADD,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.POST, "/tenants")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.UPDATE,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.PATCH, "/tenants")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.REPLACE,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.PUT, "/tenants")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.DELETE,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.DELETE, "/tenants")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.READ_KEY_HISTORY,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.GET, "/tenants/history")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.ADD_TENANT_EVENT,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.POST, "/tenants/events")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.READ_TENANT_EVENT,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.GET, "/tenants/events")
                    }
            ),
    }),



    TEAM ( new ActionPermissionDefinition[] {
            new ActionPermissionDefinition(
                    PermissionActionEnum.ALL,
                    new PermissionDefinition[]{
                        new PermissionDefinition(RequestMethodEnum.ALL, "/team")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.READ,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.GET, "/team"),
                            new PermissionDefinition(RequestMethodEnum.GET, "/team/roles")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.STATS,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.GET, "/team/stats")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.ADD,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.POST, "/team")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.UPDATE,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.PUT, "/team")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.DELETE,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.DELETE, "/team")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.RESEND_ACTIVATION_EMAIL,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.POST, "/team/resendActivationEmail")
                    }
            ),
            new ActionPermissionDefinition(
                    PermissionActionEnum.RESET_PASSWORD,
                    new PermissionDefinition[]{
                            new PermissionDefinition(RequestMethodEnum.POST, "/team/resetPassword")
                    }
            )
        }
    );

    FrontEggPermissionEnum(ActionPermissionDefinition[] permissions) {
        this.permissions = permissions;
    }

    private final ActionPermissionDefinition[] permissions;

    public Permission with(PermissionActionEnum ...actions) {
        Permission permission = new Permission();
        permission.setRootPermission(this.name());
        String[] actionPermissions = new String[actions.length];
        int index = 0;
        for (PermissionActionEnum actionEnum : actions) {
            actionPermissions[index++] = actionEnum.name();
        }
        permission.setActionPermissions(actionPermissions);
        return permission;
    }

    public Map<String, List<String>> getRoutsByOperation(PermissionActionEnum actionEnum) {
        Map<String, List<String>> result = new HashMap<>();
        for (ActionPermissionDefinition actionPermissionDefinition : permissions) {
            if (actionPermissionDefinition.action == actionEnum) {
                for (PermissionDefinition def : actionPermissionDefinition.definitions) {
                    List<String> routList;
                    if (result.containsKey(def.method.name())) {
                        routList = result.get(def.method.name());
                    } else {
                        routList = new ArrayList<>();
                    }
                    routList.add(def.url);
                    result.put(def.method.name(), routList);
                }
            }
        }
        return result;
    }

    final static class ActionPermissionDefinition {

        public ActionPermissionDefinition(PermissionActionEnum action, PermissionDefinition[] definitions) {
            this.action = action;
            this.definitions = definitions;
        }

        private final PermissionActionEnum action;
        private final PermissionDefinition[] definitions;

        public PermissionActionEnum getAction() {
            return action;
        }

        public PermissionDefinition[] getDefinitions() {
            return definitions;
        }
    }
    final static class PermissionDefinition {

        PermissionDefinition(RequestMethodEnum method, String url) {
            this.method = method;
            this.url = url;
        }

        private final RequestMethodEnum method;
        private final String url;

        public RequestMethodEnum getMethod() {
            return method;
        }

        public String getUrl() {
            return url;
        }
    }
    enum RequestMethodEnum {
        ALL,
        GET,
        POST,
        PUT,
        DELETE,
        PATCH;
    }
}
