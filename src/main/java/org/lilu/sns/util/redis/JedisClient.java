package org.lilu.sns.util.redis;

/**
 * @Auther: lilu
 * @Date: 2019/2/21
 * @Description: 获取redis集群的jedis连接
 */
// @Component
//public class JedisClient implements RedisClientAdapter {

//    @Value("${spring.redis.cluster.nodes}")
//    private String clusterNodes;
//
//    @Override
//    @Bean
//    public <T> T getRedisClusterClient() {
//        String[] nodesArray = clusterNodes.split(",");
//        Set<HostAndPort> nodes = new HashSet<>();
//        for (String node : nodesArray) {
//            String[] hp = node.split(":");
//            nodes.add(new HostAndPort(hp[0],Integer.parseInt(hp[1])));
//        }
//        // 创建redis集群连接池对象并返回
//        return (T) new JedisCluster(nodes);
//    }
// }