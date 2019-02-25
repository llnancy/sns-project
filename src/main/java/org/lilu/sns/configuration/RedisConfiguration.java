package org.lilu.sns.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * @Auther: lilu
 * @Date: 2019/2/20
 * @Description:
 */
@Configuration
public class RedisConfiguration {
    @Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;

    /**
     * 使用Jedis连接redis集群
     * @return
     */
    @Bean
    public JedisCluster getJedisCluster() {
        String[] nodesArray = clusterNodes.split(",");
        Set<HostAndPort> nodes = new HashSet<>();
        for (String node : nodesArray) {
            String[] hp = node.split(":");
            nodes.add(new HostAndPort(hp[0],Integer.parseInt(hp[1])));
        }
        // 创建redis集群连接池对象并返回
        return new JedisCluster(nodes);
    }
}