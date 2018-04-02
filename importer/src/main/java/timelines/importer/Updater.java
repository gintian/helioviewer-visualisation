package timelines.importer;

/**
 * Used to import new data and update the database as well as the cache
 */
public class Updater {


  public static void main(String[] args) throws Exception {
    Updater updater = new Updater();
    updater.updateResources();
  }


  /**
   * Used to update the database and if needed the cache
   * @throws Exception on error
   */
  private void updateResources() throws Exception {
    Importer importer = new Importer();
    boolean updated = importer.importNewData();

    if(updated) {
      // TODO some calculations, pre-caching
    }
  }

}
