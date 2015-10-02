package timelines.importer.downloader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import timelines.importer.csv.CsvToGoesSxrLeafConverter;
import timelines.importer.csv.GoesSxrLeaf;
import timelines.utils.StringUtils;
import timelines.utils.TimeUtils;

public class GoesFullDownloader implements IDownloader {

  //	private static final Logger logger = LoggerFactory.getLogger(GoesFullDownloader.class);

  private String hostname = "satdat.ngdc.noaa.gov";

  private String directory = "sem/goes/data/full/xrays/{year}/";

  private String loginUser = "anonymous";

  private String loginPassword = "";

  private String tempDirectoryFolder = "timelineviz";

  private Date goesFullStartDateMidnight;

  private Date goesFullEndDateMidnight;

  private final CsvToGoesSxrLeafConverter csvParser;

  static class Columns {
    public static final int TIMESTAMP = 0;
    public static final int XS = 3;
    public static final int XL = 4;
  }

  public GoesFullDownloader() {

    try {
      goesFullStartDateMidnight = TimeUtils.fromString("1974-01-01", "yyyy-MM-dd");
      goesFullEndDateMidnight = TimeUtils.fromString("1996-08-12", "yyyy-MM-dd");

    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }



//    final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    csvParser = new CsvToGoesSxrLeafConverter(Columns.TIMESTAMP, Columns.XS, Columns.XL, format);
  }

  @Override
  public boolean isSameDownloadSite(Date thisDateMidnight, Date otherDateMidnight) {
    return TimeUtils.getYear(otherDateMidnight)  == TimeUtils.getYear(otherDateMidnight);
  }

  @Override
  public Date getStartDateMidnight() {
    return goesFullStartDateMidnight;
  }

  @Override
  public Date getEndDateMidnight() {
    return goesFullEndDateMidnight;
  }

  @Override
  public Set<GoesSxrLeaf> getGoesSxrLeafs(Date startTimestamp, Date currentDateMidnight) {
    // TODO
    //  Set<GoesSxrLeaf> goesSxrLeafs = new HashSet<GoesSxrLeaf>();
    //  final String currentYear = Integer.toString(currentDateMidnight.getYear());
    //
    //  final FTPClient ftpClient = openFtpClient(currentYear);
    //  if (ftpClient != null) {
    //    // List<File> downloadedFiles = Arrays.asList(new File(
    //    // "C:\\Users\\jonas.lauener\\AppData\\Local\\Temp\\timelineviz\\1996\\").listFiles());
    //    List<File> downloadedFiles = saveDirectory(ftpClient, currentYear);
    //
    //    closeFtpClientQuitly(ftpClient);
    //
    //    if (downloadedFiles.size() > 0) {
    //      for (File file : downloadedFiles) {
    //        Reader fileReader = null;
    //        try {
    //          fileReader = new FileReader(file);
    //          final ReadableInstant maxStartTimestamp = TimeUtils.getMaxReadableInstant(startTimestamp, getStartDateMidnight());
    //          final DateMidnight endTimestamp = getEndDateMidnight();
    //          final Set<GoesSxrLeaf> parsedLeaves = csvParser.parseFile(maxStartTimestamp, endTimestamp, fileReader);
    //          goesSxrLeafs.addAll(parsedLeaves);
    //        } catch (FileNotFoundException e) {
    //          throw new RuntimeException(e);
    //        } catch (IOException e) {
    //          throw new RuntimeException(e);
    //        } finally {
    //          IOUtils.closeQuietly(fileReader);
    //        }
    //      }
    //
    //      final File parentDirectory = downloadedFiles.get(0).getParentFile();
    //      FileUtils.deleteQuietly(parentDirectory);
    //    }
    //  }
    //
    //  return new TreeSet<GoesSxrLeaf>(goesSxrLeafs);
    return null;
  }

  /**
   * Open FTPClient. Catch and print SocketException and IOException.
   *
   * @param year
   * @return FTPCLient with opened and changed working directory.
   */
//  private FTPClient openFtpClient(String year) {
//    FTPClient ftpClient = null;
//    try {
//      ftpClient = new FTPClient();
//      ftpClient.connect(hostname);
//      ftpClient.login(loginUser, loginPassword);
//      ftpClient.enterLocalPassiveMode();
//      ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
//      ftpClient.changeWorkingDirectory(getRemoteDirectory(year));
//    } catch (SocketException e) {
//      throw new RuntimeException(e);
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//    return ftpClient;
//  }

  /**
   * Get directory of <code>year</code>.
   *
   * @param year
   * @return directory of <code>year</code>
   */
  private String getRemoteDirectory(String year) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("year", year);

    return StringUtils.format(directory, params);
  }

  /**
   * Download and Save all CSV-Files of working directory to tempFile in folder
   * <code>year</code>.
   *
   * @param ftpClient
   * @param year
   * @return
   */
//  private List<File> saveDirectory(FTPClient ftpClient, String year) {
//    List<File> tempFiles = new LinkedList<File>();
//    try {
//      FTPFile[] ftpFiles = ftpClient.listFiles();
//      for (FTPFile ftpFile : ftpFiles) {
//        final String ftpFileName = ftpFile.getName();
//
//        if (isFileCsv(ftpFileName)) {
//          File tempFile = getTempFile(year, ftpFileName);
//          final String fileUrl = ftpClient.printWorkingDirectory() + "/" + ftpFileName;
//          downloadFile(ftpClient, fileUrl, tempFile);
//          tempFiles.add(tempFile);
//        }
//      }
//    } catch (SocketException e) {
//      throw new RuntimeException(e);
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//    return tempFiles;
//  }

  /**
   * Download file from fileUrl and save to tempFIle.
   *
   * @param ftpClient
   * @param fileUrl
   * @param tempFile
   * @throws FileNotFoundException
   * @throws IOException
   */
//  private void downloadFile(FTPClient ftpClient, String fileUrl, File tempFile) throws FileNotFoundException, IOException {
//    //		logger.info(DateTime.now().toString() + " - Importing data from: " + fileUrl);
//
//    OutputStream outputStream = new FileOutputStream(tempFile);
//    ftpClient.retrieveFile(fileUrl, outputStream);
//    outputStream.close();
//  }

  /**
   * Get or create temporary file.
   *
   * @param year
   * @param ftpFileName
   * @return temporary file
   */
//  private File getTempFile(String year, final String ftpFileName) {
//    File file = FileUtils.getFile(FileUtils.getTempDirectory(), tempDirectoryFolder, year, ftpFileName);
//    file.getParentFile().mkdirs();
//    return file;
//  }

  /**
   * Checks if fileName ends with ".csv".
   *
   * @param ftpFileName
   * @return if fileName ends with ".csv".
   */
  private boolean isFileCsv(String ftpFileName) {
    return ftpFileName.endsWith(".csv");
  }

  /**
   * Logout and disconnect the ftpClient. Catch and print IOExceptions.
   *
   * @param ftpClient
   */
//  private void closeFtpClientQuitly(FTPClient ftpClient) {
//    try {
//      ftpClient.logout();
//      ftpClient.disconnect();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }

}
