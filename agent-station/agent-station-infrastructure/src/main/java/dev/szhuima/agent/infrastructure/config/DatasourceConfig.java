package dev.szhuima.agent.infrastructure.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.observation.ObservationRegistry;
import jakarta.annotation.Resource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaEmbeddingOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@MapperScan("dev.szhuima.agent.infrastructure.mapper")
@Configuration
public class DatasourceConfig {

    /**
     * 配置 MyBatis 的 SqlSessionFactory
     */
//    @Bean("sqlSessionFactory")
//    public SqlSessionFactoryBean sqlSessionFactory(@Qualifier("mybatisDataSource") DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dataSource);
//
//        // 设置MyBatis配置文件位置
//        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        sqlSessionFactoryBean.setTypeAliasesPackage("dev.szhuima.agent.infrastructure.po");
//
//        sqlSessionFactoryBean.setPlugins(mybatisPlusInterceptor());
//
//        // 设置Mapper XML文件位置
//        Resource[] resources = resolver.getResources("classpath:/mapper/*.xml");
//        sqlSessionFactoryBean.setMapperLocations(resources);
//        return sqlSessionFactoryBean;
//    }


    @Resource
    private MetaObjectHandler handler;

    /**
     * 为 MyBatis 创建主数据源
     */
    @Bean("mybatisDataSource")
    @Primary
    public DataSource mybatisDataSource(@Value("${spring.datasource.driver-class-name}") String driverClassName,
                                        @Value("${spring.datasource.url}") String url,
                                        @Value("${spring.datasource.username}") String username,
                                        @Value("${spring.datasource.password}") String password,
                                        @Value("${spring.datasource.hikari.maximum-pool-size:10}") int maximumPoolSize,
                                        @Value("${spring.datasource.hikari.minimum-idle:5}") int minimumIdle,
                                        @Value("${spring.datasource.hikari.idle-timeout:30000}") long idleTimeout,
                                        @Value("${spring.datasource.hikari.connection-timeout:30000}") long connectionTimeout,
                                        @Value("${spring.datasource.hikari.max-lifetime:1800000}") long maxLifetime,
                                        @Value("${spring.datasource.hikari.validation-timeout}") long validationTimeOut,
                                        @Value("${spring.datasource.hikari.connection-test-query}") String testQuery) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        // 连接池配置
        dataSource.setMaximumPoolSize(maximumPoolSize);
        dataSource.setMinimumIdle(minimumIdle);
        dataSource.setIdleTimeout(idleTimeout);
        dataSource.setConnectionTimeout(connectionTimeout);
        dataSource.setMaxLifetime(maxLifetime);
        dataSource.setValidationTimeout(validationTimeOut);
        dataSource.setConnectionTestQuery(testQuery);
        dataSource.setPoolName("mySQLHikariPool");
        return dataSource;
    }

    /**
     * 配置 MyBatis plus 的 SqlSessionFactory
     */
    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("mybatisDataSource") DataSource dataSource,
                                               MybatisPlusInterceptor mybatisPlusInterceptor) throws Exception {
        MybatisSqlSessionFactoryBean plusSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        plusSessionFactoryBean.setDataSource(dataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        plusSessionFactoryBean.setTypeAliasesPackage("dev.szhuima.agent.infrastructure.po");
        plusSessionFactoryBean.setMapperLocations(resolver.getResources("classpath*:mapper/*.xml"));
        plusSessionFactoryBean.setPlugins(mybatisPlusInterceptor);

        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);
        plusSessionFactoryBean.setConfiguration(configuration);


        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setMetaObjectHandler(handler);
        plusSessionFactoryBean.setGlobalConfig(globalConfig);

        return plusSessionFactoryBean.getObject();
    }

    /**
     * 配置 SqlSessionTemplate
     */
    @Bean("sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 为 PgVector 创建专用的数据源
     */
    @Bean("pgVectorDataSource")
    public DataSource pgVectorDataSource(@Value("${spring.ai.vectorstore.pgvector.datasource.driver-class-name}") String driverClassName,
                                         @Value("${spring.ai.vectorstore.pgvector.datasource.url}") String url,
                                         @Value("${spring.ai.vectorstore.pgvector.datasource.username}") String username,
                                         @Value("${spring.ai.vectorstore.pgvector.datasource.password}") String password,
                                         @Value("${spring.ai.vectorstore.pgvector.datasource.hikari.maximum-pool-size:5}") int maximumPoolSize,
                                         @Value("${spring.ai.vectorstore.pgvector.datasource.hikari.minimum-idle:2}") int minimumIdle,
                                         @Value("${spring.ai.vectorstore.pgvector.datasource.hikari.idle-timeout:30000}") long idleTimeout,
                                         @Value("${spring.ai.vectorstore.pgvector.datasource.hikari.connection-timeout:30000}") long connectionTimeout) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        // 连接池配置
        dataSource.setMaximumPoolSize(maximumPoolSize);
        dataSource.setMinimumIdle(minimumIdle);
        dataSource.setIdleTimeout(idleTimeout);
        dataSource.setConnectionTimeout(connectionTimeout);
        // 确保在启动时连接数据库
//        dataSource.setInitializationFailTimeout(1);  // 设置为1ms，如果连接失败则快速失败
        dataSource.setConnectionTestQuery("SELECT 1"); // 简单的连接测试查询
        dataSource.setAutoCommit(true);
        dataSource.setPoolName("PgVectorHikariPool");
        return dataSource;
    }

    /**
     * 为 PgVector 创建专用的 JdbcTemplate
     */
    @Bean("pgVectorJdbcTemplate")
    public JdbcTemplate pgVectorJdbcTemplate(@Qualifier("pgVectorDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * -- 删除旧的表（如果存在）
     * DROP TABLE IF EXISTS public.vector_store_openai;
     * <p>
     * -- 创建新的表，使用UUID作为主键
     * CREATE TABLE public.vector_store_openai (
     * id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     * content TEXT NOT NULL,
     * metadata JSONB,
     * embedding VECTOR(1536)
     * );
     * <p>
     * SELECT * FROM vector_store_openai
     */
    @Bean("vectorStore")
    public PgVectorStore pgVectorStore(@Value("${spring.ai.ollama.base-url}") String baseUrl,
                                       @Value("${spring.ai.ollama.embedding.model}") String model,
                                       @Qualifier("pgVectorJdbcTemplate") JdbcTemplate jdbcTemplate,
                                       ObservationRegistry observationRegistry) {

        // 1. 构造 Ollama API 客户端
        OllamaApi ollamaApi = OllamaApi.builder()
                .baseUrl(baseUrl)
                .build();

        OllamaEmbeddingOptions options = OllamaEmbeddingOptions.builder()
                .model(model)
                .build();

        ModelManagementOptions modelManagementOptions = ModelManagementOptions.builder().build();

        // 2. 构造 Ollama Embedding 模型（使用本地模型）
        OllamaEmbeddingModel embeddingModel = new OllamaEmbeddingModel(ollamaApi, options, observationRegistry, modelManagementOptions);

        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName("vector_store_768")
                .build();
    }

    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(500)
                .withMinChunkSizeChars(350)
                .withMinChunkLengthToEmbed(20)
                .withMaxNumChunks(10000)
                .withKeepSeparator(true).build();

        return tokenTextSplitter;
    }

}
