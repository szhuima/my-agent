-- 如果数据库不存在，先创建数据库
-- 注意：CREATE DATABASE 不能在已经连接该数据库时执行
-- 所以通常分两步执行
-- 1) 连接到 postgres 或其他管理库
-- 2) 创建数据库（如果不存在）

-- 连接到 postgres（默认库）
\c postgres;

-- 创建数据库 springai（如果不存在）
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'springai') THEN
            CREATE DATABASE springai;
        END IF;
    END
$$;

-- 切换到目标数据库 springai
\c springai;

-- 安装 pgvector 扩展（如果还没安装）
CREATE EXTENSION IF NOT EXISTS vector;

-- 查询表
-- SELECT * FROM information_schema.tables;

-- 删除旧表
DROP TABLE IF EXISTS public.vector_store_768;

-- 创建新表
CREATE TABLE public.vector_store_768
(
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content   TEXT NOT NULL,
    metadata  JSONB,
    embedding VECTOR(768)
);
