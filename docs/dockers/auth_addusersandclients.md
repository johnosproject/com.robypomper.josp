# Dockers - Auth: Add users and clients

The **Auth**'s users and clients can be added via the KeyCloak Web UI at
[https://localhost:8998/auth/admin/master/console/#/realms/jcp](https://localhost:8998/auth/admin/master/console/#/realms/jcp)
(default admin credentials: ```admin/password```).

## Add user

Registered users can log in to the JOSP Platform from any JSL service.

1. Go to "Users" section of the KeyCloak admin Web UI and click "Add user" button
1. Fill form fields with user details then save them
1. Open the "Credentials" tab of registered user
1. Set user's password and disable "Temporary" option and click on "Reset password"

Optionally, user can set JOSP platform administrator:

1. Go to "Users" section and click on created user (if not visible click the button "View all users")
1. Open the "Role Mappings" tab of selected user
1. Add the "mng" role to current user, KeyCloak auto-save added role


## Add client

Registered clients can authenticate them self as JOSP objects or services
depending on the assigned role. Assign "obj" role for JOSP object's and "srv"
role for JOSP services.

**JOSP Object Client registration:**<br>
  1. Go to "Clients" section of the KeyCloak admin Web UI and click "Create" button
  1. Set the client's id and check "openid-connect" protocol is selected
  1. Clikc con "Save" button
  1. On "Settings" tab of created client set the "Access type" as "confidential",
    disable the "Standard flow enabled" option and enable the "Service Accounts
    Enabled", then save changes
  1. Open the "Service Account Roles" tab and add the "obj" role to current
    client, KeyCloak auto-save added role
  1. Open the "Credentials" tab of registered client, here you can find the client's
    secret to use in the JOSP object configuration

**JOSP Service Client registration:**<br>
  1. Go to "Clients" section of the KeyCloak admin Web UI and click "Create" button
  1. Set the client's id, check "openid-connect" protocol is selected and set the
    client's root url*
  1. Click con "Save" button
  1. On "Settings" tab of created client set the "Access type" as "confidential",
    enable the "Standard flow enabled" and the "Service Accounts Enabled", then
    save changes
  1. Open the "Mappers" tab and click on "Create" button
  1. Set the Protocol Mapper name as "Srv Role", select mapper type to "Hardcoded
     Role" and set the role field with "srv", then click on "Save" button
  1. Open the "Credentials" tab of registered client, here you can find the client's
    secret to use in the JOSP service configuration

\* client's root url is used when the client is a web service. So authentication
process can run the user to KeyCloak web site and, once the user logged in, bring
back the user to the service's url.
The client's root url must be always set also to the JOSP service configurations.
