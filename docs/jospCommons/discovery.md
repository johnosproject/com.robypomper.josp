# JOSP Commons - Discovery

ToDo

Publisher and discoverer
Implement DiscoverySubSystem





## Check commands

### Avahi

* Browse
```$ avahi-browse -pr _discTest._tcp```

* Resolve
```$ ...```

* Register
```$ ...```

* Kill
```$ ps aux | grep -E “avahi-browse|avahi-publish” | grep '?' | awk '{print $2}' | xargs kill```

* Clean
```$ sudo service avahi-daemon restart```


### DNS-SD

* Browse
```$ dns-sd -B _discTest._tcp```

* Resolve
```$ dns-sd -L "Pinco Pallo Example B" _discTest._tcp.```

* Register
```$ dns-sd -R "My Test" _josp2._tcp. . 515 pdl=application/postscript```

* Kill
```$ ps aux | grep dns-sd | grep '??' | awk '{print $2}' | xargs kill```

* Clean
```
$ dscacheutil -flushcache
$ sudo killall -HUP mDNSResponder
```
