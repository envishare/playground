## Sangria playground

This is an example of a [GraphQL](https://facebook.github.io/graphql) server written with [Play framework](https://www.playframework.com) and
[Sangria](http://sangria-graphql.org). It also serves as a playground. On the right hand side you can see a textual representation of the GraphQL
schema which is implemented on the server and that you can query here. On the left hand side
you can execute a GraphQL queries and see the results of its execution.

It's available here:

[http://try.sangria-graphql.org](http://try.sangria-graphql.org)

This is just a small demonstration. It really gets interesting when you start to play with the schema on the server side. Fortunately it's
pretty easy to do. Since it's a simple Play application, all it takes to start playground locally and start playing with the schema is this:

```bash
$ git clone https://github.com/sangria-graphql/sangria-playground.git
$ cd sangria-playground
$ sbt run
```

Now you are ready to point your browser to [http://localhost:9000](http://localhost:9000).
The only prerequisites are [SBT](http://www.scala-sbt.org/download.html) and [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

##How to create a new elastic instance
01. install docker
02. run docker pull docker.elastic.co/elasticsearch/elasticsearch:7.4.2
03. find the image ( docker images )
04. set the environmental variables
    sudo sysctl -w vm.max_map_count=262144
    Incase the system reboot, set it permanently by adding below line to this file >> vi /etc/sysctl.conf
    vm.max_map_count=262144

05. run the image as a single node
docker run -p 9200:9200 -d=true -e "discovery.type=single-node" <imageid>

##how to release the service manually
01. sbt docker:publishLocal
02. docker save <imageid> > sangria-playground-<version>.tar
03. upload the sangria-playground-<version>.tar to the save
04. log in to the server
05. docker load -i sangria-playground-<version>.tar
06. run docker image ls and identify the old image and kill it, and identify the new image id
05. docker run -e JAVA_OPTS='-Dplay.server.pidfile.path=/dev/null' -m 2048m <newImageId>

To create a index
http://localhost:9200/company

curl -u elastic:doNotChange -X POST "localhost:9200/_xpack/security/role/hotel_notes_admin?pretty" -H 'Content-Type: application/json' -d ' "indices": [ { "names": [ "en_activities*"], "privileges": ["all"]}] '
docker run -p 9000:9000 -e JAVA_OPTS='-Dplay.server.pidfile.path=/dev/null' -m 2048m <imageid>

