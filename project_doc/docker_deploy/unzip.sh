#!/bin/bash

#define default variable
base_path=$(cd `dirname $0`; pwd)
app_path="${base_path}/dist"
zip_name="${base_path}/dist.zip"
rm -fr ${app_path}
unzip -d ${app_path} ${zip_name}
app_doc_path="${base_path}/dist_doc"
zip_doc_name="${base_path}/dist_doc.zip"
rm -fr ${app_doc_path}
unzip -d ${app_doc_path} ${zip_doc_name}
echo "unzip success!"
