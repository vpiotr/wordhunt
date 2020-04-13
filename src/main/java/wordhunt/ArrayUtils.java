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

import java.util.Arrays;

/**
 * Utility functions for arrays.
 *
 * @author piotr
 */
public class ArrayUtils {

    /**
     * Joins two arrays into a new one.
     *
     * @param first first array
     * @param second second array
     * @return joined arrays or null if both are null.
     */
    public static <T> T[] merge(T[] first, T[] second) {
        T[] result;

        if (first != null) {
            if (second == null) {
                result = Arrays.copyOf(first, first.length);
            } else {
                result = Arrays.copyOf(first, first.length + second.length);
            }
        } else {
            result = null;
        }

        if (second == null) {
            return result;
        }

        if (result == null) {
            result = second;
        } else {
            int firstLen = (first != null) ? first.length : 0;
            System.arraycopy(second, 0, result, firstLen, second.length);
        }

        return result;
    }

    private ArrayUtils() {
    }
}
