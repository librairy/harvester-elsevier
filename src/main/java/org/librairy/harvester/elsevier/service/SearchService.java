package org.librairy.harvester.elsevier.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;
import org.librairy.harvester.elsevier.rest.ElsevierRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(SearchService.class);

    private static final Predicate validPredicate = context -> true;

    private final ElsevierRestClient client;

    public SearchService(ElsevierRestClient client){
        this.client = client;
    }

    public List<String> listEIDsBy(String filter, Integer size, Integer start){


        try {
            String request = new StringBuilder().append("/content/search/scopus?count="+size+"&start="+start+"&field=eid&query=").append(URLEncoder.encode(filter,"UTF-8")).toString();

            String response = client.get(request);
            DocumentContext jsonContext = JsonPath.parse(response);
            return jsonContext.read("$.search-results.entry[*].eid", validPredicate);

        } catch (Exception e) {
            LOG.error("Error on searching", e);
            return Collections.emptyList();
        }
    }
}
