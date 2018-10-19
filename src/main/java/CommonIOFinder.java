import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommonIOFinder extends DirectoryWalker<File> {

    long counter;

    public CommonIOFinder(){
        super();
    }

    public CommonIOFinder(IOFileFilter dirFilter) {
        super(dirFilter, -1);

    }

    public ArrayList<File> getDirectories(File startDirectory) throws IOException {
        ArrayList<File> dirs = new ArrayList<File>();
        walk(startDirectory, dirs);
        System.out.println(counter);
        return dirs;
    }

    @Override
    protected boolean handleDirectory(File directory, int depth,
                                      Collection<File> results) {
        if(directory.getPath().contains("World of Warcraft"))
            results.add(directory);
        counter++;
        return true;
    }
}
// Use the filters to construct the walker
//    FooDirectoryWalker walker = new FooDirectoryWalker(
//            HiddenFileFilter.VISIBLE,
//            FileFilterUtils.suffixFileFilter(".txt"),
//            );

