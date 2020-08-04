# JOSP JSL - Shell's commands

TODO: to describe shell commands

## JSL

* jc    jsl-connect     ()
* jd    jsl-disconnect  ()
* js    jsl-status      ()


## JSL Info

* is    info-srv    ()


## JCP APIs Connection

* jcs   jcp-client-status       ()
* jcc   jcp-client-connect      ()
* jcd   jcp-client-disconnect   ()


## JSL User login

* jul       jcp-user-login  ()
* jcuslo    jcp-user-logout ()


## JSL communications

**Local**

* cls       comm-local-status                   ()
* colost    comm-local-start                    ()
* -         comm-local-stop                     ()
* cpalc     comm-print-all-local-connections    ()

**Cloud**

* ccs   comm-cloud-status       ()
* ccc   comm-cloud-connect      ()
* ccd   comm-cloud-disconnect   ()


### Example: Enable/Disable local communication

**On JSL Shell**

```
JSL$ comm-local-status
Local communication discovery system is running
JSL$ comm-local-stop
Local communication discovery system stopped successfully.
JSL$ comm-local-status
Local communication discovery system is NOT running
JSL$ comm-local-start
Local communication discovery system started successfully.
JSL$ comm-local-status
Local communication discovery system is running
```


### Example: Enable/Disable cloud communication

**On JSL Shell**

```
…
```


## JSL ObjsMngr

### List objects

* opa   objs-print-all              ()
* opac  objs-print-all-connected    ()


## Object's interactions

* oson  obj-set-object-name     (p1, p2)
* osoo  obj-set-object-owner    (p1, p2)


### Object structure and status

* opoi  obj-print-object-info           (p1)
* opos  obj-print-object-struct         (p1)
* opoc  obj-print-object-connections    (p1)
* opop  obj-print-object-permissions    (p1)


### Print obejct's component status

* os        obj-status                  (p1, p2)


### Send action commands

* oabs      obj-action-boolean-switch   (p1, p2)
* oabt      obj-action-boolean-true     (p1, p2)
* oabf      obj-action-boolean-false    (p1, p2)
* oar       obj-action-range            (p1, p2, p3)
* oari      obj-action-range-increase   (p1, p2)
* oard      obj-action-range-decrease   (p1, p2)
* oarm      obj-action-range-max        (p1, p2)
* obacrami  obj-action-range-min        (p1, p2)


#### Example: Send Boolean Action Command

**On JSL Shell**

```
JSL$ opa									# objs-print-all
KNOWN OBJECTS LIST
- Raspberry_80                   (BSNJJ-HSLFO-OKWSN)

JSL$ opos BSNJJ-HSLFO-OKWSN			# obj-print-object-struct {OBJ_ID}
- root                         Container       
  - BoolAction                 BooleanAction   false
  - BoolState                  BooleanState    false
  - RangeState                 RangeState      0.0
  - RangeAction                RangeAction     0.0

JSL$ oabs BSNJJ-HSLFO-OKWSN BoolAction	# obj-action-boolean-switch {OBJ_ID} {COMP_NAME}

JSL$ opos BSNJJ-HSLFO-OKWSN			# obj-print-object-struct {OBJ_ID}
- root                         Container       
  - BoolAction                 BooleanAction   true
  - BoolState                  BooleanState    false
  - RangeState                 RangeState      0.0
  - RangeAction                RangeAction     0.0
```


#### Example: Send Range Action Command

**On JSL Shell**

```
JSL$ opos BSNJJ-HSLFO-OKWSN			# obj-print-object-struct {OBJ_ID}
- root                         Container       
  - BoolAction                 BooleanAction   false
  - BoolState                  BooleanState    false
  - RangeState                 RangeState      0.0
  - RangeAction                RangeAction     0.0

JSL$ oar BSNJJ-HSLFO-OKWSN BoolAction 1.23	# obj-action-boolean-switch {OBJ_ID} {COMP_NAME}

JSL$ opos BSNJJ-HSLFO-OKWSN			# obj-print-object-struct {OBJ_ID}
- root                         Container       
  - BoolAction                 BooleanAction   true
  - BoolState                  BooleanState    false
  - RangeState                 RangeState      0.0
  - RangeAction                RangeAction     1.23
```


## Object's Permissions

* oap   obj-add-perm    (p1, p2, p3, p4, p5)
* oup   obj-upd-perm    (p1, p2, p3, p4, p5, p6)
* orp   obj-rem-perm    (p1, p2)


## JSL Listeners

* omal  objs-mngr-add-listeners     ()
* ocal  objs-comm-add-listeners     ()
* oal   obj-add-listeners           (p1)
* ocal  obj-component-add-listeners (p1, p2)
