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

import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class Files {
    static List<String> removeMissingFiles(List<String> files, Log log) {
        List<String> existingFiles = new ArrayList<>();
        for (String file : files) {
            if (new File(file).exists()) {
                existingFiles.add(file);
            } else {
                log.warn("Directory [" + file + "] do not exist");
            }
        }
        return existingFiles;
    }
}
