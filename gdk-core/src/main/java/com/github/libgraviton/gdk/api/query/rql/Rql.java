package com.github.libgraviton.gdk.api.query.rql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.libgraviton.gdk.api.query.Query;
import com.github.libgraviton.gdk.api.query.QueryStatement;
import com.github.libgraviton.gdk.api.query.rql.statements.AndOperator;
import com.github.libgraviton.gdk.api.query.rql.statements.Eq;
import com.github.libgraviton.gdk.api.query.rql.statements.Limit;
import com.github.libgraviton.gdk.api.query.rql.statements.Select;
import com.github.libgraviton.gdk.data.GravitonBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Together with the enclosed Builder, it allows to generate RQL queries that can be used for GET requests.
 *
 * @author List of contributors {@literal <https://github.com/libgraviton/gdk-java/graphs/contributors>}
 * @see <a href="http://swisscom.ch">http://swisscom.ch</a>
 * @version $Id: $Id
 */
public class Rql extends Query {

    private Rql(List<QueryStatement> statements) {
        this.statements = statements;
    }

    public static class Builder {

        private Limit limit;

        private Select select;

        private QueryStatement resourceStatement;

        public Rql.Builder setLimit(int numberOfElements, int offset) {
            limit = new Limit(numberOfElements, offset);
            return this;
        }

        public Rql.Builder setLimit(int numberOfElements) {
            limit = new Limit(numberOfElements);
            return this;
        }

        public Rql.Builder addSelect(String attributeName) {
            if (select == null) {
                select = new Select();
            }

            select.add(attributeName);

            return this;
        }

        /**
         * Creates an QueryStatement for each attribute set in the passed 'resource' parameter.
         * If there are more than 1 QueryStatement, they will be AND connected.
         *
         * @param resource resource to generate the statements from
         * @param mapper ObjectMapper
         * @return resulting QueryStatement
         */
        public Rql.Builder setResource(GravitonBase resource, ObjectMapper mapper) {
            JsonNode node = mapper.valueToTree(resource);

            List<QueryStatement> QueryStatements = getQueryStatementsFromNode(node, null);
            switch (QueryStatements.size()) {
                case 0:
                    resourceStatement = null;
                    break;
                case 1:
                    resourceStatement = QueryStatements.get(0);
                    break;
                default:
                    AndOperator andOperator = new AndOperator();
                    andOperator.addStatements(QueryStatements);
                    resourceStatement = andOperator;
                    break;
            }

            return this;
        }

        public Rql build() {
            List<QueryStatement> statements = new ArrayList<>();
            if (resourceStatement != null) {
                statements.add(resourceStatement);
            }
            if (limit != null) {
                statements.add(limit);
            }
            if (select != null) {
                statements.add(select);
            }
            return new Rql(statements);
        }

        protected List<QueryStatement> getQueryStatementsFromNode(JsonNode node, String path) {
            List<QueryStatement> statements = new ArrayList<>();
            Iterator<String> fieldNames = node.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                String currentPath = path == null ? fieldName : path + "." + fieldName;
                JsonNode currentNode = node.get(fieldName);
                if (currentNode.isArray()) {
                    for (JsonNode nodeEntry : currentNode) {
                        statements.addAll(getQueryStatementsFromNode(nodeEntry, currentPath + "."));
                    }
                } else if (currentNode.isObject()) {
                    statements.addAll(getQueryStatementsFromNode(currentNode, currentPath));
                } else {
                    Eq eq = new Eq(currentPath, "string:" + currentNode.textValue());
                    statements.add(eq);
                }
            }

            return statements;
        }
    }
}