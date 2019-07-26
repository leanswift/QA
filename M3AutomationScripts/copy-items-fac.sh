#!/bin/bash
while IFS=, read col1
do
    for i in `seq $1 $2`
    do
        echo "Trying to create $col1-$i from $col1..."
        echo
        echo "URL: "
        url="https://172.30.1.121:22108/m3api-rest/execute/MMS200MI/CpyItmFac;metadata=true;maxrecs=100;excludempty=false?&CONO=$3&FACI=$4&ITNO=$col1-$i&CFAC=$5&CITN=$col1"
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
 