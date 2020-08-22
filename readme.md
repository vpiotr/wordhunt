About wordhunt
--------------------
Wordhunt is a word search utility, finds files in local file system using word queries like Internet search engine.

Author: Piotr Likus

Created: 31/01/2017

Unique features
------------------
Words can be specified in any order, order in file names can be different.
This tool extracts words using character case and allows numbers to be used as words.

Search "python machine learning" can match any of the following files:

    Python-Machine-Learning.pdf
    PythonMachineLearning.txt
    Machine Learning/Python - introduction to ML.pdf
    
Search "java 8" can match any of the following:

    JAVA_FX_8_ESSENTIALS/9781784398026-JAVA_FX_8_ESSENTIALS.pdf
    Java/Java_8/java8lambdas/java8lambdas.epub
    Java/JEE/JAVA_EE_8_HIGH_PERFORMANCE
     
Search "9781784398026" can return this result:

    9781784398026-JAVA_FX_8_ESSENTIALS.pdf
             
Examples
------------------
Example of usage:

  ./wordhunt.sh --find . basic search java

-> finds all files with words "basic search java" in file path, name or contents (in any order).

  java -jar ./wordhunt.jar --find . basic search java
  
-> performs the same without usage of shell scripting.  
  
Syntax
--------------------
Usage: wordhunt COMMAND DIRECTORY [OPTIONS] [WORD-LIST]

Search for words from WORD-LIST in files stored inside the specified DIRECTORY.

Examples:
* Create index file:

    wordhunt --index /home/user1/Books
* Find documents with 3 words in current dir:

    wordhunt basic search java
* Find documents with 3 words in Downloads dir:

    wordhunt --find "/home/user1/Downloads" --inpath "basic search java"
* Create brief output, useful for further processing:

    wordhunt --find /home/user1/Books --brief python machine learning

COMMAND can be:

    --index   creates index required for searching
    --find    performs search in the specified directory
    --help    shows this help information
    --version shows version information

DIRECTORY specifies where files to be searched are stored,
    it's absolute or relative path, "." is acceptable

OPTIONS can be:

    --brief                       for clean file listing, useful for further processing
    --include-dirs                for including matching directory names
    --index-path "path"           for specifying index file path,
                                  by default index is stored in DIRECTORY
    --case-sensitive              for case-sensitive search
    --no-case-split               for disabling word split by character case
    --inpath "word-list"          for searching for words in path
    --inname "word-list"          for searching for words in file name
    --incontent "word-list"       for searching for words inside file's contents
    --anywhere "word-list"        for searching for words in path or file's contents

WORD-LIST is space-separated word list in any order (any number of arguments).

Default search mode is --inpath

System requirements
--------------------
This tool has been prepared using Oracle Java 1.8 and can be used on any platform supporting this Java version.
Additional shell scripts are prepared to be used on Linux Mint 18/19/20.

Licensing
--------------------
Wordhunt is licensed under the Apache License, Version 2.0. See LICENSE for the full license text.
