/*************************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
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
 *************************GO-LICENSE-END***********************************/

package com.thoughtworks.go.domain.materials;

import java.io.File;

import com.thoughtworks.go.config.materials.PackageMaterial;
import com.thoughtworks.go.config.materials.ScmMaterial;
import com.thoughtworks.go.domain.MaterialRevision;
import com.thoughtworks.go.config.materials.dependency.DependencyMaterial;
import com.thoughtworks.go.domain.materials.dependency.DependencyMaterialAgent;
import com.thoughtworks.go.domain.materials.packagematerial.PackageMaterialAgent;
import com.thoughtworks.go.remote.AgentIdentifier;
import com.thoughtworks.go.util.command.ProcessOutputStreamConsumer;

public class MaterialAgentFactory {
    private ProcessOutputStreamConsumer consumer;
    private File workingDirectory;
    private final AgentIdentifier agentIdentifier;

    public MaterialAgentFactory(ProcessOutputStreamConsumer consumer, File workingDirectory, AgentIdentifier agentIdentifier) {
        this.consumer = consumer;
        this.workingDirectory = workingDirectory;
        this.agentIdentifier = agentIdentifier;
    }

    public MaterialAgent createAgent(MaterialRevision revision) {
        Material material = revision.getMaterial();
        if (material instanceof DependencyMaterial) {
            return new DependencyMaterialAgent(revision);
        } else if (material instanceof PackageMaterial) {
            return new PackageMaterialAgent();

        } else if (material instanceof ScmMaterial) {
            String destFolderPath = ((ScmMaterial) material).workingdir(workingDirectory).getAbsolutePath();
            return new AbstractMaterialAgent(revision, consumer, workingDirectory, new AgentSubprocessExecutionContext(agentIdentifier, destFolderPath));
        }
        throw new RuntimeException("Could not find MaterialChecker for material = " + material);
    }

}
