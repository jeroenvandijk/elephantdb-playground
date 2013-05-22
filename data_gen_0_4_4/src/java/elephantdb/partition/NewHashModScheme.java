package elephantdb.partition;

import elephantdb.Utils;

public class NewHashModScheme implements ShardingScheme {

    public int shardIndex(byte[] shardKey, int shardCount) {
        return Utils.keyShard(shardKey, shardCount);
    }
}