package org.librairy.harvester.elsevier.rest;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.Predicate;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class ElsevierRestClient {

    private static final Logger LOG = LoggerFactory.getLogger(ElsevierRestClient.class);

    public static final String API_HOST = "elsevier.api.host";

    private static final Predicate validPredicate = context -> true;

    private static final String apiKey = System.getenv("ELSEVIER_API_KEY");

    private final String baseUrl;

    public ElsevierRestClient(){

        this.baseUrl = "http://"+System.getProperty(API_HOST);

        if (Strings.isNullOrEmpty(apiKey)) {
            LOG.error("Environment variable: 'ELSEVIER_API_KEY'  not found!!!" );
            throw new RuntimeException();
        }

    }


    public String get(String query) throws UnirestException {
        HttpResponse<String> response = Unirest.get(baseUrl + query)
                .headers(
                        ImmutableMap.of(
                                "Accept","application/json",
                                "X-ELS-APIKey", apiKey))
                .asString();

        if (response.getStatus() != 200) throw new RuntimeException("Http error: " + response.getStatus() + " by query: '" + query + "'");

        return response.getBody();
    }

}
