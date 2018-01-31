package ee.rosman.github.test.service;

import ee.rosman.github.test.model.DbCounter;
import ee.rosman.github.test.model.DbScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DbService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Charset utf8;
    private MongoOperations mongo;

    DbService(MongoOperations mongo) {
        this.mongo = mongo;
        this.utf8 = Charset.forName("utf-8");
    }

    /**
     * Gets next identifier for collection
     *
     * @param collection the name of collection to get next identifier for
     * @return next identifier
     */
    public int getNextId(String collection) {
        final Query query = Query.query(Criteria.where("_id").is(collection));
        final Update update = new Update().inc("next", 1);
        final FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true).upsert(true);
        return mongo.findAndModify(query, update, options, DbCounter.class).getNext();
    }

    /**
     * Runs not executed mongodb scripts
     *
     * @param resources the script resources
     * @throws IOException if the resource stream could not be opened
     */
    public void runScripts(Resource[] resources) throws IOException {

        final Map<String, DbScript> executedScripts = mongo.findAll(DbScript.class).stream().collect(Collectors.toMap(DbScript::getName, s -> s));

        for (Resource resource : resources) {
            final DbScript script = getScript(resource);
            final DbScript executed = executedScripts.get(script.getName());
            if (null == executed) {
                runScript(script);
            } else if (!executed.getChecksum().equals(script.getChecksum())) {
                resetDb(resources);
                break;
            }
        }
    }

    private void resetDb(Resource[] resources) throws IOException {
        logger.warn("resetting data base");
        mongo.getCollectionNames().forEach(c -> mongo.dropCollection(c));
        for (Resource resource : resources) {
            runScript(getScript(resource));
        }
    }

    private DbScript getScript(Resource resource) throws IOException {
        final String checksum, script;
        final String name = resource.getFilename();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), utf8))) {
            script = br.lines().collect(Collectors.joining(System.lineSeparator()));
            checksum = DigestUtils.md5DigestAsHex(script.getBytes(utf8));
        }
        return DbScript.builder().name(name).checksum(checksum).script(script).date(new Date()).build();
    }

    private void runScript(DbScript dbScript) {
        logger.info("applying script: {}", dbScript.getName());
        final ScriptOperations scriptOps = mongo.scriptOps();
        scriptOps.execute(new ExecutableMongoScript("function() {" + dbScript.getScript() + "}"));
        mongo.save(dbScript);
    }
}
