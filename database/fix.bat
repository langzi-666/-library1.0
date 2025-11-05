@echo off
cd /d %~dp0
echo 正在修复数据库表结构...
echo 请输入 MySQL root 密码：
mysql -u root -p library1.4 < fix_update_time_fields.sql
echo.
echo 修复完成！
pause

