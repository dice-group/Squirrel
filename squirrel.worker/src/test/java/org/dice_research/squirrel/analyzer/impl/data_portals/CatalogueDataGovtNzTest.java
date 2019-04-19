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
public class CatalogueDataGovtNzTest extends AbstractDataPortalTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException {
        List<Object[]> testConfigs = new ArrayList<Object[]>();
        String uriSearchPage = "https://catalogue.data.govt.nz/dataset?q=&sort=score+desc%2C+metadata_modified+desc";
        String uriDetailsPage = "https://catalogue.data.govt.nz/dataset/directory-of-educational-institutions";
        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriSearchPage)),
            new File("src/test/resources/html_scraper_analyzer/catalogue_data_govt_nz/search_result_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/directory-of-educational-institutions")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/emergency-management-basemap")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/nzflora-brownsey-perrie-2018-davalliaceae")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/nzflora-fife-2018-ptychomitriaceae")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/nzflora-brownsey-perrie-2015-ophioglossaceae")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/phylogeny-of-new-zealand-ascomycetes")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/nzflora-fife-2018-rhabdoweisiaceae")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/nzflora-fife-2015-meesiaceae")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/nzflora-brownsey-perrie-2018-selaginellaceae")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/nzflora-brownsey-perrie-2018-tectariaceae")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/regional-gross-domestic-product")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/family-services-directory")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/pharmaceutical-schedule-database")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/pharmac-chief-executive-expenses")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/international-migration")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/international-travel")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/accommodation-survey")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/gambling-expenditure-statistics")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/gaming-machine-statistics")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#link"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/food-price-index")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset?page=2")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset?page=3")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset?page=1")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset?page=327")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriSearchPage),
                    new PropertyImpl("http://projekt-opal.de/dataset#pagination"),
                    new ResourceImpl("https://catalogue.data.govt.nz#")
                )
            )
        });

        testConfigs.add(new Object[]{
            new CrawleableUri(new URI(uriDetailsPage)),
            new File("src/test/resources/html_scraper_analyzer/catalogue_data_govt_nz/details_page.html"),
            ModelFactory.createDefaultModel().add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.title.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Directory of Educational Institutions"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.description.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("The following key areas summarise the overall focus of the Ministry's work: 1) More children gaining strong learning foundations; 2) More students participating in and achieving... read more"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.publisher.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Ministry of Education"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.creator.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Ministry of Education"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.modified.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("2018-10-31"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.issued.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("Unknown"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl("http://www.w3.org/ns/dcat#distribution"),
                    new ResourceImpl("http://www.educationcounts.govt.nz/statistics/tertiary_education/27436")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.language.toString()),
                    new LiteralImpl(NodeFactory.createLiteral("English"), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCTerms.spatial.toString()),
                    new LiteralImpl(NodeFactory.createLiteral(""), null)
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/2756db90-a096-4ffa-9fb4-73b74ad279d9/resource/26f44973-b06d-479d-b697-8d7943c97c57/download/ecedirectory-19-03-2019-110021.csv")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("https://www.educationcounts.govt.nz/__data/assets/file/0009/63873/Directory-Maori-Medium-current.csv")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("https://www.educationcounts.govt.nz/__data/assets/file/0004/74344/Directory-Private-Schools-Current.csv")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("https://catalogue.data.govt.nz/dataset/2756db90-a096-4ffa-9fb4-73b74ad279d9/resource/bdfe0e4c-1554-4701-a8fe-ba1c8e0cc2ce/download/schooldirectory-19-03-2019-233049.csv")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("https://www.educationcounts.govt.nz/__data/assets/file/0005/62573/Directory-Tertiary-Current.csv")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0008/170684/School-Closures_Mergers-and-New_Nov_2016.xlsx")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0007/162259/School-Closures_Mergers-and-New_Dec_2015.xlsx")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0008/145709/School-Closures_Mergers-and-New_Nov_2014.xlsx")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0009/115758/School-Closures_Mergers-and-New_Nov_Dec_2013.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0014/107330/School-Closures_Mergers-and-New_Dec_2012.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0009/101313/School-Closures_Mergers-and-New_Dec_2011.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0018/71505/School-Closures-Mergers-New-2010.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0010/38962/School-Closures-Mergers-New-2009.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://www.educationcounts.govt.nz/__data/assets/excel_doc/0008/33983/School_Closures_Mergers_and_New_December_2008.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://www.educationcounts.govt.nz/__data/assets/file/0003/7770/Edcounts2007.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://www.educationcounts.govt.nz/__data/assets/file/0006/7773/mergers-closures-new-sch-jan06-jan07.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("http://www.educationcounts.govt.nz/__data/assets/file/0005/7772/mergers-closures-new-sch-jan05-jan06.xls")
                )
            ).add(
                new StatementImpl(
                    new ResourceImpl(uriDetailsPage),
                    new PropertyImpl(DCAT.downloadURL.toString()),
                    new ResourceImpl("https://www.educationcounts.govt.nz/__data/assets/file/0004/7771/mergers-closures-new-sch-jan04-jan05.xls")
                )
            )
        });
        return testConfigs;
    }

    public CatalogueDataGovtNzTest(CrawleableUri uri, File fileToScrape, ModelCom expectedModel) {
        super(uri, fileToScrape, expectedModel);
    }
}
