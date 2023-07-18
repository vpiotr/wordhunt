package wordhunt;

public class HelpWriter {

    private HelpWriter() { throw new RuntimeException("Unsupported") {};}

    public static void writeHelp(boolean longDescription, ProcessLog processLog) {
        processLog.writeLine("Usage: wordhunt COMMAND DIRECTORY [OPTIONS] [WORD-LIST]");
        if (longDescription) {
            String[] syntax = new String[]{
                    "Examples:",
                    "* Create index file for/in specified directory:",
                    "    wordhunt --index /home/user1/Books",
                    "* Find documents with 3 words in current dir:",
                    "    wordhunt basic search java",
                    "* Find documents with 3 words in Downloads dir:",
                    "    wordhunt --find \"/home/user1/Downloads\" --inpath \"basic search java\"",
                    "* Create brief output, useful for further processing:",
                    "    wordhunt --find /home/user1/Books --brief python machine learning",
                    "",
                    "COMMAND can be:",
                    "    --index   creates index required for searching",
                    "    --find    performs search in the specified directory",
                    "    --help    shows this help information",
                    "    --version shows version information",
                    "",
                    "DIRECTORY specifies where files to be searched are stored,",
                    "    it's absolute or relative path, \".\" is acceptable",
                    "",
                    "OPTIONS can be:",
                    "    --brief                       for clean file listing, useful for further processing",
                    "    --include-dirs                for including matching directory names",
                    "    --index-path \"path\"           for specifying index file path,",
                    "                                  by default index is stored in DIRECTORY",
                    "    --case-sensitive              for case-sensitive search",
                    "    --no-case-split               for disabling word split by character case",
                    "    --inpath \"word-list\"          for searching for words in path",
                    "    --inname \"word-list\"          for searching for words in file name",
                    "    --incontent \"word-list\"       for searching for words inside file's contents",
                    "    --anywhere \"word-list\"        for searching for words in path or file's contents",
                    "",
                    "WORD-LIST is space-separated word list in any order (any number of arguments).",
                    "Default search mode is --inpath"
            };

            for (String line : syntax) {
                processLog.writeLine(line);
            }
        } else {
            processLog.writeLine("Try 'wordhunt --help' for more information.");
        }
    }

    public static void writeVersion(String versionString, ProcessLog processLog) {

        processLog.writeLine("wordhunt version " + versionString);

        String[] licenseInfo = new String[]{
                "Copyright (C) 2020 Piotr Likus",
                "Apache License v2.0: <https://www.apache.org/licenses/LICENSE-2.0>.",
                "There is NO WARRANTY, to the extent permitted by law."
        };

        for (String line : licenseInfo) {
            processLog.writeLine(line);
        }
    }


}
