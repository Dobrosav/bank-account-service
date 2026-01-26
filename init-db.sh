#!/bin/bash
set -e

# Start SQL Server in the background
/opt/mssql/bin/sqlservr &
pid=$!

echo "Waiting for SQL Server to start..."
# Wait for SQL Server to be ready. 
# We can check for a specific message in the log, but a simple sleep is often sufficient for local dev.
sleep 15s 

echo "Running setup script..."
# Run the setup script to create the DB and schema
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P "Password123" -d master -i /init.sql
echo "Setup script finished."

# Bring SQL Server to the foreground
wait $pid
