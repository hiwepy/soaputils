/*
 * SoapUI, Copyright (C) 2004-2017 SmartBear Software
 *
 * Licensed under the EUPL, Version 1.1 or - as soon as they will be approved by the European Commission - subsequent 
 * versions of the EUPL (the "Licence"); 
 * You may not use this work except in compliance with the Licence. 
 * You may obtain a copy of the Licence at: 
 * 
 * http://ec.europa.eu/idabc/eupl 
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is 
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either 
 * express or implied. See the Licence for the specific language governing permissions and limitations 
 * under the Licence. 
 */

package io.github.easy4j.soap;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base implementation of {@link SoapVersion} that provides
 * common envelope validation logic shared by all SOAP version
 * implementations. Subclasses supply the concrete schema loader,
 * envelope type and fault type for their specific SOAP version.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 * @see SoapVersion
 * @see SoapVersion11
 * @see SoapVersion12
 */

public abstract class AbstractSoapVersion implements SoapVersion {
    private final static Logger log = LoggerFactory.getLogger(AbstractSoapVersion.class);

    /**
     * Validates the given SOAP message string against the envelope schema
     * of this SOAP version. Any validation errors found are appended to
     * the supplied error list.
     *
     * @param soapMessage the XML string of the SOAP envelope to validate
     * @param errors      mutable list to which validation errors are appended
     */
    @SuppressWarnings("unchecked")
    public void validateSoapEnvelope(String soapMessage, List<XmlError> errors) {
        List<XmlError> errorList = new ArrayList<XmlError>();

        try {
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setLoadLineNumbers();
            xmlOptions.setValidateTreatLaxAsSkip();
            xmlOptions.setLoadLineNumbers(XmlOptions.LOAD_LINE_NUMBERS_END_ELEMENT);
            XmlObject xmlObject = getSoapEnvelopeSchemaLoader().parse(soapMessage, getEnvelopeType(), xmlOptions);
            xmlOptions.setErrorListener(errorList);
            xmlObject.validate(xmlOptions);
        } catch (XmlException e) {
            if (e.getErrors() != null) {
                errorList.addAll(e.getErrors());
            }

            errors.add(XmlError.forMessage(e.getMessage()));
        } catch (Exception e) {
            errors.add(XmlError.forMessage(e.getMessage()));
        } finally {
            for (XmlError error : errorList) {
                if (error instanceof XmlValidationError && shouldIgnore((XmlValidationError) error)) {
                    log.warn("Ignoring validation error: " + error.toString());
                    continue;
                }

                errors.add(error);
            }
        }
    }

    /**
     * Returns the {@link SchemaTypeLoader} that contains the SOAP envelope
     * schema for this version.
     *
     * @return the schema type loader for envelope validation
     */
    protected abstract SchemaTypeLoader getSoapEnvelopeSchemaLoader();

    /**
     * Determines whether the given XML validation error can be safely
     * ignored for this SOAP version. Errors related to
     * {@code encodingStyle} and {@code mustUnderstand} attributes are
     * ignored because the SOAP specification allows constructions that
     * the XML Schema may not permit.
     *
     * @param error the validation error to evaluate
     * @return {@code true} if the error should be ignored, {@code false} otherwise
     */
    public boolean shouldIgnore(XmlValidationError error) {
        QName offendingQName = error.getOffendingQName();
        if (offendingQName != null) {
            if (offendingQName.equals(new QName(getEnvelopeNamespace(), "encodingStyle"))) {
                return true;
            } else if (offendingQName.equals(new QName(getEnvelopeNamespace(), "mustUnderstand"))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the {@link SchemaType} representing the SOAP Fault element
     * for this version.
     *
     * @return the fault schema type
     */
    public abstract SchemaType getFaultType();

    /**
     * Returns the {@link SchemaType} representing the SOAP Envelope element
     * for this version.
     *
     * @return the envelope schema type
     */
    public abstract SchemaType getEnvelopeType();
}
