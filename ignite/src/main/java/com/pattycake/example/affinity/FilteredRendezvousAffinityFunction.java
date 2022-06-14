package com.pattycake.example.affinity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.ignite.cache.affinity.AffinityFunctionContext;
import org.apache.ignite.cache.affinity.rendezvous.RendezvousAffinityFunction;
import org.apache.ignite.cluster.ClusterNode;
import org.jetbrains.annotations.Nullable;

public class FilteredRendezvousAffinityFunction extends RendezvousAffinityFunction {

  @Override
  public List<List<ClusterNode>> assignPartitions(final AffinityFunctionContext affCtx) {
    return super.assignPartitions(affCtx);
  }

  @Override
  public List<ClusterNode> assignPartition(final int part, final List<ClusterNode> nodes,
      final int backups,
      @Nullable final Map<UUID, Collection<ClusterNode>> neighborhoodCache) {

//    nodes.stream()
//        .filter(i -> i.attribute())
    return super.assignPartition(part, nodes, backups, neighborhoodCache);
  }
}
