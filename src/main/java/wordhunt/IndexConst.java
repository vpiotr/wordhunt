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

/**
 * Constants for internals of index file
 * 
 * @author piotr
 */
public final class IndexConst {
  public static final String FILE_PREFIX = "F";
  public static final String DIR_PREFIX = "D";
  public static final String COMMENT_PREFIX = "; ";
  public static final String ENTRY_FIELD_SEPARATOR = "*";
  public static final String ENTRY_SEPARATOR = "\n";
  public static final String META_SOURCE_PATH = "source-path";
  public static final String META_SEPARATOR = "=";

  private IndexConst() {}
}
