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
package com.example.heimdallseye.Model.entities;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.heimdallseye.Controller.SoapOnvifRequest;
import jakarta.xml.soap.SOAPException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.codec.binary.Base64;

import org.onvif.ver10.media.wsdl.GetProfiles;
import org.onvif.ver10.media.wsdl.GetProfilesResponse;
import org.onvif.ver10.media.wsdl.GetStreamUri;
import org.onvif.ver10.media.wsdl.GetStreamUriResponse;
import org.onvif.ver10.schema.PTZSpeed;
import org.onvif.ver10.schema.PTZVector;
import org.onvif.ver10.schema.Profile;
import org.onvif.ver10.schema.StreamSetup;
import org.onvif.ver10.schema.StreamType;
import org.onvif.ver10.schema.Transport;
import org.onvif.ver10.schema.TransportProtocol;
import org.onvif.ver10.schema.Vector1D;
import org.onvif.ver10.schema.Vector2D;
import org.onvif.ver20.ptz.wsdl.ContinuousMove;
import org.onvif.ver20.ptz.wsdl.ContinuousMoveResponse;
import org.onvif.ver10.device.wsdl.GetCapabilities;
import org.onvif.ver10.device.wsdl.GetCapabilitiesResponse;
import org.onvif.ver20.ptz.wsdl.RelativeMove;
import org.onvif.ver20.ptz.wsdl.RelativeMoveResponse;
import org.onvif.ver20.ptz.wsdl.Stop;
import org.onvif.ver20.ptz.wsdl.StopResponse;
import static org.apache.commons.codec.digest.DigestUtils.sha1;
import org.onvif.ver10.schema.Capabilities;
import org.onvif.ver10.schema.MediaUri;

import static org.apache.commons.codec.digest.DigestUtils.sha1;


import static org.apache.commons.codec.digest.DigestUtils.sha1;


/**
 *
 * @author Maurice Amon
 */
public class IpCameraEntity implements Entity {

    public static final ObservableList<IpCameraEntity> IP_CAMERA_LIST = FXCollections.observableArrayList();

    private Integer id;

    private String deviceUri;

    private String ptzUri = null;

    private final SoapOnvifRequest SOAP;

    private final SimpleStringProperty ipAddress = new SimpleStringProperty();

    private final SimpleIntegerProperty port = new SimpleIntegerProperty();

    private final SimpleStringProperty username = new SimpleStringProperty();

    private Boolean isAvaiable = false;

    private String password;

    private String nonce;

    private String utcTimestamp;

    private PTZVector position;

    private Integer zoom;

    private String streamUri = null;

    private String state = "Offline";

    public IpCameraEntity(Integer id, String ipAddress, Integer port, String username, String password) {
        this.id = id;
        this.ipAddress.set(ipAddress);
        this.port.set(port);
        this.username.set(username);
        this.password = password;
        this.deviceUri = "http://" + ipAddress + ":80/onvif/device_service";
        SOAP = new SoapOnvifRequest(this);
    }

    public Integer getId() {
        return id;
    }

    public static void addIpCamera(IpCameraEntity camera) {
        IP_CAMERA_LIST.add(camera);
    }

    public static void removeIpCamera(IpCameraEntity camera) {
        IP_CAMERA_LIST.remove(camera);
    }

