package com.yeleman.fondasms;

import android.os.Build;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XmlUtils {

    public static Document parseResponse(String responseBody)
            throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilder xmlBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        byte[] responseBytes;
        if (Build.VERSION.SDK_INT >= 19) {
            responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        } else {
            responseBytes = responseBody.getBytes("UTF-8");
        }
            InputStream responseStream = new ByteArrayInputStream(responseBytes);
        return xmlBuilder.parse(responseStream);
    }

    public static String getElementText(Element element) {
        StringBuilder text = new StringBuilder();
        NodeList childNodes = element.getChildNodes();
        int numChildren = childNodes.getLength();
        for (int j = 0; j < numChildren; j++) {
            text.append(childNodes.item(j).getNodeValue());
        }
        return text.toString();
    }

    public static String getErrorText(Document xml) {
        NodeList errorNodes = xml.getElementsByTagName("error");
        if (errorNodes.getLength() > 0) {
            Element errorElement = (Element) errorNodes.item(0);
            return getElementText(errorElement);
        }
        return null;
    }

    public static List<OutgoingMessage> getMessagesList(Document xml, App app, String defaultTo)
    {
        List<OutgoingMessage> messages = new ArrayList<OutgoingMessage>();

        Element messagesElement = (Element) xml.getElementsByTagName("messages").item(0);
        if (messagesElement != null)
        {
            NodeList messageNodes = messagesElement.getChildNodes();
            int numNodes = messageNodes.getLength();
            for (int i = 0; i < numNodes; i++)
            {
                Element messageElement = (Element) messageNodes.item(i);

                String nodeName = messageElement.getNodeName();

                OutgoingMessage message = OutgoingMessage.newFromMessageType(app, nodeName);

                message.setFrom(app.getPhoneNumber());

                String to = messageElement.getAttribute("to");

                message.setTo("".equals(to) ? defaultTo : to);

                String serverId = messageElement.getAttribute("id");

                message.setServerId("".equals(serverId) ? null : serverId);

                String priorityStr = messageElement.getAttribute("priority");

                if (!priorityStr.equals(""))
                {
                    try
                    {
                        message.setPriority(Integer.parseInt(priorityStr));
                    }
                    catch (NumberFormatException ex)
                    {
                        app.log("Invalid message priority: " + priorityStr);
                    }
                }

                message.setMessageBody(XmlUtils.getElementText(messageElement));

                messages.add(message);
            }
        }
        return messages;
    }
}
