#! /bin/bash

#Check for the correct usage

#while getopts ":pf" opt
#do

#done

if [ "$#" != 3 ]
then
	echo "Usage: sh RUNALLTESTS.sh [-pf] compiler.jar test_dir"
	echo "Use -p if tests in test_dir are intended to compile without error."
	echo "Use -f if tests in test_dir should fail when compiled."
	echo "-p is used as default"

	exit 1
fi

COMPILER=$1
PASSING_DIR=$2
FAILING_DIR=$3

#Iterate over all of the .488 files in the passing directory.
for file in $(find $PASSING_DIR -name '*.488')
do
	#Try to compile the file.
	result="$(java -jar $COMPILER $file 2>&1 )"
	# 2>&1
	#echo "$result"

	#If the compilation output contains the word error, print the error information.
	grep -qv "Error" <<< $result
	if [ ! $? -eq 0 ]
	then
		echo "Error in file: $file"
		echo "$result"
		echo
	fi	
done

#Iterate over all of the .488 files in the failing directory,

exit 0

