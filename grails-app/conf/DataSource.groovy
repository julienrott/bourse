dataSource {
    dbCreate = "update"
    pooled = true
	driverClassName = "com.mysql.jdbc.Driver"
    username = "bourse"
    password = "bourse"
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
    development {
        dataSource {
            url = "jdbc:mysql://localhost:3306/bourse?useUnicode=yes&characterEncoding=UTF-8"
        }
    }
    test {
        dataSource {
            url = "jdbc:mysql://localhost:3306/bourse?useUnicode=yes&characterEncoding=UTF-8"
        }
    }
    production {
        dataSource {
            driverClassName = "org.postgresql.Driver"
			username = "bourse"
			password = "bourse"
            url = "jdbc:postgresql://localhost:5432/bourse"
            properties {
               maxActive = -1
               minEvictableIdleTimeMillis=1800000
               timeBetweenEvictionRunsMillis=1800000
               numTestsPerEvictionRun=3
               testOnBorrow=true
               testWhileIdle=true
               testOnReturn=true
               validationQuery="SELECT 1"
            }
        }
    }
}
