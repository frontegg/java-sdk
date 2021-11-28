<p align="center">
  <a href="https://www.frontegg.com/" rel="noopener" target="_blank">
    <img style="margin-top:40px" height="50" src="https://frontegg.com/wp-content/uploads/2020/04/logo_frrontegg.svg" alt="Frontegg logo">
  </a>
</p>
<div align="center"></div>

## Adding frontegg's middleware to your application

To boot frontegg's spring integration just add the following to your `pom.xml` file:

```xml
<dependency>
	<groupId>com.frontegg.sdk.spring</groupId>
	<artifactId>spring-middleware</artifactId>
	<version>${version.frontegg}</version>
</dependency>
```

Alternatively, an autoconfiguration dependency is provided. Configuration will be read from a standard spring `application.yml`. Just add the following to `pom.xml`:

```xml
<dependency>
    <groupId>com.frontegg.sdk.spring</groupId>
    <artifactId>spring-autoconfigure</artifactId>
    <version>${version.frontegg}</version>
</dependency>
```

> No need to add spring-middleware as it is provided by this dependency

### Context resolving

In order to use frontegg's services, a tenant id and user is must be provided to the sdk:

```java
@Component
public class MyContextResolver implements FronteggContextResolver
{
	@Override
	public FronteggContext resolveContext(HttpServletRequest httpServletRequest)
	{
		return new FronteggContext("my-tenant", "my-user");
	}
}
```

A pluggable context resolver is provided as a second dependency, to be used with frontegg's identity services, but you can create one of your own in case that you already have an identity provider.

> Only one resolver is available in each application! Don't forget to remove `MyContextResolver` first

```xml
<dependency>
    <groupId>com.frontegg.sdk.spring</groupId>
    <artifactId>spring-frontegg-identity-context-resolver</artifactId>
    <version>${version.frontegg}</version>
</dependency>
```

If you're using frontegg's UI componenets (react/angular) you'd probably need to handle CORS headers and maybe

## Overriding options

In order to change the base endpoint for frontegg you need to create/override `FronteggFilter` default path (`/frontegg`):

```java
@Configuration
class MyAppConfiguration {
    @Bean
    public FronteggFilter fronteggFilter(FronteggAuthenticationService authenticationService,
                                         IFronteggRouteService fronteggRouteService,
                                         FronteggServiceDelegate fronteggServiceDelegate,
                                         FronteggOptions options) {
        Assert.notNull(authenticationService, "authenticationService cannot be null");
        Assert.notNull(fronteggRouteService, "fronteggRouteService cannot be null");
        Assert.notNull(fronteggServiceDelegate, "delegate cannot be null");
        Assert.notNull(options, "frontegg options cannot be null");

        return new FronteggFilter("my-path", authenticationService, fronteggRouteService, fronteggServiceDelegate, options);
    }
}
```

To override FronteggOptions in your configuration class define your own bean

```java
@Configuration
class MyAppConfiguration {
    @Bean
    public FronteggOptions fronteggOptions() {
        FronteggOptions fronteggOptions = new FronteggOptions();
        fronteggOptions.setMaxRetries(maxRetries);
        fronteggOptions.setDisableCors(disableCors);
        fronteggOptions.setCookieDomainRewrite(cookieDomainRewrite);
        fronteggOptions.setClientId(clientID);
        fronteggOptions.setApiKey(apiKey);
        return fronteggOptions;
    }
}
```

all frontegg specific properties can be defined in the `application.yml` file as follows:

```yaml
frontegg:
    clientId: { your client id }
    apiKey: { your api key }
    basePath: /frontegg
    settings:
        disableCors: false
        maxRetries: 3
        cookieDomainRewrite:
```

### configuration options

| Option Name             |  Type   | Description                                                       |
| ----------------------- | :-----: | ----------------------------------------------------------------- |
| **maxRetries**          |   int   | Maximum number of retries in case            |
| **disableCors**         | boolean | if cors enabled adds frontegg api appropriate headers to response |
| **cookieDomainRewrite** | string  | overrides cookies domain                                    |


#### Frontegg Urls

Frontegg Urls configuration is defined in config modules readme file.



example of yaml

```yaml
frontegg:
    config:
        urls:
            baseUrl: https://api.frontegg.com
            authenticationService: /vendors/auth/token
            auditsService: /audits/
            notificationService: /notification/
            tenantsService: /tenants/
            metadataService: /metadata/
            teamService: /team
            eventService: /event
            identityService: /identity
```
