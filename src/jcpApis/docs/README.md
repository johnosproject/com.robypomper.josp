# jcpApis

This project provide the **JCP API's service**.

The JCP API's (aka jcpAPIs) are the main API for the JOSP Cloud Platform and
support Objects, Services and Users from JOSP Eco-System.


## Status

Now it implements a Spring Boot application responding on port 9001 and
expose 2 groups of endpoints:

Methods:
* [http://localhost:9001/apis/str](): test String as return type
* [http://localhost:9001/apis/int](): test int as return type

DBEntity:
* [http://localhost:9001/apis/db](): full list of usernames and ids
* [http://localhost:9001/apis/db/{id}](): username (as Entity) of specified id
* [http://localhost:9001/apis/db/{id}/username](): username (as String) of specified id
* [http://localhost:9001/apis/db/add?username=pippo](): add new username with string 'pippo'
