# redis-demo
test redis

- wiki: https://docs.spring.io/spring-data/redis/docs/2.1.0.M1/reference/html/#redis
- 掘金小册: https://juejin.im/book/5afc2e5f6fb9a07a9b362527/section/5b4c19216fb9a04fb8773ed1

- spring boot 整合redis: https://juejin.im/post/5ba0a098f265da0adb30c684

- [spring boot](https://docs.spring.io/spring-boot/docs/1.5.16.RELEASE/reference/htmlsingle/#boot-features-connecting-to-redis)

因为在 springoot 2.x版本中，默认采用的是 Lettuce实现的，

- LettuceConnectionFactory
- JedisConnectionFactory

#### Redis GeoHash
#### Redis Scan
    https://mp.weixin.qq.com/s/ufoLJiXE0wU4Bc7ZbE9cDQ
#### REDIS 持久化 `性能和数据安全`
- redis是单线程IO,AOF同步会影响redis服务器性能.
- redis通过fork函数，对进程的同步redis指令，产生一个子进程，内存像连体婴儿一样共享，持久化完成后分离。
- fork使用COW（copy on write机制）实现
- 一般情况下，redis大部分数据都是冷数据，很少修改，所以叫快照。
- AOP日志存储的是顺序指令序列，redis通过重放AOP日志来恢复redis当前实例的内存数据结构。
- Redis可以通过 bgrewriteaof 将日志瘦身，方式是通过内存数据结构还原优化顺序指令序列。
- AOP日志以文件形式存在,万一在备份的过程中机器挂了，AOP日志还没有刷到磁盘中怎么办？ fsync
- Linx 的glibc提供了fsync 可将指定文件内容强制从内核缓存刷新到磁盘。
- 但是fsync是磁盘IO操作，很影响性能。所以通常只有redis从节点才进行持久化操作，这样不会影响主节点性能。
- 为了防止从节点也挂了，最好是在生产环境做好监控，另外增加一个从节点保证至少有一个从节点。
- redis重启时， 一般是通常通过AOP增量同步加上 rdb备份内容 实现混合持久化。加强性能。
- RDB持久化是指在指定的时间间隔内将内存中的数据集快照写入磁盘，实际操作过程是fork一个子进程，先将数据集写入临时文件，写入成功后，再替换之前的文件，用二进制压缩存储。
- AOF持久化以日志的形式记录服务器所处理的每一个写、删除操作，查询操作不会记录，以文本的方式记录，可以打开文件看到详细的操作记录。

#### REDIS 管道 `客户端批量IO技术`
- redis管道技术其实是个客户端技术。
- redis管道压力测试工具 `redis-benchmark`
- ` redis-benchmark -t set -P 2 -q  -P 它表示单个管道内并行的请求数量`

#### REDIS 事务 `不支持回滚`
- Redis 的事务使用非常简单,它的事务模型很不严格
- 不支持回滚操作是因为redis是先执行指令然后做日志，所以即使发生异常，没有可以用来执行回滚操作的日志。
- Redis 在形式上看起来也差不多，分别是 multi/exec/discard。multi 指示事务的开始，exec 指示事务的执行，discard 指示事务的丢弃。
- 所有的指令在 exec 之前不执行，而是缓存在服务器的一个事务队列中，服务器一旦收到 exec 指令，才开执行整个事务队列，执行完毕后一次性返回所有指令的运行结果。
- 原子性: Redis 的事务根本不能算「原子性」，而仅仅是满足了事务的「隔离性」，隔离性中的串行化——当前执行的事务有着不被其它事务打断的权利。
- discard: 用于丢弃事务缓存队列中的所有指令，在 exec 执行之前
- Redis的客户端在执行事务时都会结合`pipeline`一起使用，这样可以将多次 IO 操作压缩为单次 IO 操作。
- 多个客户端执行事务时对数据造成影响怎么解决？？
- 悲观锁：使用Redis的分布式锁来避免多个客户端会并发进行操作
- 乐观锁： watch 的机制，解决并发修改的方法，执行前盯住 1 个或多个关键变量。
- watch: Redis 禁止在 multi 和 exec 之间执行 watch 指令，而必须在 multi 之前做好盯住关键变量，否则会出错。


#### PubSub `消息队列不能持久化`
- Redis 消息队列的不足之处，那就是它不支持消息的多播机制。
- 消息多播：允许生产者生产一次消息，中间件负责将消息复制到多个消息队列，每个消息队列由相应的消费组进行消费。
- PubSub：PublisherSubscriber 发布者订阅者模型
- Redis PubSub 的生产者和消费者是两个不同Redis连接，不允许连接在 subscribe 等待消息时还要进行其它的操作。
- 在生产环境中，我们很少将生产者和消费者放在同一个线程里。
- redis 提供了模式订阅功能Pattern Subscribe，这样就可以一次订阅多个主题，即使生产者新增加了同模式的主题，消费者也可以立即收到消息
- 缺点1: PubSub 的生产者传递过来一个消息，Redis 会直接找到相应的消费者传递过去。挂掉的消费者重新连上的时候,对它而言是彻底丢失了。
- 缺点2: 如果 Redis 停机重启，PubSub 的消息是不会持久化的，毕竟 Redis 宕机就相当于一个消费者都没有，所有的消息直接被丢弃。
- Disque: 专门用来做多播消息队列,项目目前没有成熟，一直长期处于 Beta 版本.
- Stream: 这个功能给 Redis 带来了持久化消息队列,从此 PubSub 可以消失了.

#### 小对象压缩(ziplist)

- Redis 是一个非常耗费内存的数据库，它所有的数据都放在内存里。
- 32bit vs 64bit: 如果你对 Redis 使用内存不超过 4G，可以考虑使用 32bit 进行编译,内部所有数据结构所使用的指针空间占用会少一半 。
- 小对象压缩存储 (ziplist): 如果内部元素比较少,将map、set压缩成紧凑的数据
- 存储界限 : 当集合对象的元素不断增加，或者某个 value 值过大，这种小对象存储也会被升级为标准结构
```
hash-max-ziplist-entries 512  # hash 的元素个数超过 512 就必须用标准结构存储
hash-max-ziplist-value 64  # hash 的任意元素的 key/value 的长度超过 64 就必须用标准结构存储
list-max-ziplist-entries 512  # list 的元素个数超过 512 就必须用标准结构存储
list-max-ziplist-value 64  # list 的任意元素的长度超过 64 就必须用标准结构存储
zset-max-ziplist-entries 128  # zset 的元素个数超过 128 就必须用标准结构存储
zset-max-ziplist-value 64  # zset 的任意元素的长度超过 64 就必须用标准结构存储
set-max-intset-entries 512  # set 的整数元素个数超过 512 就必须用标准结构存储
```
- 内存回收机制: Redis 并不总是可以将空闲内存立即归还给操作系统。这就好比电影院里虽然人走了，但是座位还在，下一波观众来了，直接坐就行
- 内存分配算法: 内存分配是一个非常复杂的课题，需要适当的算法划分内存页，需要考虑内存碎片，需要平衡性能和效率，Redis 可以使用 jemalloc(facebook) 库来管理内存。

#### 主从备份
- CAP 原理
  - C - Consistent ，一致性
  - A - Availability ，可用性
  - P - Partition tolerance ，分区容忍性
  - 网络分区: 分布式系统的节点往往都是分布在不同的机器上进行网络隔离开的，这意味着必然会有网络断开的风险，这个网络断开的场景的专业词汇叫着「网络分区」。
  - 一句话概括 CAP 原理就是——网络分区发生时，一致性和可用性两难全。
- 最终一致
  - 一致性: Redis 的主从数据是异步同步的，所以分布式的 Redis 系统并不满足「一致性」要求。
  - 可用性: 当客户端在 Redis 的主节点修改了数据后，立即返回，即使在主从网络断开的情况下，主节点依旧可以正常对外提供修改服务，所以 Redis 满足「可用性」。
  - 最终一致性: Redis 保证「最终一致性」，从节点会努力追赶主节点，最终从节点的状态会和主节点的状态将保持一致。
- 主从同步
  - Redis 同步支持主从同步和从从同步，从从同步功能是 Redis 后续版本增加的功能，为了减轻主库的同步负担。后面为了描述上的方便，统一理解为主从同步。
  - 增量同步: Redis 同步的是指令流,Redis 的复制内存 buffer 是一个定长的环形数组，如果数组内容满了，就会从头开始覆盖前面的内容
  - 快照同步: 非常耗费资源的操作,在主库上进行一次 bgsave 将当前内存的数据全部快照到磁盘文件中,将快照文件的内容全部传送到从节点.
  - 配置合适buffer大小参数: 避免快照复制的死循环。
  - 当从节点刚刚加入到集群时，它必须先要进行一次快照同步，同步完成后再继续进行增量同步。
  - 无盘复制: 指主服务器直接通过套接字将快照内容发送到从节点，生成快照是一个遍历的过程，主节点会一边遍历内存，一边将序列化的内容发送到从节点.
  - Wait 指令: 可以让异步复制变身同步复制. wait 提供两个参数，第一个参数是从库的数量 N，第二个参数是时间 t，以毫秒为单位。
  - 主从复制是 Redis 分布式的基础,Redis 的高可用离开了主从复制将无从进行。
  - 不过复制功能也不是必须的，如果你将 Redis 只用来做缓存，跟 memcache 一样来对待，也就无需要从库做备份，挂掉了重新启动一下就行。
  - 但是只要你使用了 Redis 的持久化功能，就必须认真对待主从复制，它是系统数据安全的基础保障。

#### 集群方案：Sentinel
- 当故障发生时可以自动进行从主切换，程序可以不用重启，运维可以继续睡大觉，仿佛什么事也没发生一样。
- Redis 官方提供了这样一种方案 —— Redis Sentinel(哨兵)。
- 消息丢失: 如果主从延迟特别大，Sentinel 无法保证消息完全不丢失,它有两个选项可以限制主从延迟过大。
```
min-slaves-to-write 1 # 示主节点必须至少有一个从节点在进行正常复制,否则就停止对外写服务，丧失可用性
min-slaves-max-lag 10 # 表示如果 10s 没有收到从节点的反馈，就意味着从节点同步不正常，要么网络断开了，要么一直没有给反馈。
```

- Sentinel 基本使用
  - 客户端: sentinel 发现主从节点的地址，然后在通过这些地址建立相应的连接来进行数据存取操作.
  - 1. 建立sentinel连接池 ->
  - 2. 去查询主库地址 ->
  - 3. 跟内存中的主库地址进行比对 ->
  - 4. 如果变更了，就断开所有连接，重新使用新地址建立新连接 ->
  - 5. 如果是旧的主库挂掉了，那么所有正在使用的连接都会被关闭，然后在重连时就会用上新地址。
  -  没有新连接需要建立，那这个连接是不是一致切换不了？
    - 主从切换后，之前的主库被降级到从库，所有的修改性的指令都会抛出ReadonlyError。
    - 如果没有修改性指令，虽然连接不会得到切换，但是数据不会被破坏，所以即使不切换也没关系。
  - sentinel一般用于读写分离，从库也会服务，cluster的从库一般只是备用库

   - [ ] 尝试自己搭建一套 redis-sentinel 集群；
   - [ ] 使用Java 的客户端对集群进行一些常规操作；
   - [ ] 试试主从切换，主动切换和被动切换都试一试，看看客户端能否正常切换连接；

#### 集群方案: Codis

- 它可以将众多小内存的 Redis 实例综合起来，将分布在多台机器上的众多 CPU 核心的计算能力聚集到一起，完成海量数据存储和高并发读写操作。
- 有点像微服务
- 每个 Codis 节点都是对等的。因为单个 Codis 代理能支撑的 QPS 比较有限，
- 通过启动多个 Codis 代理可以显著增加整体的 QPS 需求，还能起到容灾功能，
- 挂掉一个 Codis 代理没关系，还有很多 Codis 代理可以继续服务。
- 分片:
   - Codis 将所有的 key 默认划分为 1024 个槽位(slot),hash计算slot-key。
- Codis 实例之间slot关系如何同步:
   - 如果 Codis 的槽位映射关系只存储在内存里，那么不同的 Codis 实例之间的槽位关系就无法得到同步。
   - 所以 Codis 还需要一个分布式配置存储数据库专门用来持久化槽位关系。Codis 开始使用 ZooKeeper，后来连 etcd 也一块支持了。
   - Codis 将槽位关系存储在 zk 中，并且提供了一个 Dashboard 可以用来观察和修改槽位关系，
   - 当槽位关系变化时，Codis Proxy 会监听到变化并重新同步槽位关系，从而实现多个 Codis Proxy 之间共享相同的槽位关系配置。
- 扩容:
   - 刚开始 Codis 后端只有一个 Redis 实例，1024 个槽位全部指向同一个 Redis。
   - 然后一个 Redis 实例内存不够了，所以又加了一个 Redis 实例。
   - 这时候需要对槽位关系进行调整，将一半的槽位划分到新的节点。
   - 这意味着需要对这一半的槽位对应的所有 key 进行迁移，迁移到新的 Redis 实例。
- 如何找到shot所有的key？？
- 自动均衡：观察每个 Redis 实例对应的 Slots 数量，如果不平衡，就会自动进行迁移
- Codis 的代价:
   - Codis 中所有的 key 分散在不同的 Redis 实例中,所以事务就不能再支持了.
   - 同样 rename 操作也很危险，它的参数是两个 key.
   - 同样为了支持扩容，单个 key 对应的 value 不宜过大，因为集群的迁移的最小单位是 key
   - Codis 因为增加了 Proxy 作为中转层，所有在网络开销上要比单个 Redis 大.
   - Codis 的集群配置中心使用 zk 来实现，意味着在部署上增加了 zk 运维的代价
- Codis 的优点:
   - Codis 在设计上相比 Redis Cluster 官方集群方案要简单很多
   - 将分布式的问题交给了第三方 zk/etcd 去负责
- Codis MGET 指令的操作过程:分治汇总

#### 集群 ：Cluster
- RedisCluster 是 Redis 的亲儿子，它是 Redis 作者自己提供的 Redis 集群化方案。
- Redis Cluster 将所有数据划分为 16384 的 slots，它比 Codis 的 1024 个槽划分的更为精细。
- 每个节点负责其中一部分槽位。槽位的信息存储于每个节点中，它不像 Codis，它不需要另外的分布式存储来存储节点槽位信息。
- 当 Redis Cluster 的客户端来连接集群时，它也会得到一份集群的槽位配置信息。这样当客户端要查找某个key 时，可以直接定位到目标节点。
- Codis 需要通过 Proxy 来定位目标节点，RedisCluster 是直接定位。
- 客户端为了可以直接定位某个具体的 key 所在的节点，它就需要缓存槽位相关信息，这样才可以准确快速地定位到相应的节点。
- 同时因为槽位的信息可能会存在客户端与服务器不一致的情况，还需要纠正机制来实现槽位信息的校验调整。

- 槽位定位算法
   - Cluster 默认会对 key 值使用 crc16 算法进行 hash 得到一个整数值，然后用这个整数值对 16384 进行取模来得到具体槽位。
- 跳转槽位
- 迁移槽位
- 容错
- 网络抖动
- 槽位迁移感知
- 集群变更感知


#### Redis Stream (了解了解）
- Stream，它是一个新的强大的支持多播的可持久化的消息队列
- Comsumer Group  last_delivered_id
####  Info 指令

- Server 服务器运行的环境参数
- Clients 客户端相关信息
- Memory 服务器运行内存统计数据
- Persistence 持久化信息
- Stats 通用统计数据
- Replication 主从复制相关信息
- CPU CPU 使用情况
- Cluster 集群信息
- KeySpace 键值对统计数量信息

##### Redis 每秒执行多少次指令？
qjyd@bogon ~$ redis-cli info stats | grep ops
instantaneous_ops_per_sec:0
##### Redis 连接了多少客户端？
redis-cli info clients
##### Redis 内存占用多大 ?
info memory
- used_memory_human:827.46K # 内存分配器 (jemalloc) 从操作系统分配的内存总量
- used_memory_rss_human:3.61M  # 操作系统看到的内存占用 ,top 命令看到的内存
- used_memory_peak_human:829.41K  # Redis 内存消耗的峰值
- used_memory_lua_human:37.00K # lua 脚本引擎占用的内存大小
- 如果单个 Redis 内存占用过大，并且在业务上没有太多压缩的空间的话，可以考虑集群化了。
##### 复制积压缓冲区多大？
- >redis-cli info replication |grep backlog
-  repl_backlog_active:0
-  repl_backlog_size:1048576  # 这个就是积压缓冲区大小
-  repl_backlog_first_byte_offset:0
-  repl_backlog_histlen:0
复制积压缓冲区大小非常重要，它严重影响到主从复制的效率。
从库可以通过积压缓冲区恢复中断的主从同步过程。
积压缓冲区是环形的，后来的指令会覆盖掉前面的内容。
如果有多个从库复制，积压缓冲区是共享的，它不会因为从库过多而线性增长。
如果实例的修改指令请求很频繁，那就把积压缓冲区调大一些，几十个 M 大小差不多了，如果很闲，那就设置为几个 M。

- slowlog get 可以查看慢操作；
- 定位大key可以使用redis-cli --bigkeys 或者使用rdb工具分析rdb文件；
- 看热key只能临时打开下monitor吧
##### redis的缓存命中率?
- 读取一个键之后(读操作和写操作都要对键进行读取)，服务器会根据键是否存在来更新服务器的键空间命中(hit)次数或键空间不命中(miss)次数，
- 这两个值可以在INFO status 命令的keyspace_hits属性和keyspace_misses属性中查看。


#### Redis 分布式锁
- Sentinel集群模式实现分布式锁的问题？
  - 比如在 Sentinel 集群中，主节点挂掉时，从节点会取而代之，客户端上却并没有明显感知。
  - 原先第一个客户端在主节点中申请成功了一把锁，但是这把锁还没有来得及同步到从节点，主节点突然挂掉了。
  - 然后从节点变成了主节点，这个新的节点内部没有这个锁，所以当另一个客户端过来请求加锁时，立即就批准了。
  - 这样就会导致系统中同样一把锁被两个客户端同时持有，不安全性由此产生。

- RedLock
  - 为了解决这个问题，Antirez 发明了 Redlock 算法，它的流程比较复杂，不过已经有了很多开源的 library 做了良好的封装，用户可以拿来即用，比如 redlock-py。
  - 如果你很在乎高可用性，希望挂了一台 redis 完全不受影响，那就应该考虑 redlock。
  - 不过代价也是有的，需要更多的 redis 实例，性能也下降了，代码上还需要引入额外的library，运维上也需要特殊对待，这些都是需要考虑的成本，使用前请再三斟酌。



#### 过期策略
- 字典存储 定时遍历 惰性删除
- 从库的过期策略(主从延迟)
   - 从库不会进行过期扫描，从库对过期的处理是被动的。主库在 key 到期时，会在 AOF 文件里增加一条 del 指令，同步到所有的从库，从库通过执行这条 del 指令来删除过期的 key。
   - 因为指令同步是异步进行的，所以主库过期的 key 的 del 指令没有及时同步到从库的话，会出现主从数据的不一致，主库没有的数据在从库里还存在，比如上一节的集群环境分布式锁的算法漏洞就是因为这个同步延迟产生的。

#### LRU

- 当 Redis 内存超出物理内存限制时，内存的数据会开始和磁盘产生频繁的交换 (swap)。

- 交换会让 Redis 的性能急剧下降，对于访问量比较频繁的 Redis 来说，这样龟速的存取效率基本上等于不可用。

- 在生产环境中我们是不允许 Redis 出现交换行为的，为了限制最大使用内存，Redis 提供了配置参数 maxmemory 来限制内存超出期望大小。
- 当实际内存超出 maxmemory 时，Redis 提供了几种可选策略 (maxmemory-policy) 来让用户自己决定该如何腾出新的空间以继续提供读写服务。
  - noeviction 只读不谢
  - volatile-lru 设置了过期时间的key，LRU过期淘汰
  - volatile-ttl key的剩余寿命ttl过期淘汰
  - volatile-random 随机淘汰
  - allkeys-lru  全体key LRU淘汰
  - allkeys-random 全体key 随机淘汰
- 如果你只是拿 Redis 做缓存，那应该使用 allkeys-xxx，客户端写缓存时不必携带过期时间。如果你还想同时使用 Redis 的持久化功能，那就使用 volatile-xxx 策略，这样可以保留没有设置过期时间的 key，它们是永久的 key 不会被 LRU 算法淘汰。
- Redis作为LRU Cache的实现： https://yq.aliyun.com/articles/63034
- redis lru实现策略： https://blog.csdn.net/mysqldba23/article/details/68482894

##### LRU 算法实现
- 实现 LRU 算法除了需要 key/value 字典外，还需要附加一个链表，链表中的元素按照一定的顺序进行排列。当空间满的时候，会踢掉链表尾部的元素。当字典的某个元素被访问时，它在链表中的位置会被移动到表头。所以链表的元素排列顺序就是元素最近被访问的时间顺序。
- 位于链表尾部的元素就是不被重用的元素，所以会被踢掉。位于表头的元素就是最近刚刚被人用过的元素，所以暂时不会被踢。
##### 近似 LRU 算法
Redis 使用的是一种近似 LRU 算法，它跟 LRU 算法还不太一样。
之所以不使用 LRU 算法，是因为需要消耗大量的额外的内存，需要对现有的数据结构进行较大的改造。
近似 LRU 算法则很简单，在现有数据结构的基础上使用随机采样法来淘汰元素，能达到和 LRU 算法非常近似的效果。
Redis 为实现近似 LRU 算法，它给每个 key 增加了一个额外的小字段，这个字段的长度是 24 个 bit，也就是最后一次被访问的时间戳。

####  懒惰删除(unlink)
删除指令 del 会直接释放对象的内存，大部分情况下，这个指令非常快，没有明显延迟。
不过如果删除的 key 是一个非常大的对象，比如一个包含了千万元素的 hash，那么删除操作就会导致单线程卡顿。
- unlink
   - Redis 为了解决这个卡顿问题，在 4.0 版本引入了 unlink 指令，它能对删除操作进行懒处理，丢给后台线程来异步回收内存。
- flushdb/flushall
   - Redis 提供了 flushdb 和 flushall 指令，用来清空数据库，这也是极其缓慢的操作。Redis 4.0 同样给这两个指令也带来了异步化，在指令后面增加 async 参数就可以将整棵大树连根拔起，扔给后台线程慢慢焚烧。
- AOF Sync也很慢
   - Redis需要每秒一次(可配置)同步AOF日志到磁盘，
   - 确保消息尽量不丢失，需要调用sync函数，这个操作会比较耗时，会导致主线程的效率下降，所以Redis也将这个操作移到异步线程来完成。
- 更多异步删除点
   - Redis 回收内存除了 del 指令和 flush 之外，还会存在于在 key 的过期、LRU 淘汰、rename 指令以及从库全量同步时接受完 rdb 文件后会立即进行的 flush 操作。
   - Redis4.0 为这些删除点也带来了异步删除机制，打开这些点需要额外的配置选项。
   - slave-lazy-flush 从库接受完 rdb 文件后的 flush 操作
   - lazyfree-lazy-eviction 内存达到 maxmemory 时进行淘汰
   - lazyfree-lazy-expire key 过期删除
   - lazyfree-lazy-server-del rename 指令删除 destKey
   - redis4.0之lazyfree: https://yq.aliyun.com/articles/205504

#### 优雅地使用 Jedis
- 本节面向 Java 用户，主题是如何优雅地使用 Jedis 编写应用程序，既可以让代码看起来赏心悦目，又可以避免使用者犯错。
- JedisPool，Jedis 对象不是线程安全的


#### redis 安全问题
##### 指令安全

Redis 有一些非常危险的指令，这些指令会对 Redis 的稳定以及数据安全造成非常严重的影响。
比如 keys 指令会导致 Redis 卡顿，flushdb 和 flushall 会让 Redis 的所有数据全部清空。
如何避免人为操作失误导致这些灾难性的后果也是运维人员特别需要注意的风险点之一。

Redis 在配置文件中提供了 rename-command 指令用于将某些危险的指令修改成特别的名称，用来避免人为误操作。

比如在配置文件的 security 块增加下面的内容:

rename-command keys abckeysabc

如果还想执行 keys 方法，那就不能直接敲 keys 命令了，而需要键入abckeysabc。 如果想完全封杀某条指令，可以将指令 rename 成空串，就无法通过任何字符串指令来执行这条指令了。

rename-command flushall ""
##### 端口安全
- Redis 的服务地址一旦可以被外网直接访问，内部的数据就彻底丧失了安全性,在 Redis 的配置文件中指定监听的 IP 地址，避免这样的惨剧发生
`bind 10.100.20.13`
requirepass yoursecurepasswordhereplease
- 密码控制也会影响到从库复制，masterauth指令配置相应的密码才可以进行复制操作。
`masterauth yoursecurepasswordhereplease`
##### Lua 脚本安全
开发者必须禁止 Lua 脚本由用户输入的内容 (UGC) 生成
##### SSL 代理
#### lua 脚本支持
SCRIPT LOAD 和 EVALSHA 指令

#### 参考资料
- 1. 国内 90 后技术大神黄健宏的著作《Redis 设计与实现》
- 2. Redis 官网 & Redis 作者 Antirez 的 Blog
- 3. Redis 官网: https://redis.io/
- 4. Antirez 博客: http://antirez.com/latest/0
- 5. Redis 扩展模块: https://redis.io/modules







