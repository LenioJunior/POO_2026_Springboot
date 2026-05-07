#!/bin/bash

/opt/mssql/bin/sqlservr &

echo "Iniciando SQL Server..."

/usr/config/wait-for-sql.sh

echo "Espera pelo SQL Server encerrada."

# verifica se banco já existe
DB_EXISTS=$(/opt/mssql-tools18/bin/sqlcmd \
    -S localhost \
    -U sa \
    -P "$MSSQL_SA_PASSWORD" \
    -Q "SET NOCOUNT ON; SELECT COUNT(*) FROM sys.databases WHERE name='TestDB'" \
    -C -h -1 -W)

if [ "$DB_EXISTS" == "0" ]; then

    echo "Banco não encontrado. Executando scripts..."

    for script in /usr/config/sql/*.sql
    do
        echo "Executando $script"
        /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -C -i "$script"
    done

    echo "Inicialização concluída."

else
    echo "Banco já existe. Pulando inicialização."
fi

wait