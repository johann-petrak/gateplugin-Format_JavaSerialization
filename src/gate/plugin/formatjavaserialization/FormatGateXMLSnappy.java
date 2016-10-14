/*
 * Copyright (c) 2015-2016 The University Of Sheffield.
 *
 * This file is part of gateplugin-Format_JavaSerialization
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */
package gate.plugin.formatjavaserialization;

import gate.Document;
import gate.Resource;
import gate.TextualDocument;
import gate.corpora.DocumentStaxUtils;
import gate.corpora.MimeType;
import gate.corpora.RepositioningInfo;
import gate.corpora.XmlDocumentFormat;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.CreoleResource;
import gate.event.StatusListener;
import gate.util.DocumentFormatException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xerial.snappy.SnappyInputStream;

@CreoleResource(
        name = "Snappy compressed GATE XML format",
        isPrivate = true,
        autoinstances = {
          @AutoInstance(hidden = true)},
        comment = "Support for reading GATE documents Snappy compressed XML format",
        helpURL = "")
public class FormatGateXMLSnappy extends XmlDocumentFormat {

  //private static final long serialVersionUID = -2394353168913311584L;
  /**
   * Default construction
   */
  public FormatGateXMLSnappy() {
    super();
  }

  /**
   * Initialise this resource, and return it.
   */
  @Override
  public Resource init() throws ResourceInstantiationException {
    MimeType mime = new MimeType("text", "gatexml+snappy");
    mimeString2ClassHandlerMap.put(mime.getType() + "/" + mime.getSubtype(), this);
    mimeString2mimeTypeMap.put(mime.getType() + "/" + mime.getSubtype(), mime);
    mimeString2mimeTypeMap.put("application/xml+gate+snappy", mime);
    suffixes2mimeTypeMap.put("gatexml.snappy", mime);
    setMimeType(mime);
    return this;
  }

  protected void unpackMarkupHelper(Document dcmnt)
          throws DocumentFormatException {
    URL sourceURL = dcmnt.getSourceUrl();
    if (sourceURL == null) {
      throw new DocumentFormatException("Cannot create document, no sourceURL");
    }
    String encoding = ((TextualDocument) dcmnt).getEncoding();
    StatusListener statusListener = new StatusListener() {
      @Override
      public void statusChanged(String text) {
        // This is implemented in DocumentFormat.java and inherited here
        fireStatusChanged(text);
      }
    };
    try (
            InputStream urlStream = sourceURL.openStream();
            SnappyInputStream gzipin = new SnappyInputStream(urlStream);
            InputStreamReader inputReader = new InputStreamReader(gzipin, encoding);) {
      XMLStreamReader xsr = getInputFactory().createXMLStreamReader(
              sourceURL.toExternalForm(), inputReader);
      xsr.nextTag();
      DocumentStaxUtils.readGateXmlDocument(xsr, dcmnt, statusListener);
    } catch (Exception ex) {
      throw new DocumentFormatException("Exception when trying to read the document " + sourceURL, ex);
    }

  }

  @Override
  public void unpackMarkup(Document dcmnt) throws DocumentFormatException {
    unpackMarkupHelper(dcmnt);
  }

  @Override
  public void unpackMarkup(Document dcmnt, RepositioningInfo ri, RepositioningInfo ri1) throws DocumentFormatException {
    unpackMarkupHelper(dcmnt);
  }
  // the following would not be necessary if the methods and fields would be
  // protected instead of private in the superclass!
  private static XMLInputFactory staxFactory;

  private static XMLInputFactory getInputFactory() throws XMLStreamException {
    if (staxFactory == null) {
      staxFactory = XMLInputFactory.newInstance();
      staxFactory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
      staxFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
      staxFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
              Boolean.TRUE);
      staxFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
              Boolean.TRUE);
    }
    return staxFactory;
  }

}
