# config

Config module is designed to have ability to load FrontEgg specific configuration from different sources.


#### FrontEgg Urls

| Option Name       | Type   | Default Value 
|-------------------|:---:|---|
| **baseUrl**      | string | https://api.frontegg.com | 
| **authenticationService**  | string | https://api.frontegg.com/vendors/auth/token |
| **auditsService**   | string | https://api.frontegg.com/audits/ |
| **notificationService**   | string | https://api.frontegg.com/notification/ | 
| **tenantsService**   | string | https://api.frontegg.com/tenants/ | 
| **metadataService**   | string | https://api.frontegg.com/metadata/ | 
| **teamService**   | string | https://api.frontegg.com/team | 
| **identityService**   | string | https://api.frontegg.com/identity | 


#### Whitelist Url

`List<String> urls` will be populated from middleware with appropriate `ConfigurationProviderChain`

#### Custom Provider
There is also ability to provide your custom configuration providers.

```java

public class CustomConfigProvider implements ConfigProvider {
    @Override
    public FronteggConfig resolveConfigs() {
        ....
    }

```

