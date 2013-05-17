{:replication 1
 :port 3578
 :hosts ["IP_ADDRESS"]
 :domains {
   "example" "../data/domains/example"
   ; "example_0_2_0" "../data/domains/example_0_2_0"

   ;; If you have uploaded the data to a s3 bucket you can try out the following
   ; "example" "s3n://your-bucket/domains/genders"
 }
}
