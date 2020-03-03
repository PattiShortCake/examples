package com.example.dependency.model;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Builder
@Getter
@ToString
@EqualsAndHashCode(exclude = {"scope"})
public class Dependency {

  private static final Pattern VERSION_PATTERN = Pattern.compile("[\\[\\(]([^,]*),([^\\)\\]]*)[\\)\\]]");

  @NonNull
  private final String groupId;

  @NonNull
  private final String artifactId;

  @NonNull
  private final String version;
  private final String scope;
  private final String classifier;

  public String getGroupId() {
    return StringUtils.trim(groupId);
  }

  public String getArtifactId() {
    return StringUtils.trim(artifactId);
  }

  public String getVersion() {
    final Matcher matcher = VERSION_PATTERN.matcher(version);
    if (matcher.matches()) {
      if (matcher.group(1).isEmpty()) {
        return StringUtils.trim(matcher.group(2));
      }
      return StringUtils.trim(matcher.group(1));
    }
    return version;
  }

  public Optional<String> getScope() {
    return Optional.ofNullable(scope);
  }

  public Optional<String> getClassifier() {
    return Optional.ofNullable(classifier);
  }

  public String toUri() {
    final String artifactId = getArtifactId();
    final String version = getVersion();
    return new StringBuilder()
        .append(StringUtils.replaceChars(getGroupId(), '.', '/'))
        .append('/')
        .append(artifactId)
        .append('/')
        .append(version)
        .append('/')
        .append(artifactId)
        .append('-')
        .append(version)
        .toString();
  }
}
