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
package wordhunt.utils;

import java.util.Arrays;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * Utility functions for arrays.
 *
 * @author piotr
 */
public final class ArrayUtils {

    private ArrayUtils() {
    }

    /**
     * Joins two arrays into a new one.
     *
     * @param first first array
     * @param second second array
     * @return joined arrays or null if both are null.
     */
    @SuppressFBWarnings("PZLA_PREFER_ZERO_LENGTH_ARRAYS")
    public static <T> T[] merge(T[] first, T[] second) {
        // If both arrays are null, return null
        if (first == null && second == null) {
            return null;
        }
        
        // If either array is null, return the other (possibly empty) array
        if (first == null) {
            return second;
        }
        if (second == null) {
            return Arrays.copyOf(first, first.length);
        }
        
        // Otherwise merge both arrays
        var result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

}
