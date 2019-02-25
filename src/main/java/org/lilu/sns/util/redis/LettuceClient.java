package org.lilu.sns.util.redis;

/**
 * @Auther: lilu
 * @Date: 2019/2/21
 * @Description: 配置lettuce的集群
 */
// @Component
//public class LettuceClient implements RedisClientAdapter {
//    @Value("${spring.redis.cluster.nodes}")
//    private String clusterNodes;
//
//    @Override
//    public <T> T getRedisClusterClient() {
//        List<RedisURI> redisURIS = new ArrayList<>();
//        String[] nodesArray = clusterNodes.split(",");
//        for (String node : nodesArray) {
//            String[] hp = node.split(":");
//            redisURIS.add(RedisURI.create(hp[0],Integer.parseInt(hp[1])));
//        }
//        // 创建redis集群客户端
//        RedisClusterClient redisClusterClient = RedisClusterClient.create(redisURIS);
//        // 连接到集群
//        StatefulRedisClusterConnection<String,String> clusterConnection = redisClusterClient.connect();
//        // 获取集群同步命令对象
//        RedisClusterCommands<String,String> commands = clusterConnection.sync();
//        return (T) commands;
//    }
//}