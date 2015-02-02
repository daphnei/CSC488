#!/bin/bash

bash RUNALLTESTS.sh ./Assignment2/dist/compiler488.jar ./tests/passing/
bash RUNALLTESTS.sh -f ./Assignment2/dist/compiler488.jar ./tests/failing

