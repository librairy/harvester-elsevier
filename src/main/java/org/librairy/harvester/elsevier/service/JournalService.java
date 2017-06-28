package org.librairy.harvester.elsevier.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.librairy.harvester.elsevier.model.Article;
import org.librairy.harvester.elsevier.rest.ElsevierRestClient;
import org.librairy.harvester.elsevier.rest.LibrairyRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class JournalService {

    private static final Logger LOG = LoggerFactory.getLogger(JournalService.class);

    private final SearchService searchService;

    private final ElsevierRestClient elsevierClient;
    private final LibrairyRestClient librairyClient;
    private final ArticleService articleService;

    public JournalService(Boolean rethoricalAnalysisEnabled){

        this.elsevierClient = new ElsevierRestClient();
        this.librairyClient = new LibrairyRestClient();

        this.searchService  = new SearchService(elsevierClient);
        this.articleService = new ArticleService(elsevierClient);
    }


    public void loadJournal(String journal, Integer maxDocs, Boolean downloadFiles){

        ObjectMapper jsonMapper = new ObjectMapper();

        String id = Arrays.stream(journal.split(" ")).map(w -> w.substring(0,1)).collect(Collectors.joining()).toLowerCase();

        Path path = Paths.get("out",id);



        try {
            // create a domain in librairy
            librairyClient.createDomain(id, journal);

            String filterByJournal = "SRCTITLE("+journal+")";
            if (downloadFiles) Files.createDirectories(path);
            Boolean completed = false;
            Integer index = 0;
            Integer size = (maxDocs < 25)? maxDocs : 25;
            AtomicInteger counter = new AtomicInteger(0);
            while(!completed){

                List<String> docs = this.searchService.listEIDsBy(filterByJournal, size, index);
                index += size;


                docs.stream().forEach( eid -> {
                    try {
                        Article article = this.articleService.getByEID(eid);

                        if (!Strings.isNullOrEmpty(article.getEid())) {
                            counter.incrementAndGet();

                            // create a document in librairy
                            librairyClient.createItem(article.getEid(), article.getTitle(), article.getFullContent());

                            // create a part containing the abstract from article in librairy
                            librairyClient.createPart("abstract", article.getEid(), article.getAbstractContent());

                            // annotate the document with keywords
                            if (!article.getKeywords().isEmpty()) librairyClient.annotateItem(article.getEid(), "keywords", article.getKeywords().stream().map(w -> w.replace(" ", "_")).collect(Collectors.joining(" ")));

                            // add document to domain
                            librairyClient.addItemToDomain(article.getEid(), id);

                            // json serialize
                            if (downloadFiles) {
                                String json = jsonMapper.writeValueAsString(article);

                                Files.write(Paths.get(path.toString(), eid + ".json"), json.getBytes());
                            }
                            LOG.info("Article '" + eid + "' from journal '" + id + "' retrieved");

                        }
                        if (counter.get() >= maxDocs) return;
                    }catch (RuntimeException e){
                        LOG.warn(e.getMessage());
                    }catch (Exception e){
                        LOG.warn("Unexpected error",e);
                    }
                });

                completed = (docs.size() < size) || (counter.get() >= maxDocs);
            }
            librairyClient.updateTopics(id);
        } catch (IOException e) {
            LOG.error("Error getting articles from journal: '" + journal + "'", e);
        } catch (UnirestException e) {
            LOG.error("Error on librairy communication", e);
        }


    }
}
