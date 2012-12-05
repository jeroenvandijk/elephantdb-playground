# Elephantdb Playground

This project is meant to get up and running quickly with ElephantDB. It shows how to run ElephantDB locally, how to generate shards and how to fetch data from the repl.

Note: all the command below assume you have Leiningen 2 installed

## Starting the database

  bin/serve
  
This will get your local IP and write config files to the tmp directory in elephantdb_local

## Interacting with the database

  bin/repl

This will tell the repl what IP address should be used (can be overridden to interact with a remote server). Thry the following in the repl:
  
  user=> (fetch-raw "example" "a")
  #<Value Value(data:80 01 00 02 00 00 00 09 67 65 74 53 74 72 69 6E 67 00 00 00 01 0C 00 00 0B 00 01 00 00 00 10 0C 00 01 0A 00 01 00 00 00 00 00 00 00 01 00 00)>
    
  user=> (fetch "example" "a")
  #<Value <Value count:Count(value:1)>>
  
## Generate shards

ElephantDB depends on shards generated from Hadoop. Look in the data_gen directory to see how this works. 
I've added Thrift serialization to see how you can have flexible data types. To use the ElephantDB database you need to serialize the values to ByteArrays. Here is how you generate shards:

  cd data_gen
  lein run -m data.core/generate
  
## List raw data in shards

  cd data_gen
  lein run -m data.core/list_raw ../data/domains/example
  
## List data in shards

  cd data_gen
  lein run -m data.core/list ../data/domains/example