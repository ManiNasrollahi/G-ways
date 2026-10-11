package org.springframework.cloud.gateway.server.mvc.filter;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import com.Gways.configuration.IpResolver;
import com.Gways.configuration.RuntimeConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.server.mvc.common.MvcUtils;
import org.springframework.core.Ordered;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.function.ServerRequest;

@Component
public class XForwardedRequestHeadersFilter implements HttpHeadersFilter.RequestHttpHeadersFilter, Ordered {
    private static final Log log = LogFactory.getLog(XForwardedRequestHeadersFilter.class);
    public static final int HTTP_PORT = 80;
    public static final int HTTPS_PORT = 443;
    public static final String HTTP_SCHEME = "http";
    public static final String HTTPS_SCHEME = "https";
    public static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
    public static final String X_FORWARDED_HOST_HEADER = "X-Forwarded-Host";
    public static final String X_FORWARDED_PORT_HEADER = "X-Forwarded-Port";
    public static final String X_FORWARDED_PROTO_HEADER = "X-Forwarded-Proto";
    public static final String X_FORWARDED_PREFIX_HEADER = "X-Forwarded-Prefix";
    private final XForwardedRequestHeadersFilterProperties properties;

    private RuntimeConfiguration runtimeConfiguration;
    private IpResolver ipResolver;

    @Autowired
    public void setRuntimeConfiguration(RuntimeConfiguration runtimeConfiguration) {
        this.runtimeConfiguration = runtimeConfiguration;
    }

    @Autowired
    public void setIpResolver(IpResolver ipResolver) {
        this.ipResolver = ipResolver;
    }

    /** @deprecated */
    @Deprecated
    public XForwardedRequestHeadersFilter(XForwardedRequestHeadersFilterProperties properties) {
        this(properties, (TrustedProxies)((s) -> true));
        log.warn("spring.cloud.gateway.server.webmvc.trusted-proxies is not set. Using deprecated Constructor. Untrusted hosts might be added to X-Forwarded header.");
    }

    public XForwardedRequestHeadersFilter(XForwardedRequestHeadersFilterProperties props, String trustedProxies) {
        this(props, TrustedProxies.from(trustedProxies));
    }

    private XForwardedRequestHeadersFilter(XForwardedRequestHeadersFilterProperties props, TrustedProxies trustedProxies) {
        Objects.requireNonNull(props, "XForwardedRequestHeadersFilterProperties must not be null");
        Objects.requireNonNull(trustedProxies, "trustedProxies must not be null");
        this.properties = props;
    }

    public int getOrder() {
        return this.properties.getOrder();
    }

    public HttpHeaders apply(HttpHeaders input, ServerRequest request) {
        String regex = runtimeConfiguration.getTrustedProxiesRegex();
        if (request.servletRequest().getRemoteAddr() != null && !ipResolver.isTrusted(request.servletRequest().getRemoteAddr(), regex)) {
            log.trace(LogMessage.format("Remote address not trusted. pattern %s remote address %s", regex, request.servletRequest().getRemoteHost()));
            return RemoveXForwardedRequestHeadersFilter.removeXForwardedHeaders(input, request);
        } else {
            HttpHeaders updated = new HttpHeaders();

            for(Map.Entry<String, List<String>> entry : input.headerSet()) {
                updated.addAll(entry.getKey(), entry.getValue());
            }

            if (this.properties.isForEnabled()) {
                String remoteAddr = null;
                if (request.servletRequest().getRemoteAddr() != null) {
                    remoteAddr = request.servletRequest().getRemoteAddr();
                }

                boolean var10004 = this.properties.isForAppend();
                TrustedProxies var10005 = TrustedProxies.from(regex);
                Objects.requireNonNull(var10005);
                this.write(updated, "X-Forwarded-For", remoteAddr, var10004, value -> ipResolver.isTrusted(value, regex));
            }

            String proto = request.uri().getScheme();
            if (this.properties.isProtoEnabled()) {
                this.write(updated, "X-Forwarded-Proto", proto, this.properties.isProtoAppend());
            }

            if (this.properties.isPrefixEnabled()) {
                LinkedHashSet<URI> originalUris = (LinkedHashSet)MvcUtils.getAttribute(request, MvcUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
                URI requestUri = request.uri();
                if (originalUris != null && requestUri != null) {
                    originalUris.forEach((originalUri) -> {
                        if (originalUri != null && originalUri.getPath() != null) {
                            String originalUriPath = this.stripTrailingSlash(originalUri);
                            String requestUriPath = this.stripTrailingSlash(requestUri);
                            this.updateRequest(updated, originalUri, originalUriPath, requestUriPath);
                        }

                    });
                }
            }

            if (this.properties.isPortEnabled()) {
                String port = String.valueOf(request.uri().getPort());
                if (request.uri().getPort() < 0) {
                    port = String.valueOf(this.getDefaultPort(proto));
                }

                this.write(updated, "X-Forwarded-Port", port, this.properties.isPortAppend());
            }

            if (this.properties.isHostEnabled()) {
                String host = this.toHostHeader(request);
                this.write(updated, "X-Forwarded-Host", host, this.properties.isHostAppend());
            }

            return updated;
        }
    }



    private void updateRequest(HttpHeaders updated, URI originalUri, String originalUriPath, @Nullable String requestUriPath) {
        if (requestUriPath != null && originalUriPath.endsWith(requestUriPath)) {
            String prefix = substringBeforeLast(originalUriPath, requestUriPath);
            if (prefix != null && prefix.length() > 0 && prefix.length() <= originalUri.getPath().length()) {
                this.write(updated, "X-Forwarded-Prefix", prefix, this.properties.isPrefixAppend());
            }
        }

    }

    private static @Nullable String substringBeforeLast(String str, String separator) {
        if (!ObjectUtils.isEmpty(str) && !ObjectUtils.isEmpty(separator)) {
            int pos = str.lastIndexOf(separator);
            return pos == -1 ? str : str.substring(0, pos);
        } else {
            return str;
        }
    }

    private void write(HttpHeaders headers, String name, @Nullable String value, boolean append) {
        this.write(headers, name, value, append, (s) -> true);
    }

    private void write(HttpHeaders headers, String name, @Nullable String value, boolean append, Predicate<String> shouldWrite) {
        if (append) {
            if (value != null) {
                headers.add(name, value);
            }

            if (headers.containsHeader(name)) {
                List<String> values = headers.get(name);
                if (values != null) {
                    List<String> filteredValues = values.stream().filter(shouldWrite).toList();
                    String delimitedValue = StringUtils.collectionToCommaDelimitedString(filteredValues);
                    headers.set(name, delimitedValue);
                }
            }
        } else if (value != null && shouldWrite.test(value)) {
            headers.set(name, value);
        }

    }

    private int getDefaultPort(String scheme) {
        return "https".equals(scheme) ? 443 : 80;
    }

    private String toHostHeader(ServerRequest request) {
        int port = request.uri().getPort();
        String host = request.uri().getHost();
        String scheme = request.uri().getScheme();
        return port >= 0 && (port != 80 || !"http".equals(scheme)) && (port != 443 || !"https".equals(scheme)) ? host + ":" + port : host;
    }

    private String stripTrailingSlash(URI uri) {
        return uri.getPath().endsWith("/") ? uri.getPath().substring(0, uri.getPath().length() - 1) : uri.getPath();
    }
}
