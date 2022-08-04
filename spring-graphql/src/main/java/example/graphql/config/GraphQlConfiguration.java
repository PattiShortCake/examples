package example.graphql.config;

import graphql.language.StringValue;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQlConfiguration {

  @Bean
  public RuntimeWiringConfigurer runtimeWiringConfigurer() {
    return builder -> builder.directive("connection", new ConnectionDirectiveWiring());
  }

  static class ConnectionDirectiveWiring implements SchemaDirectiveWiring {

    @Override
    public GraphQLFieldDefinition onField(
        final SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
      final StringValue stringValue = (StringValue) environment.getAppliedDirective("connection")
          .getArgument("for").getArgumentValue().getValue();
      final String type = stringValue.getValue();

      final String fieldName = environment.getFieldDefinition().getName();

      final GraphQLFieldDefinition node = GraphQLFieldDefinition.newFieldDefinition()
          .type(GraphQLTypeReference.typeRef(type))
          .name("node")
          .build();

      final GraphQLObjectType edgesType = GraphQLObjectType.newObject()
          .name(type + "Edges")
          .field(stringField("cursor", false))
          .field(node)
          .build();

      final GraphQLFieldDefinition edges = GraphQLFieldDefinition.newFieldDefinition()
          .type(GraphQLList.list(edgesType))
          .name("edges")
          .build();

      final GraphQLObjectType pageInfoType = GraphQLObjectType.newObject()
          .name(type + "PageInfo")
          .field(booleanField("hasNextPage", true))
          .field(booleanField("hasPreviousPage", true))
          .field(stringField("startCursor", false))
          .field(stringField("endCursor", false))
          .build();

      final GraphQLFieldDefinition pageInfo = GraphQLFieldDefinition.newFieldDefinition()
          .type(pageInfoType)
          .name("pageInfo")
          .build();

      final GraphQLObjectType graphQLObjectType = new Builder()
          .name(type + "Connection")
          .field(edges)
          .field(pageInfo)
          .build();

      return GraphQLFieldDefinition.newFieldDefinition()
          .name(fieldName)
          .type(graphQLObjectType)
          .build();
    }

    private GraphQLFieldDefinition booleanField(final String name, final boolean nonNull) {
      return referenceField(name, "Boolean", nonNull);
    }

    private GraphQLFieldDefinition stringField(final String name, final boolean nonNull) {
      return referenceField(name, "String", nonNull);
    }

    private GraphQLFieldDefinition referenceField(final String name, final String type,
        final boolean nonNull) {
      GraphQLOutputType graphQLOutputType = GraphQLTypeReference.typeRef(type);
      if (nonNull) {
        graphQLOutputType = GraphQLNonNull.nonNull(graphQLOutputType);
      }

      return GraphQLFieldDefinition.newFieldDefinition()
          .type(graphQLOutputType)
          .name(name)
          .build();
    }

  }
}
