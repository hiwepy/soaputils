package io.github.easy4j.soap;


import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlValidationError;

import javax.xml.namespace.QName;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.util.List;

/**
 * Defines the public contract for a SOAP version implementation.
 * Each version (1.1, 1.2) provides its envelope, body, header and
 * fault QNames, content type, encoding namespace, schema access,
 * validation logic and related metadata.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see SoapVersion11
 * @see SoapVersion12
 * @see AbstractSoapVersion
 */

public interface SoapVersion {
    /** SOAP 1.1 singleton instance. */
    public static final SoapVersion11 Soap11 = SoapVersion11.instance;
    /** SOAP 1.2 singleton instance. */
    public static final SoapVersion12 Soap12 = SoapVersion12.instance;

    /**
     * Returns the qualified name of the SOAP Envelope element.
     * @return the envelope QName
     */
    public QName getEnvelopeQName();

    /**
     * Returns the qualified name of the SOAP Body element.
     * @return the body QName
     */
    public QName getBodyQName();

    /**
     * Returns the qualified name of the SOAP Header element.
     * @return the header QName
     */
    public QName getHeaderQName();

    /**
     * Validates the given SOAP envelope string and appends any errors found.
     * @param soapMessage the XML string of the SOAP envelope
     * @param errors      mutable list to which errors are appended
     */
    public void validateSoapEnvelope(String soapMessage, List<XmlError> errors);

    /**
     * Builds the full HTTP Content-Type header value for this SOAP version.
     * @param encoding   the character encoding (may be {@code null})
     * @param soapAction the SOAPAction value
     * @return the Content-Type header string
     */
    public String getContentTypeHttpHeader(String encoding, String soapAction);

    /**
     * Returns the SOAP envelope namespace URI.
     * @return the envelope namespace URI
     */
    public String getEnvelopeNamespace();

    /**
     * Returns the namespace URI used for SOAP fault detail elements.
     * @return the fault detail namespace
     */
    public String getFaultDetailNamespace();

    /**
     * Returns the SOAP encoding namespace URI.
     * @return the encoding namespace URI
     */
    public String getEncodingNamespace();

    /**
     * Returns the parsed SOAP encoding XML schema.
     * @return the encoding schema as an {@link XmlObject}
     * @throws XmlException if the schema cannot be parsed
     * @throws IOException  if the schema resource cannot be read
     */
    public XmlObject getSoapEncodingSchema() throws XmlException, IOException;

    /**
     * Returns the parsed SOAP envelope XML schema.
     * @return the envelope schema as an {@link XmlObject}
     * @throws XmlException if the schema cannot be parsed
     * @throws IOException  if the schema resource cannot be read
     */
    public XmlObject getSoapEnvelopeSchema() throws XmlException, IOException;

    /**
     * Checks if the specified validation error should be ignored for a message
     * with this SOAP version. (The SOAP-spec may allow some constructions not
     * allowed by the corresponding XML-Schema)
     */

    /**
     * Checks if the specified validation error should be ignored for a message
     * with this SOAP version. (The SOAP-spec may allow some constructions not
     * allowed by the corresponding XML-Schema).
     *
     * @param xmlError the validation error to evaluate
     * @return {@code true} if the error should be ignored
     */
    public boolean shouldIgnore(XmlValidationError xmlError);

    /**
     * Returns the default MIME content type for this SOAP version.
     * @return the content type string (e.g. {@code "text/xml"} for SOAP 1.1)
     */
    public String getContentType();

    /**
     * Returns the schema type for the SOAP Envelope document.
     * @return the envelope schema type
     */
    public SchemaType getEnvelopeType();

    /**
     * Returns the schema type for the SOAP Fault document.
     * @return the fault schema type
     */
    public SchemaType getFaultType();

    /**
     * Returns the human-readable name of this SOAP version.
     * @return the version name (e.g. {@code "SOAP 1.1"})
     */
    public String getName();

    /**
     * Utility methods for {@link SoapVersion} resolution.
     *
     * @author <a href="https://github.com/loong10k">Loong Wan</a>
     * @since 3.0.0
     */

    public static class Utils {
        /**
         * Resolves the {@link SoapVersion} that matches the given HTTP
         * Content-Type header value. Returns the supplied default if no
         * match is found.
         *
         * @param contentType the HTTP Content-Type header value (may be {@code null})
         * @param def         the default version to return if no match is found
         * @return the resolved {@link SoapVersion}, never {@code null}
         */
        public static SoapVersion getSoapVersionForContentType(String contentType, SoapVersion def) {
            if (StringUtils.isEmpty(contentType)) {
                return def;
            }

            SoapVersion soapVersion = contentType.startsWith(SoapVersion.Soap11.getContentType()) ? SoapVersion.Soap11
                    : null;
            soapVersion = soapVersion == null && contentType.startsWith(SoapVersion.Soap12.getContentType()) ? SoapVersion.Soap12
                    : soapVersion;

            return soapVersion == null ? def : soapVersion;
        }
    }

    /**
     * Formats the given SOAP action value as an HTTP SOAPAction header value.
     * @param soapAction the raw SOAP action string (may be {@code null})
     * @return the formatted SOAPAction header value (quoted)
     */
    public String getSoapActionHeader(String soapAction);
}
