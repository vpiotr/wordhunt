About wordhunt
--------------------
Wordhunt is a word search utility, finds files using word queries like Internet search engine.

Author: Piotr Likus

Created: 31/01/2017

Examples
------------------
Example of usage:

  ./wordhunt.sh --find . basic search java

-> finds all files with words "basic search java" in file path, name or contents (in any order).

  ./java -jar ./wordhunt.jar --find . basic search java
  
-> performs the same without usage of shell scripting.  
  
Syntax
--------------------
Usage: wordhunt COMMAND DIRECTORY [OPTIONS] [WORD-LIST]
Search for words from WORD-LIST in files stored inside the specified DIRECTORY.

Examples:
    wordhunt --index .
    wordhunt --find . basic search java
    wordhunt --find "/home/user1/Downloads" --inpath "basic search java"

COMMAND can be:
    --index   creates index required for searching
    --find    performs search in the specified directory
    --help    shows this help information
    --version shows version information

DIRECTORY specifies where files to be searched are stored,
    it's absolute or relative path, "." is acceptable

OPTIONS can be:
    --list                        for clean file listing, useful for further processing
    --include-dirs                for including matching directory names
    --index-path "path"           for specifying index file path,
                                  by default index is stored in DIRECTORY
    --case-sensitive              for case-sensitive search
    --no-case-split               for disabling word split by character case
    --inpath "word-list"          for searching for words in path
    --inname "word-list"          for searching for words in file name
    --incontent "word-list"       for searching for words inside file's contents
    --anywhere "word-list"        for searching for words in path or file's contents
                                  (equal to default search version without options)

WORD-LIST is space-separated word list in any order (any number of arguments).

System requirements
--------------------
This tool has been prepared using Oracle Java 1.8 and can be used on any platform supporting this Java version.
Additional shell scripts are prepared to be used on Linux Mint 18.
  
Third party content
--------------------
This project contains for test purposes eBook "Frankenstein" by Mary Wollstonecraft (Godwin) Shelley from Project Gutenberg.
This is a free eBook which you can download at no cost from:
  https://www.gutenberg.org/ebooks/41445

Licensing
--------------------
Wordhunt is licensed under the Apache License, Version 2.0. See LICENSE for the full license text.
