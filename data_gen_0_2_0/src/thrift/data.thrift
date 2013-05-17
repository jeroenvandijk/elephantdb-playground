# Use lein2 thrift to generate java files with the schema below (note that this plugin does not yet have proper error messaging. Use thrift cli for debugging )

namespace java data.schema

struct Count {
  1: required i64 value
}

union Value {
  1: optional Count count;
}