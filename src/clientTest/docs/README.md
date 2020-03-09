# clientTest

This test application provide an easy and fast **client-server communication
tester (client side)**.

This app manages authentication and requests flow to [jcpAPIs](../../jcpApis/docs/README.md)
according to [Auth](../../jcpApis/auth/README.md) server configurations.


## App mngm
To run the client, gradle project provide two tasks:
* ```./gradlew javaClientTest_ObjRun```
* ```./gradlew javaClientTest_UsrSrvRun```

First one, **Object runnable** simulate the connection from an object using the *Client Credential
Auth Flow* and the ```test-client-obj``` client credentials. This client has
set ```obj``` role in Auth server.

**UsrSrv runnable**, simulate the connection from a service using the *Auth Code
Auth Flow* and the ```test-client-srv``` client credentials. This client has
set ```srv``` role in Auth server. To allow this client to access protected
resources, is required the user. If you login with ```roby/roby``` user, then
you will get also ```mng``` role protected resources. The ```pinco/pallino```
user act as normal user.

The clientTest application allow you to **manage clients credentials** by add/update
credentials on the ```ClientSettingsJcp``` class; or select the credentials to
use during executions setting the ```--clientCredentials``` param of the
clientTest's executables (or gradle tasks).

## App params
```shell script
usage: ClientMain
 -a,--authFlow <arg>            specify which authentication flow to use
 -c,--clientCredentials <arg>   specify which client credential to use
 -e,--env <arg>                 specify which environment settings to use
                                (valid values: test or jcp)
 -r,--disable-ssl-checks        inhibits SSL checks
```
