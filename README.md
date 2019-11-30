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


## Elastic Configuration

To create a index
http://localhost:9200/company

PUT
{
"settings": {
   "index": {
         "number_of_shards": 1,
         "number_of_replicas": 1
   },
   "analysis": {
     "analyzer": {
       "analyzer-name": {
             "type": "custom",
             "tokenizer": "keyword",
             "filter": "lowercase"
       }
     }
   },
   "mappings": {
      
       "properties": {
         "age": {
               "type": "long"
         },
         "experienceInYears": {
               "type": "long"      
         },
         "name": {
               "type": "string",
               "analyzer": "analyzer-name"
         }
       
     }
     
      "properties": {
              "id": {
                    "type": "string"
              },
              "name": {
                    "type": "string"      
              },
              "createdAt": {
                    "type": "string",
                    "analyzer": "analyzer-name"
              }
            
          }
     
     {
             "id": "1000",
             "name": "Lake Cleanup",
             "createdAt": "2019-11-24T11:41:25.206832Z",
             "createdBy": "69060e5f-82d7-4a2e-85e0-4fd81e5942b8",
             "modifiedAt": "2019-11-24T11:41:25.206954Z",
             "modifiedBy": "ddee4fd7-dfb2-45e9-a77c-3aa7161a7f6e",
             "reference": {
               "parent": null,
               "child": {
                 "id": "1001",
                 "name": "Ocean Cleanup",
                 "createdAt": "2019-11-24T11:41:25.205854Z",
                 "createdBy": "53263aee-b1b0-48b3-b0be-2afc42ff98e1",
                 "modifiedAt": "2019-11-24T11:41:25.206030Z",
                 "modifiedBy": "38325df0-f537-4ba9-92da-551bf7617459"
               }
             },
             "review": [
               {
                 "id": 7877775093513668000,
                 "review": "잺운潦ꯐࢺ뼯磻躆䑤㵿쩫受⑚ᘪӠنڪ찴捍업"
               }
             ]
           }
           
   }
 }  
}


curl -u elastic:doNotChange -X POST "localhost:9200/_xpack/security/role/hotel_notes_admin?pretty" -H 'Content-Type: application/json' -d ' "indices": [ { "names": [ "en_activities*"], "privileges": ["all"]}] '

