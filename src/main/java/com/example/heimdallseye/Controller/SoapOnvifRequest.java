/*
 * The MIT License
 *
 * Copyright 2016 Maurice Amon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.example.heimdallseye.Controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.example.heimdallseye.Model.entities.IpCameraEntity;
import jakarta.xml.bind.*;
import jakarta.xml.soap.*;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;

/**
 *
 * @author Maurice Amon
 */
public class SoapOnvifRequest {

    private SOAPConnection soapConnection = null;

    private SOAPMessage soapMessage = null;

    private SOAPMessage soapResponse = null;

    private final IpCameraEntity ipCamera;

    public SoapOnvifRequest(IpCameraEntity ipCamera) {
        this.ipCamera = ipCamera;
    }

    public Object createSOAPRequest(Object soapRequestElem, Object soapResponseElem, String soapUri, boolean needsAuthentification) throws SOAPException, ConnectException {
        try {
            soapConnection = SOAPConnectionFactory.newInstance().createConnection();
            soapMessage = createSoapMessage(soapRequestElem, needsAuthentification);

            soapResponse = soapConnection.call(soapMessage, soapUri);
            soapMessage.writeTo(System.out);
            System.out.println();

            soapResponse.writeTo(System.out);
            System.out.println();

            Unmarshaller unmarshaller = JAXBContext.newInstance(soapResponseElem.getClass()).createUnmarshaller();
            try {
                soapResponseElem = unmarshaller.unmarshal(soapResponse.getSOAPBody().extractContentAsDocument());
            } catch (UnmarshalException e) {
                System.out.println(e.getMessage());
            }

            return soapResponseElem;
        } catch (SocketException e) {
            throw new ConnectException(e.getLocalizedMessage());
        } catch (SOAPException e) {
            System.out.println(e.getMessage());
            return null;
        } catch (ParserConfigurationException | IOException | JAXBException e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            try {
                soapConnection.close();
            } catch (SOAPException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    protected SOAPMessage createSoapMessage(Object soapRequestElem, boolean needAuthentification) throws SOAPException, ParserConfigurationException, JAXBException {
        SOAPMessage message = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Marshaller marshaller = JAXBContext.newInstance(soapRequestElem.getClass()).createMarshaller();
        marshaller.marshal(soapRequestElem, document);
        message.getSOAPBody().addDocument(document);

        createSoapHeader(message);

        message.saveChanges();
        return message;
    }

    protected void createSoapHeader(SOAPMessage soapMessage) throws SOAPException {
        SOAPPart sp = soapMessage.getSOAPPart();
        SOAPEnvelope se = sp.getEnvelope();
        SOAPHeader header = soapMessage.getSOAPHeader();
        se.addNamespaceDeclaration("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        se.addNamespaceDeclaration("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");

        SOAPElement securityElem = header.addChildElement("Security", "wsse");
        SOAPElement usernameTokenElem = securityElem.addChildElement("UsernameToken", "wsse");

        SOAPElement usernameElem = usernameTokenElem.addChildElement("Username", "wsse");
        usernameElem.setTextContent(ipCamera.getUsername());

        SOAPElement passwordElem = usernameTokenElem.addChildElement("Password", "wsse");
        passwordElem.setAttribute("Type", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest");
        passwordElem.setTextContent(ipCamera.getEncryptedPassword());

        SOAPElement nonceElem = usernameTokenElem.addChildElement("Nonce", "wsse");
        nonceElem.setAttribute("EncodingType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");
        nonceElem.setTextContent(Base64.encodeBase64String(ipCamera.getNonce().getBytes()));

        SOAPElement createdElem = usernameTokenElem.addChildElement("Created", "wsu");
        createdElem.setTextContent(ipCamera.getUtcTimestamp());
    }
}
