#!/bin/bash

git checkout --orphan latest_branch
git add -A
git commit -am "initialize project"
git branch -D master
git branch -m master
git config --global http.version HTTP/1.1
git config --global http.postBuffer 524288000
git push -f origin master
