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

import classycle.Analyser;
import classycle.ClassAttributes;
import classycle.graph.AtomicVertex;
import classycle.graph.Vertex;
import classycle.util.StringPattern;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.List;

import static com.mobilepetroleum.radialencapsulation.StringPatterns.exclude;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_CLASSES;

/**
 * Radial encapsulation mojo implementation.
 */
@Mojo(name = "radial-encapsulation", defaultPhase = PROCESS_CLASSES)
public class RadialEncapsulation extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true)
    MavenProject project;

    @Parameter(property = "includeTestSources", defaultValue = "true")
    boolean includeTestSources;

    @Parameter(property = "excludes")
    String[] excludes = new String[0];

    @Parameter(property = "basePackage", required = true)
    String basePackage;

    @Parameter(property = "maxViolations")
    Integer maxViolations;

    StringPattern excludePattern;

    @Override
    public void execute() throws MojoExecutionException {
        excludePattern = exclude(excludes);

        List<String> dirs = new ArrayList<>();
        dirs.add(project.getBuild().getOutputDirectory());

        if (includeTestSources) {
            dirs.add(project.getBuild().getTestOutputDirectory());
        }

        dirs = Files.removeMissingFiles(dirs, getLog());

        Analyser analyser = new Analyser(dirs.toArray(new String[dirs.size()]), excludePattern, null, true);

        AtomicVertex[] classGraph = analyser.getClassGraph();
        int violations = 0;
        for (AtomicVertex currentVertex : classGraph) {
            String inspectedType = ((ClassAttributes) currentVertex.getAttributes()).getName();

            int numberOfIncomingArcs = currentVertex.getNumberOfOutgoingArcs();
            for (int i = 0; i < numberOfIncomingArcs; i++) {
                Vertex vertex = currentVertex.getHeadVertex(i);
                ClassAttributes dependency = (ClassAttributes) vertex.getAttributes();
                String dependencyType = dependency.getName();
                if (!accepts(inspectedType, dependencyType)) {
                    getLog().info(inspectedType + " -> " + dependencyType);
                    violations++;
                }
            }
        }

        if (violations == 0) {
            getLog().info("No violations found");
        } else {
            getLog().info("Found " + violations + " violations");
        }

        if (maxViolations != null && violations > maxViolations.intValue()) {
            String message = "Exceeded max violations. Max violations = " + maxViolations + ", violations = " + violations;
            throw new MojoExecutionException(message);
        }
    }

    boolean accepts(String base, String dependency) {
        if (!dependency.startsWith(basePackage) || !excludePattern.matches(dependency)) {
            return true;
        }
        base = base.substring(0, base.lastIndexOf('.'));
        dependency = dependency.substring(0, dependency.lastIndexOf('.'));
        return base.startsWith(dependency);
    }
}
