#!/bin/bash
while IFS=, read col1
do
    for i in `seq $1 $2`
    do
        echo "Trying to create $col1-$i from $col1..."
        echo
        echo "URL: "
        url="https://172.30.1.121:22108/m3api-rest/execute/CRS610MI/Copy;metadata=true;maxrecs=100;excludempty=false?&CONO=$3&CUTM=$col1&CUNO=$col1-$i&CUNM=$4-$i&CUA1=$5"
        echo $url
        echo
        echo "Response from M3:"
        curl -u "$6:$7" -k $url
        echo
        echo "------------------------------------------------------------------------------------"
        echo
        echo
    done
done < $8