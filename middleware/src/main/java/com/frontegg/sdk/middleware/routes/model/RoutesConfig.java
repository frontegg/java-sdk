package com.frontegg.sdk.middleware.routes.model;

import java.util.List;

public class RoutesConfig {

    private List<VendorClientPublicRouts> vendorClientPublicRoutes;


    public List<VendorClientPublicRouts> getVendorClientPublicRoutes() {
        return vendorClientPublicRoutes;
    }

    public void setVendorClientPublicRoutes(List<VendorClientPublicRouts> vendorClientPublicRoutes) {
        this.vendorClientPublicRoutes = vendorClientPublicRoutes;
    }
}
