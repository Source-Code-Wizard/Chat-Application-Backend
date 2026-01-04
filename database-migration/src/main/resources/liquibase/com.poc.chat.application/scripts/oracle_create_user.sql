alter session set "_ORACLE_SCRIPT"=true;
create user currency_management_db identified by "currency_management_db";
grant sysdba to currency_management_db;
grant all privileges to currency_management_db;

exit;
