FROM mcr.microsoft.com/mssql/server:2022-latest

USER root

WORKDIR /usr/config

#COPY sql /usr/config/sql
#COPY entrypoint.sh /usr/config/
#COPY wait-for-sql.sh /usr/config/

#RUN chmod +x /usr/config/*.sh

ENTRYPOINT ["/opt/mssql/bin/sqlservr"]