/*
Copyright 2017 Piotr Likus

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package wordhunt;

import java.util.Objects;

import static java.util.Objects.hash;

/**
 * Contains file type information - everything we know about the file required
 * for reading it.
 *
 * @author piotr
 */
public class FileType {

    private final String mimeType;
    private final String charsetName;

    public static final FileType UNKNOWN_FILE_TYPE = new FileType("", "");

    public FileType(String mimeType, String charsetName) {
        this.mimeType = mimeType;
        this.charsetName = charsetName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getCharsetName() {
        return charsetName;
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs == null) {
            return false;
        }

        if (rhs == this) {
            return true;
        }

        if (!(rhs instanceof FileType)) {
            return false;
        }

        FileType right = (FileType) rhs;
        return this.mimeType.equals(right.mimeType) && this.charsetName.equals(right.charsetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.mimeType, this.charsetName);
    }
}
