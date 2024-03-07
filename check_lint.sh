#!/bin/bash

#
# Copyright (c) 2024 Paysafe Group
#

# Define the directory
directory="reports/lint"

# Delete all files in the directory
rm -f "$directory"/*

# Run the linting process and capture its output
lint_output=$(./gradlew lint 2>&1)
lint_exit_code=$?

# Check if the lint command was successful
if [ $lint_exit_code -ne 0 ]; then
    echo "$lint_output"
    exit 1
fi

# Initialize a flag to track if all files are good
all_good=true

# Check if all text files contain "No issues found."
for file in "$directory"/*.txt; do
    # Check if the file exists and is a regular file
    if [ -f "$file" ]; then
        if ! grep -q "No issues found." "$file"; then
            all_good=false
            echo # Adding a blank line for readability
            cat "$file"
            echo # Adding a blank line for readability
        fi
    fi
done

# Output the result
if $all_good; then
    echo "pass"
fi
