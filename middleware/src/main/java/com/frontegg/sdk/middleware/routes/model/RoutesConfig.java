package com.frontegg.sdk.middleware.routes.model;

import java.util.List;

public class RoutesConfig {

    private List<VendorClientPublicRoutes> vendorClientPublicRoutes;


    public List<VendorClientPublicRoutes> getVendorClientPublicRoutes()
    {
        return this.vendorClientPublicRoutes;
    }

    public void setVendorClientPublicRoutes(List<VendorClientPublicRoutes> vendorClientPublicRoutes)
    {
        this.vendorClientPublicRoutes = vendorClientPublicRoutes;
    }
}
