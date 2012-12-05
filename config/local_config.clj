{:replication 1
   :port 3578
   :download-rate-limit 1024
   :local-root "./tmp"
   ;; Ip address is parsed here and added each time your start the server
   :hosts ["IP_ADDRESS"]

   ; Local storage doesn't need a hdfs-conf

   ; Upload the /data/domains/example to your s3 bucket and try out the following
   ; S3 storage
   ; :hdfs-conf {"fs.default.name" "s3n://your-bucket"
   ;             "fs.s3n.awsAccessKeyId" "YOUR-AWS-KEY"
   ;             "fs.s3n.awsSecretAccessKey" "YOUR-AWS-SECRET"}

}
