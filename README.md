# Elephantdb Playground

This project is meant to get up and running quickly with ElephantDB. It shows how to run ElephantDB locally, how to generate shards and how to fetch data in the repl.

Note: all the commands below assume you have [Leiningen 2](https://github.com/technomancy/leiningen) installed 

## Starting the database

    bin/serve
  
This will get your local IP and write config files to the tmp directory in ./server

## Interacting with the database

    bin/repl

This will tell the repl what IP address should be used (can be overridden to interact with a remote server). Try the following in the repl:
  
    user=> (fetch-raw "example" "a")
    #<byte[] [B@5ef94934>
    
    user=> (fetch "example" "a")
    #<Value <Value count:Count(value:1)>>
  
## Generate shards

ElephantDB depends on shards generated from Hadoop. Look in the data_gen directory to see how this works. 
I've added Thrift serialization to see how you can have flexible data types. To use the ElephantDB database you need to serialize the values to ByteArrays. Here is how you generate shards:

    bin/shards
    
Or
    
    cd data_gen
    lein run -m data.core/generate
  
## List raw data in shards

    cd data_gen
    lein run -m data.core/list_raw ../data/domains/example
  
## List data in shards

    cd data_gen
    lein run -m data.core/list ../data/domains/example

## Deploying ElephantDB

See https://github.com/sritchie/elephantdb-deploy/pull/2
