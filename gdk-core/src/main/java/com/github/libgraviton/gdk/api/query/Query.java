package com.github.libgraviton.gdk.api.query;


import java.util.List;

/**
 * Support typed URL queries with the help of query statements.
 *
 * @author List of contributors {@literal <https://github.com/libgraviton/gdk-java/graphs/contributors>}
 * @see <a href="http://swisscom.ch">http://swisscom.ch</a>
 * @version $Id: $Id
 */
public abstract class Query {

    protected List<QueryStatement> statements;

    /**
     * Generate the query based on all the QueryStatement element it contains.
     *
     * @return query as String
     */
    public abstract String generate();

    /**
     * All the QueryStatement elements for this query.
     *
     * @return query statments.
     */
    public List<QueryStatement> getStatements() {
        return statements;
    }

    /**
     * Add a list of QueryStatement to the existing QueryStatement list within this query.
     * @param statements query statements to add
     */
    public void addStatements(List<QueryStatement> statements) {
        this.statements.addAll(statements);
    }
}
