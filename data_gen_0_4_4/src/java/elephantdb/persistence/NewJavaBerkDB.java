package elephantdb.persistence;

import com.sleepycat.je.*;
import elephantdb.document.NewKeyValDocument;
import elephantdb.Utils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class NewJavaBerkDB implements Coordinator {
    public static Logger LOG = Logger.getLogger(NewJavaBerkDB.class);

    public NewJavaBerkDB() {
        super();
    }

    public Persistence openPersistenceForRead(String root, Map options) throws IOException {
        return new NewJavaBerkDBPersistence(root, options, true, false);
    }

    public Persistence openPersistenceForAppend(String root, Map options) throws IOException {
        return new NewJavaBerkDBPersistence(root, options, false, false);
    }

    public Persistence createPersistence(String root, Map options) throws IOException {
        NewJavaBerkDBPersistence ret = new NewJavaBerkDBPersistence(root, options, false, true);
        ret.close();
        return ret;
    }

    public static class NewJavaBerkDBPersistence implements NewKeyValPersistence {
        private static final String DATABASE_NAME = "elephant";
        Environment env;
        Database db;
        public NewJavaBerkDBPersistence(String root, Map options,
                                     boolean readOnly, boolean allowCreate) {

            new File(root).mkdirs();
            EnvironmentConfig envConf = new EnvironmentConfig();
            envConf.setAllowCreate(allowCreate);

            envConf.setReadOnly(readOnly);
            envConf.setLocking(false);
            envConf.setTransactional(false);
            envConf.setSharedCache(true);

            // TODO: Loop through options, setConfigParam for each one.
            envConf.setConfigParam(EnvironmentConfig.CLEANER_MIN_UTILIZATION, "10");
            envConf.setConfigParam(EnvironmentConfig.CLEANER_MIN_FILE_UTILIZATION, "5");
            envConf.setConfigParam(EnvironmentConfig.ENV_RUN_CLEANER, "false");
            envConf.setConfigParam(EnvironmentConfig.LOG_FILE_MAX, "104857600"); // 100 MB

            envConf.setConfigParam(EnvironmentConfig.FILE_LOGGING_LEVEL, "ALL");
            envConf.setConfigParam(EnvironmentConfig.CONSOLE_LOGGING_LEVEL, "ALL");
            
            env = new Environment(new File(root), envConf);
            
            DatabaseConfig dbConf = new DatabaseConfig();
            dbConf.setAllowCreate(allowCreate);
            dbConf.setReadOnly(readOnly);
            dbConf.setDeferredWrite(true);
            
            dbConf.setNodeMaxEntries(512);

            db = env.openDatabase(null, DATABASE_NAME, dbConf);
        }

        public byte[] get(byte[] key) throws IOException {
            EnvironmentConfig envConf = env.getConfig();
            
            DatabaseEntry chrysalis = new DatabaseEntry();

            OperationStatus stat = db.get(null, new DatabaseEntry(key), chrysalis, LockMode.READ_UNCOMMITTED);
            if (stat == OperationStatus.SUCCESS) {
                return chrysalis.getData();
            } else {
                LOG.debug("Lookup failed in " + env.getHome() + ": " + stat);
                return null;
            }
        }

        public void put(byte[] key, byte[] value) throws IOException {
            index(new NewKeyValDocument(key, value));
        }

        private void add(byte[] key, byte[] value) throws IOException {
            db.put(null, new DatabaseEntry(key), new DatabaseEntry(value));
        }

        public void index(NewKeyValDocument document) throws IOException {
            add(document.key, document.value);
        }

        public void close() throws IOException {
            if (!db.getConfig().getReadOnly()) {
                LOG.info("Syncing environment at " + env.getHome().getPath());
                env.sync();
                LOG.info("Done syncing environment at " + env.getHome().getPath());

                LOG.info("Cleaning environment log at " + env.getHome().getPath());
                boolean anyCleaned = false;
                while (env.cleanLog() > 0) {
                    anyCleaned = true;
                }
                LOG.info("Done cleaning environment log at " + env.getHome().getPath());
                if (anyCleaned) {
                    LOG.info("Checkpointing environment at " + env.getHome().getPath());
                    CheckpointConfig checkpoint = new CheckpointConfig();
                    checkpoint.setForce(true);
                    env.checkpoint(checkpoint);
                    LOG.info("Done checkpointing environment at " + env.getHome().getPath());
                }
            }

            db.close();
            env.close();
        }

        public CloseableIterator<NewKeyValDocument> iterator() {
            return new CloseableIterator<NewKeyValDocument>() {
                Cursor cursor = null;
                NewKeyValDocument next = null;

                private void cacheNext() {
                    DatabaseEntry key = new DatabaseEntry();
                    DatabaseEntry val = new DatabaseEntry();

                    // cursor stores the next key and value in the above mutable objects.
                    OperationStatus stat = cursor.getNext(key, val, LockMode.READ_UNCOMMITTED);
                    if (stat == OperationStatus.SUCCESS) {
                        next = new NewKeyValDocument(key.getData(), val.getData());
                    } else {
                        next = null;
                        close();
                    }
                }

                private void initCursor() {
                    if (cursor == null) {
                        cursor = db.openCursor(null, null);
                        cacheNext();
                    }
                }

                public boolean hasNext() {
                    initCursor();
                    return next != null;
                }

                public NewKeyValDocument next() {
                    initCursor();
                    if (next == null) { throw new RuntimeException("No key/value pair available"); }
                    NewKeyValDocument ret = next;
                    cacheNext(); // caches up n + 1,
                    return ret;  // return the old.
                }

                public void remove() {
                    throw new UnsupportedOperationException("Not supported.");
                }

                public void close() {
                    if (cursor != null)
                        cursor.close();
                }

            };
        }
    }

}