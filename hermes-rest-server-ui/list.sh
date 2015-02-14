#!/bin/sh
ls -la --time-style=full-iso /home/spaddo/PDF/*.pdf | awk -F' ' '{print $7 " " $9}' | sort -n -k 2