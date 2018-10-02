import org.apache.http.ProtocolException;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.TextUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class MyRedirect extends LaxRedirectStrategy {

    @Override
    protected URI createLocationURI(String location) throws ProtocolException {
        //System.out.println("before: " + location);
        location = location.replaceAll(" ", "+");
        location = location.replaceAll("%2B", "+");
        //System.out.println("AFTER: " + location);

        //Experi.tempFilename = location.substring(location.lastIndexOf("/")+1).replaceAll("\\+","-");

        //System.out.println("redir: "+Experi.tempFilename);
        try {
            URIBuilder b = new URIBuilder((new URI(location)).normalize());
            String host = b.getHost();
            if (host != null) {
                b.setHost(host.toLowerCase(Locale.ROOT));
            }

            String path = b.getPath();
            if (TextUtils.isEmpty(path)) {
                b.setPath("/");
            }

            return b.build();
        } catch (URISyntaxException var5) {
            throw new ProtocolException("Invalid redirect URI: " + location, var5);
        }
    }
}
