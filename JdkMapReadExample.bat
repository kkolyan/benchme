call mvn clean install
java -ea -XX:+UseConcMarkSweepGC -server -Xms64M -Xmx512M -jar bin/benchme.jar net.kkolyan.utils.benchme.examples.JdkMapReadExample