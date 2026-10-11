
IF OBJECT_ID(N'dbo.gateway_users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.gateway_users (
        id BIGINT IDENTITY(1,1) NOT NULL,
        username NVARCHAR(255) NOT NULL,
        password NVARCHAR(255) NOT NULL,
        first_name NVARCHAR(255) NULL,
        last_name NVARCHAR(255) NULL,
        enabled BIT NOT NULL CONSTRAINT df_gateway_users_enabled DEFAULT 1,
        CONSTRAINT pk_gateway_users PRIMARY KEY (id)
    );
END $$

IF NOT EXISTS (SELECT 1 FROM gateway_users)
BEGIN
insert into gateway_users values ('admin', '$2a$10$7fu8eaa0/ZsRD9rcblQqx.7j6Q3mxygLUkOzZ3KZ5q6YJ8qTq74Qi', 'admin', 'admin' ,1)
END $$

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'uk_gateway_users_username' AND object_id = OBJECT_ID(N'dbo.gateway_users'))
BEGIN
    CREATE UNIQUE INDEX uk_gateway_users_username ON dbo.gateway_users(username);
END $$

IF OBJECT_ID(N'dbo.gateway_route', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.gateway_route (
        id BIGINT IDENTITY(1,1) NOT NULL,
        route_id NVARCHAR(100) NOT NULL,
        route_name NVARCHAR(200) NOT NULL,
        uri NVARCHAR(1000) NOT NULL,
        enabled BIT NOT NULL,
        route_order INT NOT NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        register_user NVARCHAR(500) NOT NULL,
        CONSTRAINT pk_gateway_route PRIMARY KEY (id),
        CONSTRAINT uk_gateway_route_route_id UNIQUE (route_id)
    );
END $$

IF OBJECT_ID(N'dbo.gateway_route_predicate', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.gateway_route_predicate (
        id BIGINT IDENTITY(1,1) NOT NULL,
        route_id BIGINT NOT NULL,
        predicate_type NVARCHAR(50) NOT NULL,
        predicate_config NVARCHAR(2500) NOT NULL,
        enabled BIT NOT NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        register_user NVARCHAR(500) NOT NULL,
        CONSTRAINT pk_gateway_route_predicate PRIMARY KEY (id),
        CONSTRAINT fk_gateway_route_predicate_route FOREIGN KEY (route_id) REFERENCES dbo.gateway_route(id)
    );
END $$

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'ix_gateway_route_predicate_route' AND object_id = OBJECT_ID(N'dbo.gateway_route_predicate'))
BEGIN
    CREATE INDEX ix_gateway_route_predicate_route ON dbo.gateway_route_predicate(route_id, enabled);
END $$

IF OBJECT_ID(N'dbo.gateway_route_filter', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.gateway_route_filter (
        id BIGINT IDENTITY(1,1) NOT NULL,
        route_id BIGINT NOT NULL,
        filter_type NVARCHAR(50) NOT NULL,
        filter_config NVARCHAR(2500) NOT NULL,
        enabled BIT NOT NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        register_user NVARCHAR(500) NOT NULL,
        CONSTRAINT pk_gateway_route_filter PRIMARY KEY (id),
        CONSTRAINT fk_gateway_route_filter_route FOREIGN KEY (route_id) REFERENCES dbo.gateway_route(id)
    );
END $$

IF OBJECT_ID(N'dbo.gateway_route_rate_limit', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.gateway_route_rate_limit (
        id BIGINT IDENTITY(1,1) NOT NULL,
        route_id BIGINT NOT NULL,
        period BIGINT NOT NULL,
        capacity BIGINT NOT NULL,
        enabled BIT NOT NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        register_user NVARCHAR(500) NOT NULL,
        CONSTRAINT pk_gateway_route_rate_limit PRIMARY KEY (id),
        CONSTRAINT uk_gateway_route_rate_limit_route UNIQUE (route_id),
        CONSTRAINT fk_gateway_route_rate_limit_route FOREIGN KEY (route_id) REFERENCES dbo.gateway_route(id)
    );
END $$

IF OBJECT_ID(N'dbo.gateway_route_retry', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.gateway_route_retry (
        id BIGINT IDENTITY(1,1) NOT NULL,
        route_id BIGINT NOT NULL,
        http_method NVARCHAR(100) NOT NULL,
        retry_count INT NOT NULL,
        enabled BIT NOT NULL,
        first_backoff_S BIGINT NOT NULL,
        max_backoff_S BIGINT NOT NULL,
        backoff_factor INT NOT NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        register_user NVARCHAR(500) NOT NULL,
        CONSTRAINT pk_gateway_route_retry PRIMARY KEY (id),
        CONSTRAINT uk_gateway_route_retry_route UNIQUE (route_id),
        CONSTRAINT fk_gateway_route_retry_route FOREIGN KEY (route_id) REFERENCES dbo.gateway_route(id)
    );
END $$

IF OBJECT_ID(N'dbo.gateway_route_circuit_breaker', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.gateway_route_circuit_breaker (
        id BIGINT IDENTITY(1,1) NOT NULL,
        route_id BIGINT NOT NULL,
        enabled BIT NOT NULL,
        minimum_number_of_calls INT NOT NULL,
        sliding_window_size INT NOT NULL,
        failure_rate_threshold FLOAT NOT NULL,
        wait_duration_in_open_state_seconds BIGINT NOT NULL,
        permitted_number_of_calls_in_half_open_state INT NOT NULL,
        timeOut_in_seconds INT NOT NULL,
        http_status NVARCHAR(500) NOT NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        register_user NVARCHAR(500) NOT NULL,
        fall_back_uri NVARCHAR(500) NULL,
        CONSTRAINT pk_gateway_route_circuit_breaker PRIMARY KEY (id),
        CONSTRAINT uk_gateway_route_circuit_breaker_route UNIQUE (route_id),
        CONSTRAINT fk_gateway_route_circuit_breaker_route FOREIGN KEY (route_id) REFERENCES dbo.gateway_route(id)
    );
END $$

IF OBJECT_ID(N'dbo.gateway_blacklist', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.gateway_blacklist (
        id BIGINT IDENTITY(1,1) NOT NULL,
        ip NVARCHAR(45) NOT NULL,
        enabled BIT NOT NULL,
        description NVARCHAR(500) NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        register_user NVARCHAR(500) NOT NULL,
        CONSTRAINT pk_gateway_blacklist PRIMARY KEY (id),
        CONSTRAINT uk_gateway_blacklist_ip UNIQUE (ip)
    );
    END $$

IF OBJECT_ID(N'dbo.gateway_trusted_proxy', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.gateway_trusted_proxy (
        id BIGINT IDENTITY(1,1) NOT NULL,
        pattern NVARCHAR(255) NOT NULL,
        enabled BIT NOT NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        register_user NVARCHAR(500) NOT NULL,
        CONSTRAINT pk_gateway_trusted_proxy PRIMARY KEY (id)
    );
    END $$

IF OBJECT_ID(N'dbo.gateway_cors_settings', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.gateway_cors_settings (
        id BIGINT IDENTITY(1,1) NOT NULL,
        enabled BIT NOT NULL,
        allowed_origins NVARCHAR(4000) NOT NULL,
        allowed_methods NVARCHAR(1000) NOT NULL,
        allowed_headers NVARCHAR(4000) NOT NULL,
        exposed_headers NVARCHAR(4000) NOT NULL,
        allow_credentials BIT NOT NULL,
        max_age BIGINT NOT NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        register_user NVARCHAR(500) NOT NULL,
        CONSTRAINT pk_gateway_cors_settings PRIMARY KEY (id)
    );
    END $$

IF OBJECT_ID(N'dbo.gateway_route', N'U') IS NOT NULL
   AND COL_LENGTH(N'dbo.gateway_route', N'register_user') IS NULL
BEGIN
    ALTER TABLE dbo.gateway_route
        ADD register_user NVARCHAR(500) NOT NULL
            CONSTRAINT df_gateway_route_register_user DEFAULT N'admin' WITH VALUES;
END $$

IF OBJECT_ID(N'dbo.gateway_route_predicate', N'U') IS NOT NULL
   AND COL_LENGTH(N'dbo.gateway_route_predicate', N'register_user') IS NULL
BEGIN
    ALTER TABLE dbo.gateway_route_predicate
        ADD register_user NVARCHAR(500) NOT NULL
            CONSTRAINT df_gateway_route_predicate_register_user DEFAULT N'admin' WITH VALUES;
END $$

IF OBJECT_ID(N'dbo.gateway_route_filter', N'U') IS NOT NULL
   AND COL_LENGTH(N'dbo.gateway_route_filter', N'register_user') IS NULL
BEGIN
    ALTER TABLE dbo.gateway_route_filter
        ADD register_user NVARCHAR(500) NOT NULL
            CONSTRAINT df_gateway_route_filter_register_user DEFAULT N'admin' WITH VALUES;
END $$

IF OBJECT_ID(N'dbo.gateway_route_rate_limit', N'U') IS NOT NULL
   AND COL_LENGTH(N'dbo.gateway_route_rate_limit', N'register_user') IS NULL
BEGIN
    ALTER TABLE dbo.gateway_route_rate_limit
        ADD register_user NVARCHAR(500) NOT NULL
            CONSTRAINT df_gateway_route_rate_limit_register_user DEFAULT N'admin' WITH VALUES;
END $$

IF OBJECT_ID(N'dbo.gateway_route_retry', N'U') IS NOT NULL
   AND COL_LENGTH(N'dbo.gateway_route_retry', N'register_user') IS NULL
BEGIN
    ALTER TABLE dbo.gateway_route_retry
        ADD register_user NVARCHAR(500) NOT NULL
            CONSTRAINT df_gateway_route_retry_register_user DEFAULT N'admin' WITH VALUES;
END $$

IF OBJECT_ID(N'dbo.gateway_route_circuit_breaker', N'U') IS NOT NULL
   AND COL_LENGTH(N'dbo.gateway_route_circuit_breaker', N'register_user') IS NULL
BEGIN
    ALTER TABLE dbo.gateway_route_circuit_breaker
        ADD register_user NVARCHAR(500) NOT NULL
            CONSTRAINT df_gateway_route_circuit_breaker_register_user DEFAULT N'admin' WITH VALUES;
END $$

IF OBJECT_ID(N'dbo.gateway_blacklist', N'U') IS NOT NULL
   AND COL_LENGTH(N'dbo.gateway_blacklist', N'created_at') IS NULL
BEGIN
    ALTER TABLE dbo.gateway_blacklist
        ADD created_at DATETIME2 NOT NULL
                CONSTRAINT df_gateway_blacklist_created_at DEFAULT SYSUTCDATETIME() WITH VALUES,
            updated_at DATETIME2 NOT NULL
                CONSTRAINT df_gateway_blacklist_updated_at DEFAULT SYSUTCDATETIME() WITH VALUES;
END $$

IF OBJECT_ID(N'dbo.gateway_blacklist', N'U') IS NOT NULL
   AND COL_LENGTH(N'dbo.gateway_blacklist', N'register_user') IS NULL
BEGIN
    ALTER TABLE dbo.gateway_blacklist
        ADD register_user NVARCHAR(500) NOT NULL
            CONSTRAINT df_gateway_blacklist_register_user DEFAULT N'admin' WITH VALUES;
END $$

IF OBJECT_ID(N'dbo.gateway_trusted_proxy', N'U') IS NOT NULL
   AND COL_LENGTH(N'dbo.gateway_trusted_proxy', N'register_user') IS NULL
BEGIN
    ALTER TABLE dbo.gateway_trusted_proxy
        ADD register_user NVARCHAR(500) NOT NULL
            CONSTRAINT df_gateway_trusted_proxy_register_user DEFAULT N'admin' WITH VALUES;
END $$

IF OBJECT_ID(N'dbo.gateway_cors_settings', N'U') IS NOT NULL
   AND COL_LENGTH(N'dbo.gateway_cors_settings', N'register_user') IS NULL
BEGIN
    ALTER TABLE dbo.gateway_cors_settings
        ADD register_user NVARCHAR(500) NOT NULL
            CONSTRAINT df_gateway_cors_settings_register_user DEFAULT N'admin' WITH VALUES;
END $$
