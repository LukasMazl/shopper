#!/usr/bin/env bash

target_dir="${0%/*}/../../../target"

mkdir "${target_dir}/samples"
curl -s "https://www.sreality.cz/api/cs/v2/estates?category_main_cb=2&category_type_cb=1&locality_region_id=10&per_page=60" | jq -S . > "${target_dir}/samples/estate-listing.json"
first_estate_id=$(cat "${target_dir}/samples/estate-listing.json" | jq ._embedded.estates[0].hash_id)
curl -s "https://www.sreality.cz/api/cs/v2/estates/${first_estate_id}" | jq -S . > "${target_dir}/samples/estate.json"
