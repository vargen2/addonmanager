package addonmanager.app.core.net;

import org.apache.http.ProtocolException;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.TextUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class RedirectStrategy extends LaxRedirectStrategy {

    @Override
    protected URI createLocationURI(String location) throws ProtocolException {
        location = location.replaceAll(" ", "+");
        location = location.replaceAll("%2B", "+");
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
