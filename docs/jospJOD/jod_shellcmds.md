# JOSP JOD - Shell's commands

TODO: to describe shell commands


## JOD

* js    jod-stop    ()
* jost  jod-status  ()
* -     jod-start   ()


## JOD Info

* i     info                    ()
* iu    info-user               ()
* io    info-obj                ()
* ison  info-set-object-name    (String newName)


## JCP APIs Connection

* jcs   jcp-client-status       ()
* jcc   jcp-client-connect      ()
* jcd   jcp-client-disconnect   ()


## JOD Communications

**Local**

* cls       comm-local-status                   ()
* colost    comm-local-start                    ()
* -         comm-local-stop                     ()
* cpalc     comm-print-all-local-connections    ()

**Cloud**

* ccs       comm-cloud-status                   ()
* ccc       comm-cloud-connect                  ()
* ccd       comm-cloud-disconnect               ()


### Example: Enable/Disable local communication

**On JOD Shell**

```
JSL$ comm-local-status
Local communication server is running
JSL$ comm-local-stop
Local communication server stopped successfully.
JSL$ comm-local-status
Local communication server is NOT running
JSL$ comm-local-start
Local communication server started successfully.
JSL$ comm-local-status
Local communication server is running
```


### Example: Enable/Disable cloud communication

**On JOD Shell**

```
…
```


## JOD Structure

* ot	obj-tree        ()
* oc	obj-component   (p1)
* oc	obj-component   ()


## JOD Executors

* ea    exec-activate           ()
* ed    exec-deactivate         ()

**Puller**

* elpp  exec-ls-puller-protos   ()
* elp   exec-ls-pullers         ()
* eap   exec-add-puller         (name, name, proto, configStr)
* erp   exec-rm-puller          (name)
* eep   exec-enable-puller      (name)
* edp   exec-disable-puller     (name)

**Listener**

* ellp  exec-ls-listener-protos ()
* ell   exec-ls-listeners       ()
* eal   exec-add-listener       (type, name, proto, configStr)
* erl   exec-rm-listener        (name)
* eel   exec-enable-listener    (name)
* edl   exec-disable-listener   (name)

**Executor**

* elep  exec-ls-executor-protos ()
* ele   exec-ls-executors       ()
* eae   exec-add-executor       (type, name, proto, configStr)
* ere   exec-rm-executor        (name)
* eee   exec-enable-executor    (name)
* ede   exec-disable-executor   (name)
* eae   exec-action-executor    (name)


## JOD Permissions

* pl    permissions-list        ()
* pu    permission-update       (p1, p2, p3, p4, p5)
* pa    permission-add          (p1, p2, p3, p4)
* pr    permission-remove       (p1)
* pos   permission-owner-set    (p1)
* por   permission-owner-reset  ()
