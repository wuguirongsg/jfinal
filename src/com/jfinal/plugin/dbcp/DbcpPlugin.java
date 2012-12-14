package com.jfinal.plugin.dbcp;

import com.jfinal.kit.StringKit;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * User: wuguirongsg
 * Date: 12-12-12
 * Time: 下午3:18
 */
public class DbcpPlugin implements IPlugin, IDataSourceProvider {

    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass = "com.mysql.jdbc.Driver";
    private int maxActive = 100;
    private int maxIdle = 20;
    private int minIdle = 10;
    private int initialPoolSize = 10;
    private long minEvictableIdleTimeMillis =  3600000;
    private long timeBetweenEvictionRunsMillis = 1800000;


    private BasicDataSource dataSource;

    public DbcpPlugin setDriverClass(String driverClass) {
        if (StringKit.isBlank(driverClass))
            throw new IllegalArgumentException("driverClass can not be blank.");
        this.driverClass = driverClass;
        return this;
    }

    public DbcpPlugin(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
    }

    public DbcpPlugin(String jdbcUrl, String user, String password, String driverClass) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
        this.driverClass = driverClass != null ? driverClass : this.driverClass;
    }

    public DbcpPlugin(String jdbcUrl, String user, String password, String driverClass, Integer maxActive, Integer maxIdle, Integer minIdle, Integer initialPoolSize, Long minEvictableIdleTimeMillis, Long timeBetweenEvictionRunsMillis) {
        initDbcpProperties(jdbcUrl, user, password, driverClass, maxActive, maxIdle, minIdle, initialPoolSize, minEvictableIdleTimeMillis, timeBetweenEvictionRunsMillis);
    }

    private void initDbcpProperties(String jdbcUrl, String user, String password, String driverClass, Integer maxActive, Integer maxIdle,Integer minIdle, Integer initialPoolSize, Long minEvictableIdleTimeMillis, Long timeBetweenEvictionRunsMillis) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
        this.driverClass = driverClass != null ? driverClass : this.driverClass;
        this.maxActive = maxActive != null ? maxActive : this.maxActive;
        this.maxIdle = maxIdle != null ? maxIdle : this.maxIdle;
        this.minIdle = minIdle != null ? minIdle : this.minIdle;
        this.initialPoolSize = initialPoolSize != null ? initialPoolSize : this.initialPoolSize;
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis != null ? minEvictableIdleTimeMillis : this.minEvictableIdleTimeMillis;
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis != null ? timeBetweenEvictionRunsMillis : this.timeBetweenEvictionRunsMillis;
    }

    public DbcpPlugin(File propertyfile) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(propertyfile);
            Properties ps = new Properties();
            ps.load(fis);

            initDbcpProperties(ps.getProperty("jdbcUrl"), ps.getProperty("user"), ps.getProperty("password"), ps.getProperty("driverClass"),
                    toInt(ps.getProperty("maxActive")), toInt(ps.getProperty("maxIdle")),toInt(ps.getProperty("minIdle")), toInt(ps.getProperty("initialPoolSize")),
                    toLong(ps.getProperty("minEvictableIdleTimeMillis")),toLong(ps.getProperty("timeBetweenEvictionRunsMillis")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (fis != null)
                try {fis.close();} catch (IOException e) {e.printStackTrace();}
        }
    }

    public DbcpPlugin(Properties properties) {
        Properties ps = properties;
        initDbcpProperties(ps.getProperty("jdbcUrl"), ps.getProperty("user"), ps.getProperty("password"), ps.getProperty("driverClass"),
                toInt(ps.getProperty("maxActive")),  toInt(ps.getProperty("maxIdle")),toInt(ps.getProperty("minIdle")), toInt(ps.getProperty("initialPoolSize")),
                toLong(ps.getProperty("minEvictableIdleTimeMillis")),toLong(ps.getProperty("timeBetweenEvictionRunsMillis")));
    }

    public boolean start() {
        dataSource = new BasicDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClass);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMinIdle(minIdle);
        dataSource.setInitialSize(initialPoolSize);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

        return true;
    }

    private Integer toInt(String str) {
        return Integer.parseInt(str);
    }
    
    private Long toLong(String str){
    	return Long.parseLong(str);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public boolean stop() {
        if (dataSource != null)
            try {
                dataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return true;
    }
}
