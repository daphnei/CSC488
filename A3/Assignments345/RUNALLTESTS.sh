#! /bin/bash

USAGE() {
	echo "Usage: bash RUNALLTESTS.sh test_folder"

	exit 1
}

#Check for the correct usage
if [ "$#" == 1 ]
then
	TEST_DIR=$1
else
	USAGE
fi

#Compile the code.
ant dist

echo "RUNNING TESTS IN FOLDER: $TEST_DIR"
echo

num_tests=0
num_failed=0

#Iterate over all of the .488 files in the passing directory.
for file in $(find $TEST_DIR -name '*.488')
do
	echo 
	echo "RUNNING TEST: $file"
	./RUNCOMPILER.sh $file
done

exit 0

