# Dockers - DBMS: Preset Data

The **DBMS** docker helps developer testing the JOSP Project components, so when
it's started, it loads Preset Data (DBMS's users, database schemas, tables and
data).<br>
The DBMS's Dockerfile loads Preset Data on container startup from the
```src/dockers/dbms/docker/sql``` dir.

Presetdata contains following DBs:
* [jcp_auth](auth_presetdata.md)
* ```jcp_apis```
* ```test_db```


## jcp_apis

TODO: move this paragraph to ../jcpAPIS/presetdata.md

The ```jcp_apis``` DB is the main DB for the JOSP cloud and it's used by the
[JCP APIs](../jcpAPIs/README.md) and [JOSP GWs](../jospGWs/README.md) componets.
Each client component initialize required tables and no preset data are loaded
by default.<br>
Only a preset user is loaded:

**DBMS's User:**<br>
  Username: ```jcp_apis``` and Password: ```apis_jcp```


## test_db

This is the test DB that verify the preset data are working correctly. It loads
only a single user:

**DBMS's User:**<br>
  Username: ```test_db``` and Password: ```db_test```