    public Boolean isAvaiable() {
        isAvaiable = true;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddress.get(), port.get()));
            if (!socket.isConnected()) {
                isAvaiable = false;
            }
        } catch (IOException ex) {
            isAvaiable = false;
        }
        if (isAvaiable == false) {
            state = "Offline";
        } else {
            state = "Online";
        }
        return isAvaiable;
    }

    public String getPtzXAddress() {
        if (ptzUri == null) {
            GetCapabilities getCapabilities = new GetCapabilities();
            GetCapabilitiesResponse response = new GetCapabilitiesResponse();

            try {
                response = (GetCapabilitiesResponse) SOAP.createSOAPRequest(getCapabilities, response, deviceUri, false);
                ptzUri = response.getCapabilities().getPTZ().getXAddr();
                Integer start = ptzUri.indexOf("//");
                Integer end = ptzUri.indexOf(":", start+2);
                ptzUri = ptzUri.substring(0, start+2) + ipAddress.get() + ptzUri.substring(end);
                System.out.println(ptzUri);
            } catch (SOAPException | ConnectException ex) {
                Logger.getLogger(IpCameraEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ptzUri;
    }

    public String getStreamURI() {
        if (streamUri == null) {
            StreamSetup setup = new StreamSetup();
            setup.setStream(StreamType.RTP_UNICAST);
            Transport transport = new Transport();
            transport.setProtocol(TransportProtocol.UDP);
            setup.setTransport(transport);
            GetStreamUri request = new GetStreamUri();
            GetStreamUriResponse response = new GetStreamUriResponse();
            request.setProfileToken(getProfiles().get(0).getToken());
            request.setStreamSetup(setup);

            try {
                response = (GetStreamUriResponse) SOAP.createSOAPRequest(request, response, deviceUri, false);
            } catch (SOAPException | ConnectException e) {
                System.out.println(e.getMessage());
                return null;
            }
            streamUri = response.getMediaUri().getUri();
        }
        return streamUri;
    }

    public void setStreamURI(String streamUri) {
        StreamSetup setup = new StreamSetup();
        setup.setStream(StreamType.RTP_UNICAST);
        Transport transport = new Transport();
        transport.setProtocol(TransportProtocol.UDP);
        setup.setTransport(transport);
        GetStreamUri request = new GetStreamUri();
        GetStreamUriResponse response = new GetStreamUriResponse();

        request.setProfileToken(getProfiles().get(0).getToken());
        request.setStreamSetup(setup);
        try {
            response = (GetStreamUriResponse) SOAP.createSOAPRequest(request, response, deviceUri, false);
        } catch (SOAPException | ConnectException e) {
            System.out.println(e.getMessage());
        }
        MediaUri mediaUri = new MediaUri();
        mediaUri.setUri(streamUri);
        response.setMediaUri(mediaUri);
        this.streamUri = streamUri;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress.set(ipAddress);
    }

    public void setPort(Integer port) {
        this.port.set(port);
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIpAddress() {
        return this.ipAddress.get();
    }

    public Integer getPort() {
        return this.port.get();
    }

    public String getUsername() {
        return this.username.get();
    }

    public String getPassword() {
        return this.password;
    }

    public String getState() {
        return this.state;
    }

    public String getNonce() {
        return this.nonce;
    }

    public String getUtcTimestamp() {
        return this.utcTimestamp;
    }

    public Capabilities getServiceCataCapabilities() {
        GetCapabilities request = new GetCapabilities();
        GetCapabilitiesResponse response = new GetCapabilitiesResponse();

        try {
            response = (GetCapabilitiesResponse) SOAP.createSOAPRequest(request, response, deviceUri, true);
        } catch (SOAPException | ConnectException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return response.getCapabilities();
    }

    public String getEncryptedPassword() {
        nonce = Integer.toString(new Random().nextInt());
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss'Z'");
        temp.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        utcTimestamp = temp.format(calendar.getTime());
        String secureCombination = nonce + utcTimestamp + password;
        return Base64.encodeBase64String(sha1(secureCombination));
    }

    public PTZVector getCameraPosition() {
        return this.position;
    }

    public List<Profile> getProfiles() {
        GetProfiles request = new GetProfiles();
        GetProfilesResponse response = new GetProfilesResponse();

        try {
            response = (GetProfilesResponse) SOAP.createSOAPRequest(request, response, deviceUri, true);
        } catch (SOAPException | ConnectException e) {
            System.out.println(e.getMessage());
            return null;
        }

        if (response == null) {
            return null;
        }
        return response.getProfiles();
    }

    public Integer getZoomFactor() {
        return this.zoom;
    }

    public void setAbsolutePosition(PTZVector position) {
        this.position = position;
    }

    public RelativeMoveResponse setRelativeMove(Float moveX, Float moveY) {
        RelativeMove request = new RelativeMove();
        RelativeMoveResponse response = new RelativeMoveResponse();

        Vector2D panTiltVector = new Vector2D();
        panTiltVector.setX(moveX);
        panTiltVector.setY(moveY);
        Vector1D zoomVector = new Vector1D();
        zoomVector.setX(0);

        PTZVector translation = new PTZVector();
        translation.setPanTilt(panTiltVector);
        translation.setZoom(zoomVector);

        request.setProfileToken(getProfiles().get(1).getToken());
        request.setTranslation(translation);
        
        try {
            response = (RelativeMoveResponse) SOAP.createSOAPRequest(request, response, getPtzXAddress(), true);
        } catch (SOAPException | ConnectException ex) {
            Logger.getLogger(IpCameraEntity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    public boolean stopMove() {
        Stop request = new Stop();
        request.setPanTilt(true);
        request.setZoom(true);
        StopResponse response = new StopResponse();

        request.setProfileToken(getProfiles().get(1).getToken());

        try {
            response = (StopResponse) SOAP.createSOAPRequest(request, response, getPtzXAddress(), true);
        } catch (SOAPException | ConnectException ex) {
            Logger.getLogger(IpCameraEntity.class.getName()).log(Level.SEVERE, null, ex);
        }

        return response != null;
    }

    public void setZoomFactor(float zoom) {
        ContinuousMove request = new ContinuousMove();
        ContinuousMoveResponse response = new ContinuousMoveResponse();
        Vector1D zoomFactor = new Vector1D();
        zoomFactor.setX(zoom);
        zoomFactor.setSpace("zoom");
        PTZSpeed ptzSpeed = new PTZSpeed();
        ptzSpeed.setZoom(zoomFactor);
        request.setVelocity(ptzSpeed);

        request.setProfileToken(getProfiles().get(1).getToken());
        try {
            SOAP.createSOAPRequest(request, response, deviceUri, true);
        } catch (SOAPException | ConnectException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void insert(Connection con) {
        Random random = new Random();
        id = Math.abs(random.nextInt(100000000 - 100 + 1) + 100);
        try {
            PreparedStatement stmt = con.prepareStatement("INSERT INTO APP.IPCAMERA VALUES (" + id + ", '" + ipAddress.get() + "', "
                    + port.get() + ", '" + username.get() + "', '" + password + "')");
            stmt.execute();

        } catch (SQLException ex) {
            Logger.getLogger(IpCameraEntity.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void update(Connection con) {
        try {
            PreparedStatement stmt = con.prepareStatement("UPDATE APP.IPCAMERA SET IPADDRESS = '" + ipAddress.get() + "', PORT = " + port.get() + ", USERNAME = '"
                    + username.get() + "', PASSWORD = '" + password + "' WHERE ID = " + id);
            stmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(IpCameraEntity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(Connection con) {
        try {
            PreparedStatement stmt = con.prepareStatement("DELETE FROM APP.IPCAMERA WHERE id = " + id);
            stmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(IpCameraEntity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
