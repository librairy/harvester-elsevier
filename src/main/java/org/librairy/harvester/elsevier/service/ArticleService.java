package org.librairy.harvester.elsevier.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.Predicate;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.librairy.harvester.elsevier.model.Article;
import org.librairy.harvester.elsevier.rest.ElsevierRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class ArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleService.class);

    private static final Predicate validPredicate = context -> true;

    private final ElsevierRestClient client;

    public ArticleService(ElsevierRestClient client){
        this.client = client;
    }

    public Article getByEID(String eid){
        String contentQuery = "/content/article/eid/"+eid+"?view=FULL";

        Article article = new Article();
        article.setEid(eid);
        String response = null;
        try {
            response = client.get(contentQuery);

            DocumentContext jsonArticleContext = JsonPath.parse(response);

            try{
                List<String> keywords   = jsonArticleContext.read("$.full-text-retrieval-response.coredata.dcterms:subject[*].$", validPredicate);
                article.setKeywords(keywords);
            }catch (PathNotFoundException e){
                LOG.warn("keywords not found: " + e.getMessage());
            }

            try{
                String journal            = jsonArticleContext.read("$.full-text-retrieval-response.coredata.prism:publicationName", validPredicate);
                article.setJournal(journal);
            }catch (PathNotFoundException e){
                LOG.warn("Publication Name not found: " + e.getMessage());
            }

            try{
                String title            = jsonArticleContext.read("$.full-text-retrieval-response.coredata.dc:title", validPredicate);
                article.setTitle(title);
            }catch (PathNotFoundException e){
                LOG.warn("Title not found: " + e.getMessage());
            }

            try{
                String fullContent      = jsonArticleContext.read("$.full-text-retrieval-response.originalText", validPredicate);
                article.setFullContent(fullContent);
            }catch (PathNotFoundException e){
                LOG.warn("Full-Content not found: " + e.getMessage());
            }

            try{
                String abstractContent  = jsonArticleContext.read("$.full-text-retrieval-response.coredata.dc:description", validPredicate);
                article.setAbstractContent(abstractContent);
            }catch (PathNotFoundException e){
                LOG.warn("Abstract-Content not found: " + e.getMessage());
            }

            return article;
        } catch (UnirestException e) {
            LOG.error("Error getting article", e);
        }
        return article;
    }

}
