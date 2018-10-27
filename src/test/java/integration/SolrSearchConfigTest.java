package integration;

import com.sandbox.solr.sandboxsolr.SandboxSolrApplication;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.CoreContainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = SandboxSolrApplication.class)
@RunWith(SpringRunner.class)
public class SolrSearchConfigTest {

    private EmbeddedSolrServer server;
    private CoreContainer container;

/*    public String getSchemaFile() {
        return "solr/conf/schema.xml";
    }

    public String getSolrConfigFile() {
        return "solr/conf/solrconfig.xml";
    }*/

    @Before
    public void setUp() throws Exception {

        container = new CoreContainer("src/test/java/resources/testdata/solr");
        container.load();

        server = new EmbeddedSolrServer( container, "amazon" );
    }

    @Test
    public void testThatNoResultsAreReturned() throws SolrServerException, IOException {

        SolrParams params = new SolrQuery("text that is not found");
        QueryResponse response = server.query(params);
        assertEquals(0L, response.getResults().getNumFound());
    }

    @Test
    public void testThatDocumentIsFound() throws SolrServerException, IOException {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", "1");
        document.addField("name", "my name");

        server.add(document);
        server.commit();

        SolrParams params = new SolrQuery("name");
        QueryResponse response = server.query(params);
        assertEquals(1L, response.getResults().getNumFound());
        assertEquals("1", response.getResults().get(0).get("id"));
    }
}