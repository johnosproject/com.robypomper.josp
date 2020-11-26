# Dockers - DBMS: Changelog

**v. 2.0.0:**<br>
  * Setup databases ```jcp_auth``` for the [Auth](auth.md) component 
  * Setup databases ```jco_apis``` for the [JCP APIs](../jcpAPIs/README.md) and
    [JOSP GWs](../jospGWs/README.md) components
  * Auto-load on docker init, the preset data from ```src/dockers/dbms/docker/sql```
  * Use MySQL configs from ```src/dockers/dbms/docker/configs``` (actually no
    custom configs used)
  * Databases's data are persistence among DBMS execution and stored in
    ```envs/dockers/dbms/mysql/data:/var/lib/mysql```
