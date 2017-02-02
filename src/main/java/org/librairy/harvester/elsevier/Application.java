package org.librairy.harvester.elsevier;

import com.google.common.collect.Lists;
import org.librairy.harvester.elsevier.rest.ElsevierRestClient;
import org.librairy.harvester.elsevier.rest.LibrairyRestClient;
import org.librairy.harvester.elsevier.service.JournalService;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class Application {


    public static void main(String[] args) throws Exception {


        System.setProperty(ElsevierRestClient.API_HOST, "api.elsevier.com");
        System.setProperty(LibrairyRestClient.API_HOST, "zavijava.dia.fi.upm.es:8180");
//        System.setProperty(LibrairyRestClient.API_HOST, "localhost:8080");


        JournalService journalService = new JournalService();


        Lists.newArrayList(
                "International Immunopharmacology",     // Pharma
                "Evolution and Human Behavior",         // Arts and Humanities
                "Economic Modelling",                   // Economics
                "Advances in Space Research",           // Earth and Planetary Sciences
                "Advances in Engineering Software",     // Computer Science
                "Advances in Applied Mathematics"      // Mathematics
//                "Journal of Power Sources"
        ).parallelStream().forEach(journal -> journalService.loadJournal(journal, 500, false));

    }
}
