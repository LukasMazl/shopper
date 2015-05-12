#!/usr/bin/env bash

target_dir="${0%/*}"

curl -s 'http://www.sreality.cz/api/cs/v1/estates?category_main_cb=2&category_type_cb=1&locality_region_id=10&per_page=60' | jq -S . > "${target_dir}/sample-estate-listing.json"
curl -s 'http://www.sreality.cz/api/cs/v1/estates/3410497628' | jq -S . > "${target_dir}/sample-estate.json"
