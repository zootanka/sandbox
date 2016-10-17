docker run -d --name docker-zookeeper --publish 49100:2181 --env INTEGRATION_TEST_MODUS=true dr.seometric.net:5000/sm-zookeeper:1.7
docker run -d --name docker-kafka --hostname myname --publish 49110:9092 --link docker-zookeeper:zookeeper --env KAFKA_BROKER_ID=0 --env KAFKA_NUM_PARTITIONS=1 --env KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 dr.seometric.net:5000/sm-kafka:1.15
docker run -d --env ZK_HOSTS=zookeeper:2181 -p 9000:9000 --link docker-zookeeper:zookeeper --link docker-kafka:myname --name kafka-manager dr.seometric.net:5000/sm-kafka-manager:1.5

docker ps -a

# download kafka distro
# add cluster: zookeper:2181 under : http://docker:9000/addCluster
# create topic: bin/kafka-topics.sh --create --zookeeper localhost:49100 --replication-factor 1 --partitions 1 --topic test
# list topics: bin/kafka-topics.sh --list --zookeeper localhost:49100
# produce a kafka messages: bin/kafka-console-producer.sh --broker-list localhost:49110 --topic test
# consume kafka messages: bin/kafka-console-consumer.sh --zookeeper localhost:49100 --topic test --from-beginning