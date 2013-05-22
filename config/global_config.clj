{:replication 1
 :port 3578
 :hosts ["IP_ADDRESS"]
 :domains {
    "example_0_2_0" "../data/domains/example_0_2_0"
    "example_0_2_0_to_0_4_4" "../data/domains/example_0_2_0_to_0_4_4"
    "example" "../data/domains/example"

   ;; If you have uploaded the data to a s3 bucket you can try out the following
   ; "example" "s3n://your-bucket/domains/genders"
 }
}
