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

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;

/**
 * Utility functions for charset handling.
 * @author piotr
 */
public final class CharsetUtils {

    private static final String CHARSET_UTF8 = "UTF-8";
    private static final String DEFAULT_CHARSET = "US-ASCII";

    public static String detectCharsetName(byte[] data) {
        return isValidUtf8(data) ? CHARSET_UTF8 : DEFAULT_CHARSET;
    }

    public static boolean isUtf8CharsetName(String charsetName) {
        return CHARSET_UTF8.equals(charsetName);
    }

    private static boolean isValidUtf8(byte[] input) {
        var cs = StandardCharsets.UTF_8.newDecoder();

        try {
            cs.decode(ByteBuffer.wrap(input));
            return true;
        } catch (CharacterCodingException e) {
            return false;
        }
    }

    private CharsetUtils() {
    }
}
