#!/bin/bash

echo "Cleaning log folder"
rm log/*.log

echo "Cleaning data storage files"
rm *.aof *.rdb

echo "Done"

exit 0

