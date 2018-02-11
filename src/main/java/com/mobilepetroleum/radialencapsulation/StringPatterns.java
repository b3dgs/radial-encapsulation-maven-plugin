/* 
 * Copyright MobilePetroleum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mobilepetroleum.radialencapsulation;

import classycle.util.StringPattern;

import java.util.regex.Pattern;

class StringPatterns {

    static StringPattern exclude(String... excludes) {
        Pattern[] patterns = new Pattern[excludes.length];
        for (int i = 0, excludesLength = excludes.length; i < excludesLength; i++) {
            String exclude = excludes[i];
            patterns[i] = Pattern.compile(exclude);
        }
        return new Exclude(patterns);
    }

    static class Exclude implements StringPattern {
        Pattern[] excludes;

        Exclude(Pattern[] excludes) {
            this.excludes = excludes;
        }

        @Override
        public boolean matches(String string) {
            for (Pattern exclude : excludes) {
                if (exclude.matcher(string).matches()) {
                    return false;
                }
            }
            return true;
        }
    }
}
