package addonmanager.old;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;


public class Finder extends SimpleFileVisitor<Path> {

    private final PathMatcher matcher;
    private int numMatches;
    private int accessDenies;
    private long files, directories,total;
    boolean onlyDirectories;

    Finder(String pattern,boolean onlyDirectories) {
        this.onlyDirectories=onlyDirectories;
        matcher = FileSystems.getDefault()
                .getPathMatcher("glob:" + pattern);
    }

    // Compares the glob pattern against
    // the file or directory name.
    void find(Path file) {
        total++;
        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            numMatches++;
            System.out.println(file);
        }
    }

    // Prints the total number of
    // matches to standard out.
    void done() {
        System.out.println("Matched: "
                + numMatches+" accessdenies: "+accessDenies+" files: "+files+" directories: "+directories+" total: "+total);
    }

    // Invoke the pattern matching
    // method on each file.
    @Override
    public FileVisitResult visitFile(Path file,
                                     BasicFileAttributes attrs) {

        if (onlyDirectories)
            return FileVisitResult.CONTINUE;
        files++;
        find(file);
        return CONTINUE;
    }


    //
    //Matched: 1 accessdenies: 620 files: 284921 directories: 63142


    // Invoke the pattern matching
    // method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(Path dir,
                                             BasicFileAttributes attrs) {




        //if(dir.startsWith("C:\\ProgramData")||dir.startsWith("C:\\Windows")) {
           // System.out.println(attrs.isSymbolicLink());
            //System.out.println(attrs.isRegularFile());

            //var oo=(WindowsFileSystemProvider)attrs;
            //if(oo !=null)
              //  System.out.println(oo.toString());
//            System.out.println(Files.isExecutable(dir));
//            System.out.println(Files.isReadable(dir));
//            System.out.println(Files.isWritable(dir));
//            try {
//                Files.getPosixFilePermissions(dir).forEach(x->System.out.println(x.toString()));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                var aaa=Files.getOwner(dir);
//                System.out.println(aaa.getName());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            System.out.println(dir.toString());
      //      return SKIP_SUBTREE;
       // }

        directories++;
        find(dir);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file,
                                           IOException exc) {
accessDenies++;
    //return CONTINUE;
        return FileVisitResult.SKIP_SUBTREE;
    }


}
