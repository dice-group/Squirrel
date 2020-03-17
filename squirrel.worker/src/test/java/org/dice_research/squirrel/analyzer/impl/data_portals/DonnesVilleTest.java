package org.dice_research.squirrel.analyzer.impl.data_portals;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.impl.*;
import org.apache.jena.vocabulary.DCTerms;
import org.dice_research.squirrel.analyzer.impl.data_portals.utils.AbstractDataPortalTest;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.vocab.DCAT;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class DonnesVilleTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriSearchPage = "http://donnees.ville.montreal.qc.ca/group/agriculture-alimentation";
        String uriDetailsPage = "http://donnees.ville.montreal.qc.ca/dataset/inspection-aliments-contrevenants";
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchPage)),
            new File("src/test/resources/html_scraper_analyzer/donnes_ville/search_result_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://donnees.ville.montreal.qc.ca/dataset/inspection-aliments-contrevenants")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://donnees.ville.montreal.qc.ca/dataset/agriculture-urbaine-sondage")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("http://donnees.ville.montreal.qc.ca/dataset/cuisine-de-rue")
                )
            )
        });

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/donnes_ville/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.title,
                    new LiteralImpl(NodeFactory.createLiteral("Inspection des aliments – contrevenants"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.description,
                    new LiteralImpl(NodeFactory.createLiteral("Liste des établissements alimentaires situés sur le territoire de l’agglomération montréalaise et sous la responsabilité de la Division de l’inspection des aliments de la Ville de Montréal ayant fait l’objet d’une condamnation pour une infraction à la Loi sur les produits alimentaires (L.R.Q., c. P-29) et ses règlements."), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.description,
                    new LiteralImpl(NodeFactory.createLiteral("La localisation des contrevenants sur une carte est disponible sur le portail du MAPAQ."), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.publisher,
                    new LiteralImpl(NodeFactory.createLiteral("Service de l'environnement - Division de l'inspection des aliments"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.spatial,
                    new LiteralImpl(NodeFactory.createLiteral("Territoire de la ville de Montréal"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.language,
                    new LiteralImpl(NodeFactory.createLiteral("Français"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.modified,
                    new LiteralImpl(NodeFactory.createLiteral("2019-02-27 17:07"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.license,
                    new ResourceImpl("http://creativecommons.org/licenses/by/4.0/")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCTerms.issued,
                    new LiteralImpl(NodeFactory.createLiteral("2013-10-08 12:19"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("http://donnees.ville.montreal.qc.ca/dataset/inspection-aliments-contrevenants/resource/54d7ffa0-04bf-442c-bacd-a84c6aab888d")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("http://donnees.ville.montreal.qc.ca/dataset/a5c1f0b9-261f-4247-99d8-f28da5000688/resource/54d7ffa0-04bf-442c-bacd-a84c6aab888d/download/inspection-aliments-contrevenants.csv")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("http://donnees.ville.montreal.qc.ca/dataset/inspection-aliments-contrevenants/resource/92719d9b-8bf2-4dfd-b8e0-1021ffcaee2f")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("http://donnees.ville.montreal.qc.ca/dataset/a5c1f0b9-261f-4247-99d8-f28da5000688/resource/92719d9b-8bf2-4dfd-b8e0-1021ffcaee2f/download/inspection-aliments-contrevenants.xml")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("http://donnees.ville.montreal.qc.ca/dataset/inspection-aliments-contrevenants/resource/51026016-7d82-49dc-93e0-2176df8790c6")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    DCAT.downloadURL,
                    new ResourceImpl("http://donnees.ville.montreal.qc.ca/dataset/a5c1f0b9-261f-4247-99d8-f28da5000688/resource/51026016-7d82-49dc-93e0-2176df8790c6/download/inspection-aliments-contrevenants.xlsx")
                )
            )
        });
        return testConfigs;
    }

    public DonnesVilleTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
