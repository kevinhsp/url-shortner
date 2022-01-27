package theBigShortners.urlService;
import static com.google.cloud.bigtable.data.v2.models.Filters.FILTERS;
import com.google.cloud.bigtable.data.v2.models.Filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.ReadModifyWriteRow;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.bigtable.data.v2.models.RowMutation;

import java.io.IOException;
import java.net.URI;
import java.util.*;

@RestController
@CrossOrigin(origins="*")
public class UrlMapController {
    @Autowired
    private static final Logger log = LoggerFactory.getLogger(UrlMapController.class);
    private final UrlMapRepository repository;
    private final HashMap<String, UrlMap> fakeDB = new HashMap<>();
    private final String projectId = "rice-comp-539-fall-2021";
    private final String instanceId = "comp-539-fall-2021";
    private final String tableId = "tbs_url";
    private BigtableDataClient dataClient;

    UrlMapController(UrlMapRepository repo) {
        this.dataClient = null;
        try {
            this.dataClient = BigtableDataClient.create(projectId, instanceId);
        } catch (IOException e) {
            System.out.println(
                    "Unable to initialize service client, as a network error occurred: \n" + e.toString());
        }

        this.repository = repo;
    }
    // @PostMapping("/url/short", headers = {"content-type=application/x-www-form-urlencoded"})
    /**
     * Generate a key for longUrl, create UrlMap, store them in HashMap
     * @param longUrl url to be shorten
     * @return the UrlMap
     */
    @PostMapping("/url/short")
    UrlMap shortenUrl(@RequestBody String longUrl) {
        String key = Util.keyGen(11);
        try {
            long timestamp = System.currentTimeMillis() * 1000;
            RowMutation rowMutation = RowMutation.create(tableId, key)
                    .setCell(
                            "url_mapping",
                            "long_cell",
                            timestamp,
                            longUrl);
            this.dataClient.mutateRow(rowMutation);
            log.info("Successfully wrote row %s", key);
        } catch(Exception e) {
            e.printStackTrace();
        }
        UrlMap map = new UrlMap(longUrl, key);
        //fakeDB.put(key, map);
        return map;
    }

    @GetMapping(value = "/url/resolve/{key}")
    ResponseEntity<Void> resolveUrl(@PathVariable String key) {
        String longUrl = "";
        try {
            Filters.Filter filter =
                    FILTERS
                            .chain()
                            .filter(FILTERS.family().exactMatch("url_mapping"))
                            .filter(FILTERS.qualifier().exactMatch("long_cell"));
            Row row = this.dataClient.readRow(tableId, key, filter);
            longUrl = row.getCells().get(0).getValue().toStringUtf8();
            int i = 5;
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(longUrl)).build();
    }

    @GetMapping("/url")
    List<UrlMap> all() {
        return new ArrayList<>(fakeDB.values());
        //return repository.findAll();
    }

    @PostMapping("/url")
    UrlMap newUrlMap(@RequestBody UrlMap newMap) {
        fakeDB.put(newMap.getKey(), newMap);
        return newMap;
        //return repository.save(newMap);
    }

    @GetMapping("/url/{id}")
    UrlMap one(@PathVariable Long id) {
        return null;
        //return repository.findById(id).orElseThrow(() -> new UrlNotFoundException(id));
    }

    @PutMapping("/url/{id}")
    UrlMap replaceUrlMap(@RequestBody UrlMap newMap, @PathVariable Long id) {
        return null;
        /*
        return repository.findById(id).map(urlMap -> {
            urlMap.setKey(newMap.getKey());
            return repository.save(urlMap);
        }).orElseGet(() -> {
            newMap.setId(id);
            return repository.save(newMap);
        });
        */
    }

    @DeleteMapping("/url/{id}")
    void deleteUrlMap(@PathVariable Long id) {
       // repository.deleteById(id);
    }

}
