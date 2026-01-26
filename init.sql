IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'bank')
BEGIN
    CREATE DATABASE bank;
END;
