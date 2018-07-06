#!/usr/bin/python
#import os
import json
import gzip

data = []
with gzip.open('dataset.jsonl.gz', 'rb') as f:
    for line in f:
        data.append(json.loads(line))

    for d in data:
        print(len(d))
    print len(data)

# cmd = "sudo gzip -d dataset.jsonl.gz"
# os.system(cmd)
#f = open('', mode='r', buffering=-1, encoding='utf-8', errors=None, newline=None, closefd=True, opener=None)
#string = b'{\"author\":\"TKKIM\",\"author_email\":\"misoh049@gmail.com\",\"creator_user_id\":\"bb970558-8d48-4874-9794-bd4740099474\",\"extras\":[],\"groups\":[],\"id\":\"62513382-3b26-4bc8-9096-40b6ce8383c0\",\"isopen\":true,\"license_id\":\"other-open\",\"license_title\":\"Other (Open)\",\"maintainer\":\"TKKIM\",\"maintainer_email\":\"misoh049@gmail.com\",\"metadata_created\":\"2017-08-05T03:16:52.857800\",\"metadata_modified\":\"2017-08-05T03:19:33.886925\",\"name\":\"100-validated-species-of-plants\",\"notes\":\"This data includes the typical plants for machine learning test.\",\"num_resources\":1,\"num_tags\":3,\"organization\":null,\"owner_org\":null,\"private\":false,\"relationships_as_object\":[],\"relationships_as_subject\":[],\"resources\":[{\"cache_last_updated\":null,\"cache_url\":null,\"created\":\"2017-08-05T03:17:44.192463\",\"datastore_active\":false,\"description\":\"This file includes 100 species list for machine learning test.\",\"format\":\"CSV\",\"hash\":\"\",\"id\":\"bf4d5116-634e-43f2-a41c-9ae22d362083\",\"last_modified\":null,\"mimetype\":null,\"mimetype_inner\":null,\"name\":\"validated_100_species.csv\",\"package_id\":\"62513382-3b26-4bc8-9096-40b6ce8383c0\",\"position\":0,\"resource_type\":null,\"revision_id\":\"9d6a7ee2-022e-4018-8de0-b9b628476b26\",\"size\":null,\"state\":\"active\",\"url\":\"https://demo.ckan.org/dataset/62513382-3b26-4bc8-9096-40b6ce8383c0/resource/bf4d5116-634e-43f2-a41c-9ae22d362083/download/validated_100_species.csv\",\"url_type\":\"upload\"}],\"revision_id\":\"3bbdcb05-6394-4285-8cc4-237ef77d1f15\",\"state\":\"active\",\"tags\":[{\"display_name\":\"flower\",\"id\":\"806b0369-bc31-4cc2-8147-861885f4e762\",\"name\":\"flower\",\"state\":\"active\",\"vocabulary_id\":null},{\"display_name\":\"image recognition\",\"id\":\"2e583787-08a5-4e28-a9d4-ec5ae94e19c5\",\"name\":\"image recognition\",\"state\":\"active\",\"vocabulary_id\":null},{\"display_name\":\"machine learning\",\"id\":\"7cfc3a49-2999-4aba-bf49-93678a30e62f\",\"name\":\"machine learning\",\"state\":\"active\",\"vocabulary_id\":null}],\"title\":\"100 Validated Species of Plants\",\"type\":\"dataset\",\"url\":\"\",\"version\":\"\"}'
#string2 = string*1024*1024
# string3 = string[:1073741824]
# print(len(string3))
#print(len(string2))
#print(len(string2.encode('utf-8')))

