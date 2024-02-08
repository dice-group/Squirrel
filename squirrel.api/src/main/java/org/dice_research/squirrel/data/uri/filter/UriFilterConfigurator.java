package org.dice_research.squirrel.data.uri.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * 
 * This class represents a Relational Uri Filter for the AND and OR operators It
 * is possible to compose two or more filters and use then with relational
 * operators
 * 
 * The constructor requires at least one @link
 * {org.dice_research.squirrel.data.uri.filter.KnownUriFilter} a list of @link
 * {org.dice_research.squirrel.data.uri.filter.UriFilter} and the Operator. The
 * operator can be <<AND>> or <<OR>>.
 * 
 * If the Operator is <<AND>>, the isUriGood(CrawleableUri) method will return
 * true if all the UriFilters will return true in their respectives
 * isUriGood(CrawleableUri) methods
 * 
 * In case the Operator is <<OR>>, if at least one of then returns true, the
 * isUriGood(CrawleableUri) method will return true.
 * 
 * 
 * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 * @author Michael R&ouml;der (michael.roeder@uni-paderborn.de)
 *
 */
public class UriFilterConfigurator extends AbstractKnownUriFilterDecorator implements UriFilterComposer {

    /**
     * List of additional URI filters.
     */
    private List<UriFilter> listUriFilters;
    /**
     * The operation (AND or OR-based filtering) that will be performed.
     */
    private Predicate<CrawleableUri> operation;

    public UriFilterConfigurator(KnownUriFilter knownUriFilter, List<UriFilter> listUriFilters, String operator) {
        super(knownUriFilter);
        this.listUriFilters = listUriFilters;
        setOperation(operator);
    }

    public UriFilterConfigurator(KnownUriFilter knownUriFilter, String operator) {
        super(knownUriFilter);
        this.listUriFilters = new ArrayList<UriFilter>();
        setOperation(operator);
    }

    protected void setOperation(String operator) {
        if (operator.equals("OR"))
            operation = c -> this.computeOrOperation(c);
        else
            operation = c -> this.computeAndOperation(c);

    }

    @Override
    public boolean isUriGood(CrawleableUri uri) {
        return operation.test(uri);
    }

    private boolean computeAndOperation(CrawleableUri uri) {
        boolean isUrisGood = true;
        for (UriFilter uriFilter : listUriFilters) {
            isUrisGood = isUrisGood && uriFilter.isUriGood(uri);
        }
        return isUrisGood && super.isUriGood(uri);
    }

    private boolean computeOrOperation(CrawleableUri uri) {
        boolean isUrisGood = false;
        for (UriFilter uriFilter : listUriFilters) {
            isUrisGood = isUrisGood || uriFilter.isUriGood(uri);
        }
        return isUrisGood || super.isUriGood(uri);
    }

    @Override
    public KnownUriFilter getKnownUriFilter() {
        return getDecorated();
    }

    @Override
    public void setKnownUriFilter(KnownUriFilter knownUriFilter) {
        super.decorated = knownUriFilter;
    }

}
