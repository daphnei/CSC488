#! /bin/sh

echo POOP

if [ "$#" != 3]; then
	echo "RUNALLTESTS.sh compiler.jar passing_dir failing_dir"
	exit 1
fi

COMPILER=$1
PASSING_DIR=$2
FAILING_DIR=$3

for file in $(find $PASSING_DIR -name '*.488')
do
	java -jar $COMPILER $file | grep "Error"
	echo $?	

	#if $? 
	#then
	#	echo "The file with the error  was" $file
	#fi
done

exit 0

