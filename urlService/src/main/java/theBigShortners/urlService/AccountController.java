package theBigShortners.urlService;

import static com.google.cloud.bigtable.data.v2.models.Filters.FILTERS;
import static util.AESUtil.encryptAndDecrypt;

import com.google.cloud.bigtable.data.v2.models.Filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import org.springframework.beans.factory.annotation.Autowired;
import util.AESUtil;
import util.Base64Util;
import util.JwtToken;
import util.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    private final AccountRepository repository;
    private final HashMap<String, Account> fakeDB = new HashMap<>();
    private final String projectId = "rice-comp-539-fall-2021";
    private final String instanceId = "comp-539-fall-2021";
    private final String tableId = "tbs_user";
    private BigtableDataClient dataClient;
    AccountController(AccountRepository repo) {
        this.dataClient = null;
        try {
            this.dataClient = BigtableDataClient.create(projectId, instanceId);
        } catch (IOException e) {
            System.out.println(
                    "Unable to initialize service client, as a network error occurred: \n" + e.toString());
        }

        this.repository = repo;
    }


    @PostMapping("/create")

    public String createUser(@RequestBody Account req) throws Exception {
        //
        String family_name = "UserAccount";
        String txt = req.getPassword();
        String appsecret="bigshortner";
        appsecret = MD5.md5(appsecret);
        Integer mode = 1;

        //entropy
        byte[] bytes = encryptAndDecrypt(txt.getBytes("UTF-8"), appsecret, mode);
        String encode = Base64Util.encode(bytes);
        try {
            long timestamp = System.currentTimeMillis() * 1000;
            RowMutation rowMutation = RowMutation.create(tableId, req.getEmail())
                    .setCell(
                            family_name,
                            "name",
                            timestamp,
                            req.getName())
                    .setCell(
                            family_name,
                            "password",
                            timestamp,
                            encode)
                    ;
            this.dataClient.mutateRow(rowMutation);
            log.info("Successfully wrote row %s" , req.getEmail());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        AccountService.saveAccount(req);

        return "Success";
    }

    @PostMapping(value = "/signin")
    public String userSignin(@RequestBody Account req) throws Exception {
        String password = "";
        String txt = req.getPassword();
        String appsecret="bigshortner";
        appsecret = MD5.md5(appsecret);
        Integer mode = 1;
        //entropy
        byte[] bytes = encryptAndDecrypt(txt.getBytes("UTF-8"), appsecret, mode);
        String encode = Base64Util.encode(bytes);
        try {
            Filters.Filter filter =
                    FILTERS
                            .chain()
                            .filter(FILTERS.family().exactMatch("UserAccount"))
                            .filter(FILTERS.qualifier().exactMatch("password"));
            Row row = this.dataClient.readRow(tableId, req.getEmail(), filter);
            password = row.getCells().get(0).getValue().toStringUtf8();
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(encode.equals(password)) {
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("name",req.getEmail());
            dataMap.put("id", req.getId());
            dataMap.put("email", req.getEmail());
            return JwtToken.createToken(dataMap);
        };
        return "invalid password or email address";
    }

    @GetMapping("/list")
    List<Account> all() {
        return repository.findAll();
    }
}
