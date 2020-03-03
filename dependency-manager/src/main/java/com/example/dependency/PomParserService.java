package com.example.dependency;

import com.example.dependency.model.Dependency;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.pom.x400.Model;
import org.apache.maven.pom.x400.Model.Properties;
import org.apache.maven.pom.x400.Parent;
import org.apache.maven.pom.x400.ProjectDocument;
import org.apache.maven.pom.x400.ProjectDocument.Factory;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.TokenType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Node;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PomParserService {

  private static final WebClient webClientMaven = buildWebClient("https://repo1.maven.org/maven2/");
  private static final WebClient webClientScijava = buildWebClient("https://maven.scijava.org/content/repositories/public/");
  private static final WebClient webClientMogwai = buildWebClient("http://mogwai.sourceforge.net/repository/maven2/");
  private static final WebClient webClientSpringSnapshot = buildWebClient("https://repo.spring.io/libs-snapshot/");
  private static final WebClient webClientCubrid = buildWebClient("http://maven.cubrid.org/artifactory/list/ext-release-local/");


  private static final Logger LOGGER = LoggerFactory.getLogger(PomParserService.class);
  private static final Pattern PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

  private static WebClient buildWebClient(final String baseUrl) {
    return WebClient
        .builder()
        .exchangeStrategies(
            ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 10))
                .build()
        )
        .baseUrl(baseUrl)
        .build();
  }

  static Mono<ClientResponse> exchangeToMono(final String uri) {
    return exchangePom(webClientMaven, uri)
        .switchIfEmpty(exchangePom(webClientScijava, uri))
        .switchIfEmpty(exchangePom(webClientMogwai, uri))
        .switchIfEmpty(exchangePom(webClientSpringSnapshot, uri))
        .switchIfEmpty(exchangePom(webClientCubrid, uri))
        .doOnError(throwable -> LOGGER.error("That's weird, hopefully this is gracefully is handled", throwable))
//        .onErrorMap(Exception.class, e -> Mono.empty())
//        .onErrorReturn(null)
        ;
  }

  private static Mono<ClientResponse> exchangePom(final WebClient webClient, final String uri) {
    return webClient.get()
        .uri(uri)
        .exchange()
        .filter(PomParserService::isResponseOk);
  }

  private static boolean isResponseOk(final ClientResponse response) {
    return HttpStatus.OK.equals(response.statusCode());
  }

  public List<Dependency> run(final String xmlAsString) {

    final ProjectDocument projectDocument = toProjectDocument(xmlAsString);
    final Model project = projectDocument.getProject();

    final Map<String, String> properties = toPropertyMap(projectDocument);
    final List<Dependency> dependencyList = new LinkedList<>();

    if (project.getDependencyManagement() != null) {
      dependencyList.addAll(collectDependencies(properties, project.getDependencyManagement().getDependencies().getDependencyArray()));
    }

    if (project.getDependencies() != null) {
      dependencyList.addAll(collectDependencies(properties, project.getDependencies().getDependencyArray()));
    }

    return dependencyList;
  }

  private ProjectDocument toProjectDocument(final String xmlAsString) {
    final XmlOptions xmlOptions = new XmlOptions();
    final Map<String, String> substituteNamespaces = new HashMap<>(1);
    substituteNamespaces.put("", "http://maven.apache.org/POM/4.0.0");
    xmlOptions.setLoadSubstituteNamespaces(substituteNamespaces);

    try {
      final String xmlAsStringClean = xmlAsString.replaceAll("&nbsp;", " ")
          .replaceAll("&oslash;", "ø");
      return Factory.parse(xmlAsStringClean, xmlOptions);
    } catch (final XmlException e) {
      throw new IllegalStateException("Error parsing xmlAsString[" + xmlAsString + "]", e);
    }
  }

  private Optional<ProjectDocument> fetchProjectDocument(final Dependency dependency) {
    final String uri = dependency.toUri() + ".pom";
    try {
      return exchangeToMono(uri)
          .blockOptional()
          .map(clientResponse -> clientResponse.bodyToMono(String.class))
          .map(Mono::block)
          .map(this::toProjectDocument)
          ;
    } catch (final Exception e) {
      throw new IllegalStateException("Error when processing dependency[" + dependency + "] from URI[" + uri + "]", e);
    }
  }

  private List<Dependency> collectDependencies(
      final Map<String, String> properties,
      final org.apache.maven.pom.x400.Dependency[] dependencyArray
  ) {
    return Flux.fromArray(dependencyArray)
        .filter(this::filterDependency)
        .map(dependency -> buildDependency(properties, dependency)
        )
//        .onErrorContinue((t, o) -> LOGGER.error("Something bad happen when processing {}", o))
        .toStream()
        .collect(Collectors.toList());
  }

  private Dependency buildDependency(final Map<String, String> properties, final org.apache.maven.pom.x400.Dependency dependency) {
    if ("geode-lucene-test".equalsIgnoreCase(dependency.getArtifactId())
//        && dependency.getVersion().contains("${")
    ) {
      LOGGER.info("snappy-java");
    }

    return Dependency.builder()
        .artifactId(findResolvedProperty(properties, dependency.getArtifactId()).orElseThrow(() -> new IllegalArgumentException("Unable to resolve artifactId[" + dependency.getArtifactId() +
            "]")))
        .groupId(findResolvedProperty(properties, dependency.getGroupId()).orElseThrow(() -> new IllegalArgumentException("Unable to resolve groupId[" + dependency.getGroupId() + "]")))
        .version(findResolvedProperty(properties, dependency.getVersion()).orElseThrow(() -> new IllegalArgumentException("Unable to resolve version[" + dependency.getVersion() + "]")))
        .scope(dependency.getScope())
        .classifier(dependency.getClassifier())
        .build();
  }


  private boolean filterDependency(final org.apache.maven.pom.x400.Dependency dependency) {
    if (
        StringUtils.isBlank(dependency.getVersion())
            || "test".equalsIgnoreCase(dependency.getScope())
            || "provided".equalsIgnoreCase(dependency.getScope())
            || ("war".equalsIgnoreCase(dependency.getType()) && "true".equalsIgnoreCase(dependency.getOptional()))
    ) {
      return false;
    }
    return true;
  }

  private Map<String, String> toPropertyMap(final ProjectDocument projectDocument) {
    final Map<String, String> map = new LinkedHashMap<>();
    map.put("java.version", "1.8");

    final Model project = projectDocument.getProject();
    final Parent parent = project.getParent();

    if (parent != null) {
      final Dependency parentDependency = Dependency.builder()
          .groupId(parent.getGroupId())
          .artifactId(parent.getArtifactId())
          .version(parent.getVersion())
          .build();

      final Optional<ProjectDocument> optionalProjectDocument = fetchProjectDocument(parentDependency);
      if (optionalProjectDocument.isPresent()) {
        map.putAll(toPropertyMap(optionalProjectDocument.get()));
        try {
          FileUtils.writeStringToFile(new File("not-found.txt"), parentDependency.toUri(), StandardCharsets.UTF_8, true);
        } catch (final IOException e) {
          LOGGER.error("Something bad happen when writing to file", e);
        }
      }

      map.put("project.parent.artifactId", parent.getArtifactId());
      map.put("project.parent.groupId", parent.getGroupId());
      map.put("project.parent.version", parent.getVersion());
    }

    final Properties properties = project.getProperties();
    if (properties != null) {
      final XmlCursor xmlCursor = properties.newCursor();
      do {
        final TokenType tokenType = xmlCursor.toNextToken();
        final Optional<String> parentNodeName = parentNodeName(xmlCursor.getDomNode());
        final Optional<String> grandParentNodeName = parentParentNodeName(xmlCursor.getDomNode());
        if (tokenType.isText() && grandParentNodeName.filter("properties"::equalsIgnoreCase).isPresent()) {
          map.put(parentNodeName.get(), xmlCursor.getTextValue());
        }
      } while (xmlCursor.hasNextToken());
    }

    putOnCondition(map, "project.version", project.getVersion(), StringUtils::isNotBlank);
    putOnCondition(map, "version", project.getVersion(), StringUtils::isNotBlank);
    putOnCondition(map, "pom.version", project.getVersion(), StringUtils::isNotBlank);
//    putOnCondition(map, "pom.version", project.getVersion(), "pom".equalsIgnoreCase(projectDocument.getProject().getPackaging()));

    putOnCondition(map, "project.groupId", project.getGroupId(), StringUtils::isNotBlank);
    putOnCondition(map, "groupId", project.getGroupId(), StringUtils::isNotBlank);
    putOnCondition(map, "pom.groupId", project.getGroupId(), StringUtils::isNotBlank);
//    putOnCondition(map, "pom.groupId", project.getGroupId(), "pom".equalsIgnoreCase(projectDocument.getProject().getPackaging()));

    putOnCondition(map, "project.artifactId", project.getArtifactId(), StringUtils::isNotBlank);
    putOnCondition(map, "artifactId", project.getArtifactId(), StringUtils::isNotBlank);
    putOnCondition(map, "pom.artifactId", project.getArtifactId(), StringUtils::isNotBlank);
//    putOnCondition(map, "pom.artifactId", project.getArtifactId(), "pom".equalsIgnoreCase(projectDocument.getProject().getPackaging()));

    return map;
  }

  private void putOnCondition(final Map<String, String> map, final String key, final String value, final boolean shouldAdd) {
    if (shouldAdd) {
      map.put(key, StringUtils.trim(value.replace("%20", " ")));
    }
  }

  private void putOnCondition(final Map<String, String> map, final String key, final String value, final Predicate<String> valuePredicate) {
    putOnCondition(map, key, value, valuePredicate.test(value));
  }

  private Optional<String> findResolvedProperty(final Map<String, String> source, final String propertyToFind) {
    if (propertyToFind != null) {
      final Matcher matcher = PATTERN.matcher(propertyToFind);
      if (matcher.find()) {
        final String propertyName = matcher.group(1);
        final String variable = "${" + propertyName + "}";
        final String replacementValue = source.get(propertyName);
        if (replacementValue == null) {
          throw new IllegalStateException("Did not find replacement value for variable[" + variable + "]");
        }
        final String propertyValue = propertyToFind.replace(variable, replacementValue);
        return findResolvedProperty(source, propertyValue);
      }
    }
    return Optional.ofNullable(propertyToFind);
  }

  private Optional<String> parentParentNodeName(final Node node) {
    return parentNode(node)
        .map(Node::getParentNode)
        .filter(Objects::nonNull)
        .map(Node::getNodeName)
        .filter(StringUtils::isNotBlank);
  }

  private Optional<String> parentNodeName(final Node node) {
    return parentNode(node)
        .map(Node::getNodeName)
        .filter(StringUtils::isNotBlank);
  }

  private Optional<Node> parentNode(final Node node) {
    Node parentNode = null;
    if (node != null) {
      parentNode = node.getParentNode();
    }
    return Optional.ofNullable(parentNode);
  }
}
