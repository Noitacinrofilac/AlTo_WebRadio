package alto.plugin.webradio.managerTest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import alto.plugin.webradio.manager.CollaborativeManager;
import alto.plugin.webradio.manager.DataManager;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

public class CollaborativeManagerTest {

	private static final MongodStarter starter = MongodStarter.getDefaultInstance();

    private MongodExecutable _mongodExe;
    private MongodProcess _mongod;
    
	DataManager dataManager;
	CollaborativeManager colManager;
	private MongoClient _mongo;
	
	@Before
    public void setUp() throws Exception {

        _mongodExe = starter.prepare(new MongodConfigBuilder()
            .version(Version.Main.PRODUCTION)
            .net(new Net(12345, Network.localhostIsIPv6()))
            .build());
        _mongod = _mongodExe.start();

        _mongo = new MongoClient("localhost", 12345);
        dataManager = new DataManager();
        colManager = new CollaborativeManager();
        dataManager.registerUser("luis");
    }

	@After
    public void tearDown() throws Exception {
        dataManager.removeUser("luis");
        _mongod.stop();
        _mongodExe.stop();
    }

    public Mongo getMongo() {
        return _mongo;
    }
    
	@Test
	public void createModelTest() {
		dataManager.saveUserRate("luis", "arcade fire", "reflektor", 1, null);
		dataManager.saveUserRate("luis", "arcade fire", "sprawl II", 0, null);
		colManager.createDataModel();
		assertTrue(true);
	}

}
