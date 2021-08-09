package com.pattycake.example.cache.validator;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.TopologyValidator;

import java.util.Collection;

@Slf4j
public class ClusterSizeTopologyValidator implements TopologyValidator {

    private static final long serialVersionUID = -5131743558561577592L;
    private final int expectedNodes;

    public ClusterSizeTopologyValidator(final int expectedNodes) {
        this.expectedNodes = expectedNodes;
    }

    @Override
    public boolean validate(final Collection<ClusterNode> nodes) {
        final int currentNodeSize = nodes.size();
        final boolean isValid = currentNodeSize == expectedNodes;
        if (isValid) {
            log.info("Nodes - current[{}] expected[{}].  Valid[{}]", currentNodeSize, expectedNodes, true);
        } else {
            log.error("Nodes - current[{}] expected[{}].  Valid[{}]", currentNodeSize, expectedNodes, false);
        }
        return isValid;
    }
}
