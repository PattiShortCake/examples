package example.graphql.config;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLNonNull.nonNull;
import static graphql.schema.GraphQLObjectType.newObject;

import graphql.relay.Relay;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNamedSchemaElement;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.SchemaPrinter;
import graphql.schema.idl.TypeDefinitionRegistry;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration(proxyBeanMethods = false)
public class GraphQlConfiguration {

  @Bean
  public GraphQlSourceBuilderCustomizer sourceBuilderCustomizer() {
    return builder ->
        builder.schemaFactory((typeDefinitionRegistry, runtimeWiring) -> {
          final String pageInfoSDL = new SchemaPrinter().print(Relay.pageInfoType);
          final TypeDefinitionRegistry relayTypeDefinitionRegistry = new SchemaParser().parse(
              pageInfoSDL);

//          RuntimeWiring.newRuntimeWiring()
//          runtimeWiring.bu
//          runtimeWiring.getDirectiveWiring()

//          RuntimeWiring.newRuntimeWiring()..newRuntimeWiring().

          final SchemaGenerator schemaGenerator = new SchemaGenerator();

          return schemaGenerator.makeExecutableSchema(
              relayTypeDefinitionRegistry.merge(typeDefinitionRegistry),
              runtimeWiring);
        })
//            .configureRuntimeWiring( builder2 -> builder2.)
//            .configureRuntimeWiring()
        ;
  }

  @Bean
  public RuntimeWiringConfigurer runtimeWiringConfigurer() {
    return builder -> {
      final Relay relay = new Relay();
      builder
          .directive("connectionForward", new ConnectionDirectiveWiring(
              relay.getForwardPaginationConnectionFieldArguments()))
          .directive("connectionBackward", new ConnectionDirectiveWiring(
              relay.getBackwardPaginationConnectionFieldArguments()))
          .directive("connection",
              new ConnectionDirectiveWiring(relay.getConnectionFieldArguments()));
    };
  }

  static class ConnectionDirectiveWiring implements SchemaDirectiveWiring {

    private final List<GraphQLArgument> paginationArguments;

    public ConnectionDirectiveWiring(final List<GraphQLArgument> paginationArguments) {
      this.paginationArguments = paginationArguments;
    }

    @Override
    public GraphQLFieldDefinition onField(
        final SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
      final String fieldName = environment.getFieldDefinition().getName();
      final String nodeTypeName = determineNodeTypeName(environment);

      final GraphQLTypeReference nodeType = GraphQLTypeReference.typeRef(nodeTypeName);
      final GraphQLObjectType edgeType = edgeType(nodeTypeName, nodeType, Collections.emptyList());
      final GraphQLObjectType connectionType = new Relay().connectionType(nodeTypeName, edgeType,
          Collections.emptyList());

      return GraphQLFieldDefinition.newFieldDefinition()
          .name(fieldName)
          .description("A relay for the " + nodeTypeName + " type")
          .arguments(paginationArguments)
          .type(connectionType)
          .build();
    }

    private static String determineNodeTypeName(
        final SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
      if (environment.getFieldDefinition().getType() instanceof GraphQLNamedSchemaElement s) {
        return s.getName();
      }
      throw new IllegalArgumentException("Could not determine original field definition type");
//      } else {
//        final String directiveName = environment.getDirective().getName();
//        final InputValueWithState inputValueWithState = environment
//            .getAppliedDirective(directiveName).getArgument("for").getArgumentValue();
//
//        if (inputValueWithState.isSet()
//            && inputValueWithState.getValue() instanceof StringValue s) {
//          type = Objects.requireNonNull(s.getValue(), "`for` is required");
//        } else {
//          throw new IllegalArgumentException("Could not determine original field definition type");
//        }
//      }
    }

    public GraphQLObjectType edgeType(final String name, final GraphQLTypeReference nodeType,
        final List<GraphQLFieldDefinition> edgeFields) {
      return newObject()
          .name(name + "Edge")
          .description("An edge in a connection")
          .field(newFieldDefinition()
              .name("node")
              .type(nodeType)
              .description("The item at the end of the edge"))
          .field(newFieldDefinition()
              .name("cursor")
              .type(nonNull(GraphQLString))
              .description("cursor marks a unique position or index into the connection"))
          .fields(edgeFields)
          .build();
    }

  }
}