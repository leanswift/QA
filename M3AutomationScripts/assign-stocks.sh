#!/bin/bash
while IFS=, read col1
do
    for i in `seq $1 $2`
    do
        echo "Trying to assign stock $col1-$i"
        echo
        echo "URL: "
        url="https://172.30.1.121:22108/m3api-rest/execute/MMS310MI/Update;metadata=true;maxrecs=100;excludempty=false?&CONO=$3&WHLO=$4&WHSL=$5&STQI=$6&STAG=2&PRDT=20191212&ITNO=$col1-$i"
        echo $url
        echo
        echo "Response from M3:"
        curl -u "$7:$8" -k $url
        echo
        echo "------------------------------------------------------------------------------------"
        echo
        echo
    done
done < $9



